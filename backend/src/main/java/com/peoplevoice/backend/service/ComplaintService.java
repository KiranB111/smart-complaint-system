package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.CitizenResolutionRequest;
import com.peoplevoice.backend.dto.ComplaintRequest;
import com.peoplevoice.backend.dto.ComplaintResponse;
import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintStatus;
import com.peoplevoice.backend.model.NotificationType;
import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import com.peoplevoice.backend.repository.ComplaintRepository;
import com.peoplevoice.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final PriorityService priorityService;
    private final AttachmentService attachmentService;
    private final TimelineService timelineService;
    private final NotificationService notificationService;
    private final RealtimeEventService realtimeEventService;

    public List<ComplaintResponse> findAll(String status, String category) {
        return complaintRepository.search(parseStatus(status), normalize(category)).stream().map(this::toResponse).toList();
    }

    public List<ComplaintResponse> findForCitizen(Long citizenId, String status, String category) {
        return complaintRepository.searchForCitizen(citizenId, parseStatus(status), normalize(category)).stream()
                .map(this::toResponse)
                .toList();
    }

    public ComplaintResponse getById(Long id, Long userId, Role role) {
        Complaint complaint = role == Role.CITIZEN
                ? complaintRepository.findByIdAndCitizenId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"))
                : complaintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
        return toResponse(complaint);
    }

    @Transactional
    public ComplaintResponse create(Long citizenId, ComplaintRequest request) {
        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() -> new EntityNotFoundException("Citizen not found"));

        Complaint complaint = new Complaint();
        complaint.setTitle(request.title());
        complaint.setDescription(request.description());
        complaint.setCategory(request.category());
        complaint.setLocation(request.location());
        complaint.setLatitude(request.latitude());
        complaint.setLongitude(request.longitude());
        complaint.setLocality(request.locality());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setCitizen(citizen);
        complaint.setAssignedOfficer(null);

        PriorityService.PriorityDecision decision = priorityService.calculate(request.category(), request.locality());
        complaint.setPriority(decision.priority());
        complaint.setPriorityReason(decision.reason());

        Complaint saved = complaintRepository.save(complaint);
        timelineService.record(saved, citizen.getName(), Role.CITIZEN, null, ComplaintStatus.OPEN.name(), "Complaint created by citizen");
        notificationService.notifyRole(Role.ADMIN, NotificationType.COMPLAINT_CREATED, "New complaint submitted", saved.getTitle() + " requires assignment");
        publishComplaintUpdate(saved);
        return toResponse(saved);
    }

    @Transactional
    public ComplaintResponse assign(Long complaintId, Long officerId, Long adminId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        User officer = userRepository.findById(officerId)
                .filter(user -> user.getRole() == Role.OFFICER && user.isActive())
                .orElseThrow(() -> new EntityNotFoundException("Officer not found"));

        if (officer.getAvailability() != OfficerAvailability.AVAILABLE) {
            throw new IllegalArgumentException("Officer is not currently available");
        }

        ComplaintStatus previousStatus = complaint.getStatus();
        releaseOfficer(complaint.getAssignedOfficer());
        complaint.setAssignedOfficer(officer);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        officer.setAvailability(OfficerAvailability.BUSY);
        userRepository.save(officer);
        Complaint saved = complaintRepository.save(complaint);
        timelineService.record(saved, admin.getName(), Role.ADMIN, previousStatus.name(), ComplaintStatus.ASSIGNED.name(), "Complaint assigned to " + officer.getName());
        notificationService.notifyUser(officer, NotificationType.COMPLAINT_ASSIGNED, "New complaint assigned", saved.getTitle() + " has been assigned to you");
        notificationService.notifyUser(saved.getCitizen(), NotificationType.STATUS_UPDATED, "Complaint assigned", saved.getTitle() + " is now assigned to " + officer.getName());
        publishComplaintUpdate(saved);
        return toResponse(saved);
    }

    @Transactional
    public ComplaintResponse autoAssign(Long complaintId, Long adminId) {
        List<User> availableOfficers = userRepository.findByRoleAndAvailability(Role.OFFICER, OfficerAvailability.AVAILABLE)
                .stream()
                .filter(User::isActive)
                .toList();
        if (availableOfficers.isEmpty()) {
            throw new IllegalArgumentException("No available officers to assign");
        }
        User selected = availableOfficers.stream()
                .min(Comparator
                        .comparingLong((User officer) -> activeComplaintCount(officer.getId()))
                        .thenComparing((User officer) -> officer.getRatingCount() == 0 ? 0.0 : -((double) officer.getTotalRatingScore() / officer.getRatingCount()))
                        .thenComparing(User::getName))
                .orElseThrow();
        return assign(complaintId, selected.getId(), adminId);
    }

    @Transactional
    public ComplaintResponse update(Long complaintId, Long actorId, Role role, ComplaintRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (role == Role.OFFICER) {
            validateOfficerAccess(complaint, actorId);
        }

        complaint.setTitle(request.title());
        complaint.setDescription(request.description());
        complaint.setCategory(request.category());
        complaint.setLocation(request.location());
        complaint.setLatitude(request.latitude());
        complaint.setLongitude(request.longitude());
        complaint.setLocality(request.locality());

        PriorityService.PriorityDecision decision = priorityService.calculate(request.category(), request.locality());
        complaint.setPriority(decision.priority());
        complaint.setPriorityReason(decision.reason());

        ComplaintStatus previousStatus = complaint.getStatus();
        ComplaintStatus requestedStatus = request.status() == null
                ? complaint.getStatus()
                : ComplaintStatus.valueOf(request.status().toUpperCase());

        if (role == Role.ADMIN && request.assignedOfficerId() != null) {
            User assigned = userRepository.findById(request.assignedOfficerId())
                    .filter(user -> user.getRole() == Role.OFFICER)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned officer not found"));
            if (!assigned.isActive()) {
                throw new IllegalArgumentException("Assigned officer is deactivated");
            }
            complaint.setAssignedOfficer(assigned);
        }

        applyWorkflowUpdate(complaint, requestedStatus, role);
        Complaint saved = complaintRepository.save(complaint);
        if (previousStatus != saved.getStatus()) {
            timelineService.record(saved, actor.getName(), role, previousStatus.name(), saved.getStatus().name(), "Complaint status updated");
        }
        if (saved.getStatus() == ComplaintStatus.PENDING_CITIZEN_CONFIRMATION) {
            notificationService.notifyUser(saved.getCitizen(), NotificationType.CITIZEN_CONFIRMATION_REQUESTED, "Please confirm complaint completion", saved.getTitle() + " is ready for your confirmation");
        } else if (role == Role.OFFICER || role == Role.ADMIN) {
            notificationService.notifyUser(saved.getCitizen(), NotificationType.STATUS_UPDATED, "Complaint status updated", saved.getTitle() + " is now " + saved.getStatus().name());
        }
        publishComplaintUpdate(saved);
        return toResponse(saved);
    }

    @Transactional
    public ComplaintResponse confirmResolution(Long complaintId, Long citizenId, CitizenResolutionRequest request) {
        Complaint complaint = complaintRepository.findByIdAndCitizenId(complaintId, citizenId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));

        if (complaint.getStatus() != ComplaintStatus.PENDING_CITIZEN_CONFIRMATION) {
            throw new IllegalArgumentException("Complaint is not waiting for citizen confirmation");
        }

        ComplaintStatus previousStatus = complaint.getStatus();
        if (Boolean.TRUE.equals(request.resolved())) {
            complaint.setStatus(ComplaintStatus.RESOLVED);
            complaint.setResolvedAt(LocalDateTime.now());
            applyOfficerRating(complaint, request.officerRating());
            releaseOfficer(complaint.getAssignedOfficer());
            notificationService.notifyRole(Role.ADMIN, NotificationType.COMPLAINT_RESOLVED, "Complaint resolved", complaint.getTitle() + " has been resolved by citizen confirmation");
        } else {
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            complaint.setResolvedAt(null);
            if (complaint.getAssignedOfficer() != null) {
                notificationService.notifyUser(complaint.getAssignedOfficer(), NotificationType.STATUS_UPDATED, "Complaint returned to progress", complaint.getTitle() + " was marked still pending by the citizen");
            }
        }

        Complaint saved = complaintRepository.save(complaint);
        timelineService.record(saved, saved.getCitizen().getName(), Role.CITIZEN, previousStatus.name(), saved.getStatus().name(), Boolean.TRUE.equals(request.resolved()) ? "Citizen confirmed resolution" : "Citizen marked complaint as still pending");
        publishComplaintUpdate(saved);
        return toResponse(saved);
    }

    @Transactional
    public ComplaintResponse addAttachment(Long complaintId, Long actorId, Role role, MultipartFile file) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (role == Role.CITIZEN && !complaint.getCitizen().getId().equals(actorId)) {
            throw new IllegalArgumentException("Citizen can only upload files to own complaints");
        }
        if (role == Role.OFFICER) {
            validateOfficerAccess(complaint, actorId);
        }
        String attachmentType = role == Role.OFFICER ? "completion proof" : "complaint evidence";
        attachmentService.store(complaint, file, role);
        timelineService.record(complaint, actor.getName(), role, complaint.getStatus().name(), complaint.getStatus().name(), "Uploaded " + attachmentType + ": " + file.getOriginalFilename());
        notificationService.notifyUser(complaint.getCitizen(), NotificationType.STATUS_UPDATED, "New complaint attachment", "A new " + attachmentType + " file was uploaded for " + complaint.getTitle());
        publishComplaintUpdate(complaint);
        return toResponse(complaint);
    }

    public void delete(Long complaintId) {
        complaintRepository.deleteById(complaintId);
    }

    public double averageResolutionHours() {
        return complaintRepository.findAll().stream()
                .filter(complaint -> complaint.getResolvedAt() != null)
                .mapToLong(complaint -> Duration.between(complaint.getCreatedAt(), complaint.getResolvedAt()).toHours())
                .average()
                .orElse(0.0);
    }

    public ComplaintResponse toResponse(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getCategory(),
                complaint.getLocation(),
                complaint.getLatitude(),
                complaint.getLongitude(),
                complaint.getLocality(),
                complaint.getStatus().name(),
                complaint.getPriority().name(),
                complaint.getPriorityReason(),
                complaint.getCitizen().getId(),
                complaint.getCitizen().getName(),
                complaint.getAssignedOfficer() == null ? null : complaint.getAssignedOfficer().getId(),
                complaint.getAssignedOfficer() == null ? null : complaint.getAssignedOfficer().getName(),
                complaint.getAssignedOfficer() == null ? null : complaint.getAssignedOfficer().getAvailability().name(),
                complaint.getOfficerRating(),
                attachmentService.getForComplaint(complaint.getId()),
                timelineService.getTimeline(complaint.getId()),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt(),
                complaint.getResolvedAt()
        );
    }

    private void validateOfficerAccess(Complaint complaint, Long actorId) {
        if (complaint.getAssignedOfficer() == null || !complaint.getAssignedOfficer().getId().equals(actorId)) {
            throw new IllegalArgumentException("Officer can only update assigned complaints");
        }
    }

    private void applyWorkflowUpdate(Complaint complaint, ComplaintStatus requestedStatus, Role role) {
        if (role == Role.ADMIN) {
            switch (requestedStatus) {
                case OPEN, ASSIGNED, IN_PROGRESS, REJECTED -> complaint.setStatus(requestedStatus);
                case PENDING_CITIZEN_CONFIRMATION, RESOLVED -> {
                    complaint.setStatus(ComplaintStatus.PENDING_CITIZEN_CONFIRMATION);
                    complaint.setResolvedAt(null);
                }
            }
            return;
        }

        if (role == Role.OFFICER) {
            if (requestedStatus == ComplaintStatus.IN_PROGRESS) {
                complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            } else if (requestedStatus == ComplaintStatus.PENDING_CITIZEN_CONFIRMATION
                    || requestedStatus == ComplaintStatus.RESOLVED) {
                complaint.setStatus(ComplaintStatus.PENDING_CITIZEN_CONFIRMATION);
                complaint.setResolvedAt(null);
            } else {
                throw new IllegalArgumentException("Officer can only mark complaint as in progress or ready for citizen confirmation");
            }
        }
    }

    private ComplaintStatus parseStatus(String status) {
        return status == null || status.isBlank() ? null : ComplaintStatus.valueOf(status.toUpperCase());
    }

    private String normalize(String category) {
        return category == null || category.isBlank() ? null : category.trim();
    }

    private void releaseOfficer(User officer) {
        if (officer == null) {
            return;
        }
        officer.setAvailability(officer.isActive() ? OfficerAvailability.AVAILABLE : OfficerAvailability.OFFLINE);
        userRepository.save(officer);
    }

    private void applyOfficerRating(Complaint complaint, Integer officerRating) {
        if (officerRating == null || complaint.getAssignedOfficer() == null || complaint.getOfficerRating() != null) {
            return;
        }
        User officer = complaint.getAssignedOfficer();
        officer.setTotalRatingScore(officer.getTotalRatingScore() + officerRating);
        officer.setRatingCount(officer.getRatingCount() + 1);
        complaint.setOfficerRating(officerRating);
        userRepository.save(officer);
    }

    private long activeComplaintCount(Long officerId) {
        return complaintRepository.countByAssignedOfficerIdAndStatusIn(
                officerId,
                List.of(ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS, ComplaintStatus.PENDING_CITIZEN_CONFIRMATION)
        );
    }

    private void publishComplaintUpdate(Complaint complaint) {
        Map<String, Object> payload = Map.of(
                "complaintId", complaint.getId(),
                "status", complaint.getStatus().name()
        );
        realtimeEventService.publishToUser(complaint.getCitizen().getId(), "complaint", payload);
        if (complaint.getAssignedOfficer() != null) {
            realtimeEventService.publishToUser(complaint.getAssignedOfficer().getId(), "complaint", payload);
        }
        realtimeEventService.publishToRole(Role.ADMIN, "complaint", payload);
    }
}

package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.ComplaintRequest;
import com.peoplevoice.backend.dto.ComplaintResponse;
import com.peoplevoice.backend.dto.CitizenResolutionRequest;
import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintStatus;
import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import com.peoplevoice.backend.repository.ComplaintRepository;
import com.peoplevoice.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final PriorityService priorityService;

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

        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional
    public ComplaintResponse assign(Long complaintId, Long officerId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
        User officer = userRepository.findById(officerId)
                .filter(user -> user.getRole() == Role.OFFICER)
                .orElseThrow(() -> new EntityNotFoundException("Officer not found"));

        if (officer.getAvailability() != OfficerAvailability.AVAILABLE) {
            throw new IllegalArgumentException("Officer is not currently available");
        }

        releaseOfficer(complaint.getAssignedOfficer());
        complaint.setAssignedOfficer(officer);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        officer.setAvailability(OfficerAvailability.BUSY);
        userRepository.save(officer);
        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional
    public ComplaintResponse update(Long complaintId, Long actorId, Role role, ComplaintRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));

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

        ComplaintStatus requestedStatus = request.status() == null
                ? complaint.getStatus()
                : ComplaintStatus.valueOf(request.status().toUpperCase());

        if (role == Role.ADMIN && request.assignedOfficerId() != null) {
            User assigned = userRepository.findById(request.assignedOfficerId())
                    .filter(user -> user.getRole() == Role.OFFICER)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned officer not found"));
            complaint.setAssignedOfficer(assigned);
        }

        applyWorkflowUpdate(complaint, requestedStatus, role);
        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional
    public ComplaintResponse confirmResolution(Long complaintId, Long citizenId, CitizenResolutionRequest request) {
        Complaint complaint = complaintRepository.findByIdAndCitizenId(complaintId, citizenId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));

        if (complaint.getStatus() != ComplaintStatus.PENDING_CITIZEN_CONFIRMATION) {
            throw new IllegalArgumentException("Complaint is not waiting for citizen confirmation");
        }

        if (Boolean.TRUE.equals(request.resolved())) {
            complaint.setStatus(ComplaintStatus.RESOLVED);
            complaint.setResolvedAt(LocalDateTime.now());
            applyOfficerRating(complaint, request.officerRating());
            releaseOfficer(complaint.getAssignedOfficer());
        } else {
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            complaint.setResolvedAt(null);
        }

        return toResponse(complaintRepository.save(complaint));
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
        officer.setAvailability(OfficerAvailability.AVAILABLE);
        userRepository.save(officer);
    }

    private void applyOfficerRating(Complaint complaint, Integer officerRating) {
        if (officerRating == null) {
            return;
        }
        if (complaint.getAssignedOfficer() == null) {
            return;
        }
        if (complaint.getOfficerRating() != null) {
            return;
        }
        User officer = complaint.getAssignedOfficer();
        officer.setTotalRatingScore(officer.getTotalRatingScore() + officerRating);
        officer.setRatingCount(officer.getRatingCount() + 1);
        complaint.setOfficerRating(officerRating);
        userRepository.save(officer);
    }
}

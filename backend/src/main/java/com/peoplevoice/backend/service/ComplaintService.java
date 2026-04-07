package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.ComplaintRequest;
import com.peoplevoice.backend.dto.ComplaintResponse;
import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintPriority;
import com.peoplevoice.backend.model.ComplaintStatus;
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
                : complaintRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
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

        PriorityService.PriorityDecision decision = priorityService.calculate(request.category(), request.locality());
        complaint.setPriority(decision.priority());
        complaint.setPriorityReason(decision.reason());
        complaint.setAssignedOfficer(resolveOfficer(decision.priority(), request.assignedOfficerId()));

        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional
    public ComplaintResponse update(Long complaintId, Long actorId, Role role, ComplaintRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found"));
        if (role == Role.OFFICER && complaint.getAssignedOfficer() != null
                && !complaint.getAssignedOfficer().getId().equals(actorId)) {
            throw new IllegalArgumentException("Officer can only update assigned complaints");
        }

        complaint.setTitle(request.title());
        complaint.setDescription(request.description());
        complaint.setCategory(request.category());
        complaint.setLocation(request.location());
        complaint.setLatitude(request.latitude());
        complaint.setLongitude(request.longitude());
        complaint.setLocality(request.locality());

        ComplaintStatus status = request.status() == null ? complaint.getStatus() : ComplaintStatus.valueOf(request.status());
        complaint.setStatus(status);
        complaint.setAssignedOfficer(resolveOfficer(complaint.getPriority(), request.assignedOfficerId()));

        PriorityService.PriorityDecision decision = priorityService.calculate(request.category(), request.locality());
        complaint.setPriority(decision.priority());
        complaint.setPriorityReason(decision.reason());

        if (status == ComplaintStatus.RESOLVED && complaint.getResolvedAt() == null) {
            complaint.setResolvedAt(LocalDateTime.now());
        } else if (status != ComplaintStatus.RESOLVED) {
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
                complaint.getCreatedAt(),
                complaint.getUpdatedAt(),
                complaint.getResolvedAt()
        );
    }

    private ComplaintStatus parseStatus(String status) {
        return status == null || status.isBlank() ? null : ComplaintStatus.valueOf(status.toUpperCase());
    }

    private String normalize(String category) {
        return category == null || category.isBlank() ? null : category.trim();
    }

    private User resolveOfficer(ComplaintPriority priority, Long officerId) {
        if (officerId != null) {
            return userRepository.findById(officerId)
                    .filter(user -> user.getRole() == Role.OFFICER)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned officer not found"));
        }

        List<User> officers = userRepository.findByRole(Role.OFFICER);
        if (officers.isEmpty()) {
            return null;
        }
        return officers.get(priority.ordinal() % officers.size());
    }
}

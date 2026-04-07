package com.peoplevoice.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ComplaintResponse(
        Long id,
        String title,
        String description,
        String category,
        String location,
        Double latitude,
        Double longitude,
        String locality,
        String status,
        String priority,
        String priorityReason,
        Long citizenId,
        String citizenName,
        Long assignedOfficerId,
        String assignedOfficerName,
        String assignedOfficerAvailability,
        Integer officerRating,
        List<AttachmentResponse> attachments,
        List<TimelineEntryResponse> timeline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt
) {
}

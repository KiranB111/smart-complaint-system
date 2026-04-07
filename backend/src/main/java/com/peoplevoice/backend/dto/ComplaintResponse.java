package com.peoplevoice.backend.dto;

import java.time.LocalDateTime;

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
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt
) {
}

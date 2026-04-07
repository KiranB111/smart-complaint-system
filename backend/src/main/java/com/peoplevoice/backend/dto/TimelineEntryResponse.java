package com.peoplevoice.backend.dto;

import java.time.LocalDateTime;

public record TimelineEntryResponse(
        Long id,
        String actorName,
        String actorRole,
        String fromStatus,
        String toStatus,
        String message,
        LocalDateTime createdAt
) {
}

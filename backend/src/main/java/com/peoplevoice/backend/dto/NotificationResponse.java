package com.peoplevoice.backend.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String title,
        String message,
        boolean isRead,
        String emailStatus,
        String smsStatus,
        LocalDateTime createdAt
) {
}

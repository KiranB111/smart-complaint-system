package com.peoplevoice.backend.dto;

import java.time.LocalDateTime;

public record AttachmentResponse(
        Long id,
        String fileName,
        String url,
        String uploadedByRole,
        String attachmentType,
        LocalDateTime createdAt
) {
}

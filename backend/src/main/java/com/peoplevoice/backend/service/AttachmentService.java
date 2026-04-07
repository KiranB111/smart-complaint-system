package com.peoplevoice.backend.service;

import com.peoplevoice.backend.config.AppProperties;
import com.peoplevoice.backend.dto.AttachmentResponse;
import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintAttachment;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.repository.ComplaintAttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final ComplaintAttachmentRepository attachmentRepository;
    private final AppProperties properties;

    public AttachmentResponse store(Complaint complaint, MultipartFile file, Role role) {
        try {
            Path uploadDir = Path.of(properties.storage().uploadDir()).toAbsolutePath();
            Files.createDirectories(uploadDir);
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path target = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            ComplaintAttachment attachment = new ComplaintAttachment();
            attachment.setComplaint(complaint);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFilePath(target.toString());
            attachment.setUploadedByRole(role.name());
            ComplaintAttachment saved = attachmentRepository.save(attachment);
            return toResponse(saved);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to store file", exception);
        }
    }

    public List<AttachmentResponse> getForComplaint(Long complaintId) {
        return attachmentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Path resolveFile(Long attachmentId) {
        ComplaintAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
        return Path.of(attachment.getFilePath());
    }

    private AttachmentResponse toResponse(ComplaintAttachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                "/api/public/files/" + attachment.getId(),
                attachment.getUploadedByRole(),
                attachment.getCreatedAt()
        );
    }
}

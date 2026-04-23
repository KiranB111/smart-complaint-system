package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.AdminCleanupResponse;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.repository.AppNotificationRepository;
import com.peoplevoice.backend.repository.ComplaintAttachmentRepository;
import com.peoplevoice.backend.repository.ComplaintRepository;
import com.peoplevoice.backend.repository.ComplaintTimelineEntryRepository;
import com.peoplevoice.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCleanupService {

    private final ComplaintAttachmentRepository attachmentRepository;
    private final ComplaintTimelineEntryRepository timelineEntryRepository;
    private final AppNotificationRepository notificationRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Transactional
    public AdminCleanupResponse deleteEverythingExceptAdmins() {
        long attachments = attachmentRepository.count();
        long timelineEntries = timelineEntryRepository.count();
        long notifications = notificationRepository.count();
        long complaints = complaintRepository.count();
        long nonAdminUsers = userRepository.countByRoleNot(Role.ADMIN);

        attachmentRepository.deleteAllInBatch();
        timelineEntryRepository.deleteAllInBatch();
        notificationRepository.deleteAllInBatch();
        complaintRepository.deleteAllInBatch();
        userRepository.deleteByRoleNot(Role.ADMIN);

        return new AdminCleanupResponse(
                attachments,
                timelineEntries,
                notifications,
                complaints,
                nonAdminUsers
        );
    }
}

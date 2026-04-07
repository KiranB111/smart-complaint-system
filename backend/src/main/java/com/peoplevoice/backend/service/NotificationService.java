package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.NotificationResponse;
import com.peoplevoice.backend.model.AppNotification;
import com.peoplevoice.backend.model.NotificationDeliveryStatus;
import com.peoplevoice.backend.model.NotificationType;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import com.peoplevoice.backend.repository.AppNotificationRepository;
import com.peoplevoice.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final AppNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void notifyUser(User recipient, NotificationType type, String title, String message) {
        AppNotification notification = new AppNotification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setEmailStatus(hasValue(recipient.getEmail()) ? NotificationDeliveryStatus.SENT : NotificationDeliveryStatus.SKIPPED);
        notification.setSmsStatus(hasValue(recipient.getPhone()) ? NotificationDeliveryStatus.SENT : NotificationDeliveryStatus.SKIPPED);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyRole(Role role, NotificationType type, String title, String message) {
        userRepository.findByRole(role).forEach(user -> notifyUser(user, type, title, message));
    }

    @Transactional
    public void markRead(Long notificationId, Long userId) {
        AppNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to the current user");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(AppNotification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType().name(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getEmailStatus().name(),
                notification.getSmsStatus().name(),
                notification.getCreatedAt()
        );
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }
}

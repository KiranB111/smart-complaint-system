package com.peoplevoice.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peoplevoice.backend.config.AppProperties;
import com.peoplevoice.backend.model.NotificationDeliveryStatus;
import com.peoplevoice.backend.model.NotificationType;
import com.peoplevoice.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final AppProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    public NotificationDeliveryStatus deliverEmail(User recipient, NotificationType type, String title, String message) {
        return deliver(properties.notifications().email(), recipient.getEmail(), recipient, type, title, message, "EMAIL");
    }

    public NotificationDeliveryStatus deliverSms(User recipient, NotificationType type, String title, String message) {
        return deliver(properties.notifications().sms(), recipient.getPhone(), recipient, type, title, message, "SMS");
    }

    private NotificationDeliveryStatus deliver(
            AppProperties.Channel channel,
            String address,
            User recipient,
            NotificationType type,
            String title,
            String message,
            String channelName
    ) {
        if (channel == null || !channel.enabled() || isBlank(channel.webhookUrl()) || isBlank(address)) {
            return NotificationDeliveryStatus.SKIPPED;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("channel", channelName);
        payload.put("type", type.name());
        payload.put("title", title);
        payload.put("message", message);
        payload.put("recipientName", recipient.getName());
        payload.put("recipientEmail", recipient.getEmail());
        payload.put("recipientPhone", recipient.getPhone());
        payload.put("destination", address);
        payload.put("from", channel.from());

        try {
            restClient.post()
                    .uri(channel.webhookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> applyAuth(headers, channel.authToken()))
                    .body(objectMapper.writeValueAsString(payload))
                    .retrieve()
                    .toBodilessEntity();
            return NotificationDeliveryStatus.SENT;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize notification payload", exception);
        } catch (RuntimeException exception) {
            return NotificationDeliveryStatus.FAILED;
        }
    }

    private void applyAuth(HttpHeaders headers, String authToken) {
        if (!isBlank(authToken)) {
            headers.setBearerAuth(authToken);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

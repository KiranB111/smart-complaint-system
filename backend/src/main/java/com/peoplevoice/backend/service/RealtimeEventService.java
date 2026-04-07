package com.peoplevoice.backend.service;

import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class RealtimeEventService {

    private final UserRepository userRepository;
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(error -> remove(userId, emitter));

        send(userId, emitter, "connected", Map.of(
                "message", "Realtime stream connected",
                "timestamp", Instant.now().toString()
        ));
        return emitter;
    }

    public void publishToUser(Long userId, String eventName, Object payload) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }
        userEmitters.forEach(emitter -> send(userId, emitter, eventName, payload));
    }

    public void publishToRole(Role role, String eventName, Object payload) {
        userRepository.findByRole(role).stream()
                .filter(user -> user.isActive())
                .forEach(user -> publishToUser(user.getId(), eventName, payload));
    }

    private void send(Long userId, SseEmitter emitter, String eventName, Object payload) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(payload));
        } catch (IOException exception) {
            remove(userId, emitter);
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }
        userEmitters.remove(emitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }
}

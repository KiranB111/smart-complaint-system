package com.peoplevoice.backend.controller;

import com.peoplevoice.backend.security.UserPrincipal;
import com.peoplevoice.backend.service.RealtimeEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class RealtimeController {

    private final RealtimeEventService realtimeEventService;

    @GetMapping
    public SseEmitter stream(@AuthenticationPrincipal UserPrincipal principal) {
        return realtimeEventService.subscribe(principal.getUser().getId());
    }
}

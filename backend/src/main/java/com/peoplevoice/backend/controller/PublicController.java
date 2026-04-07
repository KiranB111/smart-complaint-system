package com.peoplevoice.backend.controller;

import com.peoplevoice.backend.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final AttachmentService attachmentService;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "UP", "service", "People Voice API");
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> file(@PathVariable Long id) {
        FileSystemResource resource = new FileSystemResource(attachmentService.resolveFile(id));
        return ResponseEntity.ok(resource);
    }
}

package com.peoplevoice.backend.controller;

import com.peoplevoice.backend.dto.AdminCleanupResponse;
import com.peoplevoice.backend.service.AdminCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/maintenance")
@RequiredArgsConstructor
public class AdminMaintenanceController {

    private final AdminCleanupService adminCleanupService;

    @DeleteMapping("/demo-data")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminCleanupResponse deleteDemoData() {
        return adminCleanupService.deleteEverythingExceptAdmins();
    }
}

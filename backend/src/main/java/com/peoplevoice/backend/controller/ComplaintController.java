package com.peoplevoice.backend.controller;

import com.peoplevoice.backend.dto.ComplaintRequest;
import com.peoplevoice.backend.dto.ComplaintResponse;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.security.UserPrincipal;
import com.peoplevoice.backend.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    public List<ComplaintResponse> getAll(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        if (principal.getUser().getRole() == Role.CITIZEN) {
            return complaintService.findForCitizen(principal.getUser().getId(), status, category);
        }
        return complaintService.findAll(status, category);
    }

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ComplaintResponse create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ComplaintRequest request) {
        return complaintService.create(principal.getUser().getId(), request);
    }

    @GetMapping("/{id}")
    public ComplaintResponse getById(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return complaintService.getById(id, principal.getUser().getId(), principal.getUser().getRole());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ComplaintResponse update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ComplaintRequest request) {
        return complaintService.update(id, principal.getUser().getId(), principal.getUser().getRole(), request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public void delete(@PathVariable Long id) {
        complaintService.delete(id);
    }
}

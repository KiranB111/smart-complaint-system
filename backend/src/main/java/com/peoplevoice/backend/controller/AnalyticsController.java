package com.peoplevoice.backend.controller;

import com.peoplevoice.backend.dto.AnalyticsSummaryResponse;
import com.peoplevoice.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public AnalyticsSummaryResponse reports() {
        return analyticsService.buildSummary();
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<byte[]> exportPdf() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("people-voice-report.pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(analyticsService.exportPdf());
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<byte[]> exportExcel() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("people-voice-report.xlsx").build().toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(analyticsService.exportExcel());
    }
}

package com.peoplevoice.backend.dto;

import java.util.List;
import java.util.Map;

public record AnalyticsSummaryResponse(
        long totalComplaints,
        long openComplaints,
        long inProgressComplaints,
        long resolvedComplaints,
        double averageResolutionHours,
        List<Map<String, Object>> complaintsByCategory,
        List<Map<String, Object>> complaintsByPriority,
        List<Map<String, Object>> complaintsByStatus
) {
}

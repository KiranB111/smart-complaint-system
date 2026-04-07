package com.peoplevoice.backend.service;

import com.peoplevoice.backend.config.AppProperties;
import com.peoplevoice.backend.model.ComplaintPriority;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PriorityService {

    private static final Map<String, Double> LOCALITY_DENSITY = Map.of(
            "bengaluru central", 18000.0,
            "delhi urban", 22000.0,
            "mumbai south", 25000.0,
            "semi urban zone", 6500.0,
            "rural ward", 1800.0
    );

    private final AppProperties properties;

    public PriorityService(AppProperties properties) {
        this.properties = properties;
    }

    public PriorityDecision calculate(String category, String locality) {
        double density = LOCALITY_DENSITY.getOrDefault(
                locality == null ? "" : locality.trim().toLowerCase(),
                5000.0
        );

        ComplaintPriority priority = classifyDensity(density);
        if ("water".equalsIgnoreCase(category) || "sanitation".equalsIgnoreCase(category)) {
            priority = escalate(priority);
        }

        return new PriorityDecision(
                priority,
                "Auto-prioritized using locality density %.0f and category %s".formatted(density, category)
        );
    }

    private ComplaintPriority classifyDensity(double density) {
        if (density >= properties.density().metroThreshold()) {
            return ComplaintPriority.CRITICAL;
        }
        if (density >= properties.density().urbanThreshold()) {
            return ComplaintPriority.HIGH;
        }
        if (density >= properties.density().semiUrbanThreshold()) {
            return ComplaintPriority.MEDIUM;
        }
        return ComplaintPriority.LOW;
    }

    private ComplaintPriority escalate(ComplaintPriority priority) {
        return switch (priority) {
            case LOW -> ComplaintPriority.MEDIUM;
            case MEDIUM -> ComplaintPriority.HIGH;
            case HIGH, CRITICAL -> ComplaintPriority.CRITICAL;
        };
    }

    public record PriorityDecision(ComplaintPriority priority, String reason) {
    }
}

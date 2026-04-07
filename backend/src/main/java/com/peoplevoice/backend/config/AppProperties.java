package com.peoplevoice.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Jwt jwt,
        Density density
) {
    public record Jwt(String secret, long expirationMs, long refreshExpirationMs) {
    }

    public record Density(double metroThreshold, double urbanThreshold, double semiUrbanThreshold) {
    }
}

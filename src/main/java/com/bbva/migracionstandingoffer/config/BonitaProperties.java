package com.bbva.migracionstandingoffer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bonita")
public class BonitaProperties {
    private String baseUrl;
    private String username;
    private String password;
    private String processName = "Standing Offer";
    private String processVersion = "1.5.7";
    private int pollAttempts = 20;
    private long pollDelayMs = 3000L;
}

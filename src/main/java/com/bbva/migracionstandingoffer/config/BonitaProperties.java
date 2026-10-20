package com.bbva.migracionstandingoffer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

    public BonitaProperties() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public int getPollAttempts() {
        return pollAttempts;
    }

    public void setPollAttempts(int pollAttempts) {
        this.pollAttempts = pollAttempts;
    }

    public long getPollDelayMs() {
        return pollDelayMs;
    }

    public void setPollDelayMs(long pollDelayMs) {
        this.pollDelayMs = pollDelayMs;
    }
}

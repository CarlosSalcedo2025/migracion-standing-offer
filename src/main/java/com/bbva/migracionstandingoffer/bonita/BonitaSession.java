package com.bbva.migracionstandingoffer.bonita;

public class BonitaSession {
    private final String apiToken;
    private final String jsessionId;
    private final String userId;

    public BonitaSession(String apiToken, String jsessionId, String userId) {
        this.apiToken = apiToken;
        this.jsessionId = jsessionId;
        this.userId = userId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getJsessionId() {
        return jsessionId;
    }

    public String getUserId() {
        return userId;
    }
}

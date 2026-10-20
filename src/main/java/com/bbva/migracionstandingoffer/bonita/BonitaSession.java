package com.bbva.migracionstandingoffer.bonita;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BonitaSession {
    private final String apiToken;
    private final String jsessionId;
    private final String userId;
}

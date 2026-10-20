package com.bbva.migracionstandingoffer.bonita;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowNodeDto {
    private String id;
    private String name;
    private String displayName;
    private String type;
    private String state;
    private String processId;
    private String rootCaseId;
    private String caseId;

    @JsonProperty("assigned_id")
    private String assignedId;

    public boolean isUserTask() {
        return "USER_TASK".equalsIgnoreCase(this.type) || "HUMAN_TASK".equalsIgnoreCase(this.type);
    }

    public boolean isReady() {
        return "ready".equalsIgnoreCase(this.state);
    }

    public boolean isFailed() {
        return "failed".equalsIgnoreCase(this.state);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(this.state);
    }
}

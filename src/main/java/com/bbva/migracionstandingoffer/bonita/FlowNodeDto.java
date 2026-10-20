package com.bbva.migracionstandingoffer.bonita;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public FlowNodeDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getRootCaseId() {
        return rootCaseId;
    }

    public void setRootCaseId(String rootCaseId) {
        this.rootCaseId = rootCaseId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

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

package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.List;

public class WfProcessInstResponseDto {

    private String processInstId;
    private String processDefId;
    private String processNm;
    private String currentStateDefId;
    private String currentStateNm;
    private String entityType;
    private String entityId;
    private String status;
    private String startedBy;
    private LocalDateTime completedDt;
    private List<WfTaskInstResponseDto> tasks;
    private List<WfTransitionLogResponseDto> logs;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getProcessInstId() { return processInstId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

    public String getProcessNm() { return processNm; }
    public void setProcessNm(String processNm) { this.processNm = processNm; }

    public String getCurrentStateDefId() { return currentStateDefId; }
    public void setCurrentStateDefId(String currentStateDefId) { this.currentStateDefId = currentStateDefId; }

    public String getCurrentStateNm() { return currentStateNm; }
    public void setCurrentStateNm(String currentStateNm) { this.currentStateNm = currentStateNm; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStartedBy() { return startedBy; }
    public void setStartedBy(String startedBy) { this.startedBy = startedBy; }

    public LocalDateTime getCompletedDt() { return completedDt; }
    public void setCompletedDt(LocalDateTime completedDt) { this.completedDt = completedDt; }

    public List<WfTaskInstResponseDto> getTasks() { return tasks; }
    public void setTasks(List<WfTaskInstResponseDto> tasks) { this.tasks = tasks; }

    public List<WfTransitionLogResponseDto> getLogs() { return logs; }
    public void setLogs(List<WfTransitionLogResponseDto> logs) { this.logs = logs; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

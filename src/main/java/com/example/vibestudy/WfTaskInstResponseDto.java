package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.Map;

public class WfTaskInstResponseDto {

    public static WfTaskInstResponseDto from(WfTaskInst e, Map<String, String> stateNmMap) {
        WfTaskInstResponseDto dto = new WfTaskInstResponseDto();
        dto.taskInstId = e.getTaskInstId();
        dto.processInstId = e.getProcessInstId();
        dto.taskTemplateId = e.getTaskTemplateId();
        dto.stateDefId = e.getStateDefId();
        dto.taskNm = e.getTaskNm();
        dto.taskType = e.getTaskType();
        dto.priority = e.getPriority();
        dto.assigneeId = e.getAssigneeId();
        dto.status = e.getStatus();
        dto.result = e.getResult();
        dto.comment = e.getComment();
        dto.completedBy = e.getCompletedBy();
        dto.dueDt = e.getDueDt();
        dto.completedDt = e.getCompletedDt();
        dto.createdBy = e.getCreatedBy();
        dto.createdDt = e.getCreatedDt();
        dto.updatedBy = e.getUpdatedBy();
        dto.updatedDt = e.getUpdatedDt();
        dto.stateNm = stateNmMap.get(e.getStateDefId());
        return dto;
    }

    private String taskInstId;
    private String processInstId;
    private String taskTemplateId;
    private String stateDefId;
    private String stateNm;
    private String taskNm;
    private String taskType;
    private Integer priority;
    private String assigneeId;
    private String status;
    private String result;
    private String comment;
    private String completedBy;
    private LocalDateTime dueDt;
    private LocalDateTime completedDt;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getTaskInstId() { return taskInstId; }
    public void setTaskInstId(String taskInstId) { this.taskInstId = taskInstId; }

    public String getProcessInstId() { return processInstId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }

    public String getTaskTemplateId() { return taskTemplateId; }
    public void setTaskTemplateId(String taskTemplateId) { this.taskTemplateId = taskTemplateId; }

    public String getStateDefId() { return stateDefId; }
    public void setStateDefId(String stateDefId) { this.stateDefId = stateDefId; }

    public String getStateNm() { return stateNm; }
    public void setStateNm(String stateNm) { this.stateNm = stateNm; }

    public String getTaskNm() { return taskNm; }
    public void setTaskNm(String taskNm) { this.taskNm = taskNm; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public LocalDateTime getDueDt() { return dueDt; }
    public void setDueDt(LocalDateTime dueDt) { this.dueDt = dueDt; }

    public LocalDateTime getCompletedDt() { return completedDt; }
    public void setCompletedDt(LocalDateTime completedDt) { this.completedDt = completedDt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

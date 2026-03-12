package com.example.vibestudy;

import java.time.LocalDateTime;

public class WfTaskTemplateResponseDto {

    private String taskTemplateId;
    private String stateDefId;
    private String taskNm;
    private String taskDesc;
    private String assigneeType;
    private String assigneeValue;
    private String taskType;
    private Integer priority;
    private Integer slaHours;
    private Integer sortOrder;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getTaskTemplateId() { return taskTemplateId; }
    public void setTaskTemplateId(String taskTemplateId) { this.taskTemplateId = taskTemplateId; }

    public String getStateDefId() { return stateDefId; }
    public void setStateDefId(String stateDefId) { this.stateDefId = stateDefId; }

    public String getTaskNm() { return taskNm; }
    public void setTaskNm(String taskNm) { this.taskNm = taskNm; }

    public String getTaskDesc() { return taskDesc; }
    public void setTaskDesc(String taskDesc) { this.taskDesc = taskDesc; }

    public String getAssigneeType() { return assigneeType; }
    public void setAssigneeType(String assigneeType) { this.assigneeType = assigneeType; }

    public String getAssigneeValue() { return assigneeValue; }
    public void setAssigneeValue(String assigneeValue) { this.assigneeValue = assigneeValue; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Integer getSlaHours() { return slaHours; }
    public void setSlaHours(Integer slaHours) { this.slaHours = slaHours; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

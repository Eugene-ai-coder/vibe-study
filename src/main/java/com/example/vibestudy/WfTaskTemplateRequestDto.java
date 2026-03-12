package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfTaskTemplateRequestDto {

    @NotBlank(message = "Task명은 필수입니다.")
    private String taskNm;

    private String taskDesc;
    private String assigneeType;
    private String assigneeValue;
    private String taskType;
    private Integer priority;
    private Integer slaHours;
    private Integer sortOrder;

    /* ── System Field ── */
    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

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
}

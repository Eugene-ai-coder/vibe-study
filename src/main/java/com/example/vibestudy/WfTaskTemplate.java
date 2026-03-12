package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_task_template")
public class WfTaskTemplate {

    @Id
    @Column(name = "task_template_id", length = 30, nullable = false)
    private String taskTemplateId;

    @Column(name = "state_def_id", length = 30, nullable = false)
    private String stateDefId;

    @Column(name = "task_nm", length = 100, nullable = false)
    private String taskNm;

    @Column(name = "task_desc", length = 500)
    private String taskDesc;

    @Column(name = "assignee_type", length = 20)
    private String assigneeType;

    @Column(name = "assignee_value", length = 50)
    private String assigneeValue;

    @Column(name = "task_type", length = 20)
    private String taskType;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "sla_hours")
    private Integer slaHours;

    @Column(name = "sort_order")
    private Integer sortOrder;

    /* ── System Fields ───────────────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
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

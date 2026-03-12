package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_task_inst")
public class WfTaskInst {

    @Id
    @Column(name = "task_inst_id", length = 30, nullable = false)
    private String taskInstId;

    @Column(name = "process_inst_id", length = 30, nullable = false)
    private String processInstId;

    @Column(name = "task_template_id", length = 30)
    private String taskTemplateId;

    @Column(name = "state_def_id", length = 30, nullable = false)
    private String stateDefId;

    @Column(name = "task_nm", length = 100, nullable = false)
    private String taskNm;

    @Column(name = "task_type", length = 20)
    private String taskType;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "assignee_id", length = 50)
    private String assigneeId;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "result", length = 50)
    private String result;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "completed_by", length = 50)
    private String completedBy;

    @Column(name = "due_dt")
    private LocalDateTime dueDt;

    @Column(name = "completed_dt")
    private LocalDateTime completedDt;

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

    public String getTaskInstId() { return taskInstId; }
    public void setTaskInstId(String taskInstId) { this.taskInstId = taskInstId; }

    public String getProcessInstId() { return processInstId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }

    public String getTaskTemplateId() { return taskTemplateId; }
    public void setTaskTemplateId(String taskTemplateId) { this.taskTemplateId = taskTemplateId; }

    public String getStateDefId() { return stateDefId; }
    public void setStateDefId(String stateDefId) { this.stateDefId = stateDefId; }

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

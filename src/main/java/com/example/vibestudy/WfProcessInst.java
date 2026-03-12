package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_process_inst")
public class WfProcessInst {

    @Id
    @Column(name = "process_inst_id", length = 30, nullable = false)
    private String processInstId;

    @Column(name = "process_def_id", length = 30, nullable = false)
    private String processDefId;

    @Column(name = "current_state_def_id", length = 30)
    private String currentStateDefId;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id", length = 50)
    private String entityId;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "started_by", length = 50)
    private String startedBy;

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

    public String getProcessInstId() { return processInstId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

    public String getCurrentStateDefId() { return currentStateDefId; }
    public void setCurrentStateDefId(String currentStateDefId) { this.currentStateDefId = currentStateDefId; }

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

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_transition_log")
public class WfTransitionLog {

    @Id
    @Column(name = "transition_log_id", length = 30, nullable = false)
    private String transitionLogId;

    @Column(name = "process_inst_id", length = 30, nullable = false)
    private String processInstId;

    @Column(name = "transition_def_id", length = 30)
    private String transitionDefId;

    @Column(name = "from_state_def_id", length = 30, nullable = false)
    private String fromStateDefId;

    @Column(name = "to_state_def_id", length = 30, nullable = false)
    private String toStateDefId;

    @Column(name = "transitioned_dt", nullable = false)
    private LocalDateTime transitionedDt;

    @Column(name = "remark", length = 500)
    private String remark;

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

    public String getTransitionLogId() { return transitionLogId; }
    public void setTransitionLogId(String transitionLogId) { this.transitionLogId = transitionLogId; }

    public String getProcessInstId() { return processInstId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }

    public String getTransitionDefId() { return transitionDefId; }
    public void setTransitionDefId(String transitionDefId) { this.transitionDefId = transitionDefId; }

    public String getFromStateDefId() { return fromStateDefId; }
    public void setFromStateDefId(String fromStateDefId) { this.fromStateDefId = fromStateDefId; }

    public String getToStateDefId() { return toStateDefId; }
    public void setToStateDefId(String toStateDefId) { this.toStateDefId = toStateDefId; }

    public LocalDateTime getTransitionedDt() { return transitionedDt; }
    public void setTransitionedDt(LocalDateTime transitionedDt) { this.transitionedDt = transitionedDt; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

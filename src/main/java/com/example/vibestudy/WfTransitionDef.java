package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_transition_def")
public class WfTransitionDef {

    @Id
    @Column(name = "transition_def_id", length = 30, nullable = false)
    private String transitionDefId;

    @Column(name = "process_def_id", length = 30, nullable = false)
    private String processDefId;

    @Column(name = "from_state_def_id", length = 30, nullable = false)
    private String fromStateDefId;

    @Column(name = "to_state_def_id", length = 30, nullable = false)
    private String toStateDefId;

    @Column(name = "transition_code", length = 50)
    private String transitionCode;

    @Column(name = "event_nm", length = 100)
    private String eventNm;

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

    public String getTransitionDefId() { return transitionDefId; }
    public void setTransitionDefId(String transitionDefId) { this.transitionDefId = transitionDefId; }

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

    public String getFromStateDefId() { return fromStateDefId; }
    public void setFromStateDefId(String fromStateDefId) { this.fromStateDefId = fromStateDefId; }

    public String getToStateDefId() { return toStateDefId; }
    public void setToStateDefId(String toStateDefId) { this.toStateDefId = toStateDefId; }

    public String getTransitionCode() { return transitionCode; }
    public void setTransitionCode(String transitionCode) { this.transitionCode = transitionCode; }

    public String getEventNm() { return eventNm; }
    public void setEventNm(String eventNm) { this.eventNm = eventNm; }

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

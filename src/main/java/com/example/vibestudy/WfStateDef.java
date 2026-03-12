package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_state_def")
public class WfStateDef {

    @Id
    @Column(name = "state_def_id", length = 30, nullable = false)
    private String stateDefId;

    @Column(name = "process_def_id", length = 30, nullable = false)
    private String processDefId;

    @Column(name = "state_nm", length = 100, nullable = false)
    private String stateNm;

    @Column(name = "state_type", length = 20, nullable = false)
    private String stateType;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "entity_status_cd", length = 50)
    private String entityStatusCd;

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

    public String getStateDefId() { return stateDefId; }
    public void setStateDefId(String stateDefId) { this.stateDefId = stateDefId; }

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

    public String getStateNm() { return stateNm; }
    public void setStateNm(String stateNm) { this.stateNm = stateNm; }

    public String getStateType() { return stateType; }
    public void setStateType(String stateType) { this.stateType = stateType; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getEntityStatusCd() { return entityStatusCd; }
    public void setEntityStatusCd(String entityStatusCd) { this.entityStatusCd = entityStatusCd; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

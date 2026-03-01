package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_subscription")
public class Subscription {

    @Id
    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "subs_nm", length = 100)
    private String subsNm;

    @Column(name = "svc_nm", length = 100)
    private String svcNm;

    @Column(name = "fee_prod_nm", length = 100)
    private String feeProdNm;

    @Column(name = "subs_status_cd", length = 20)
    private String subsStatusCd;

    @Column(name = "subs_dt")
    private LocalDateTime subsDt;

    @Column(name = "chg_dt")
    private LocalDateTime chgDt;

    /* ── System Fields ─────────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getSvcNm() { return svcNm; }
    public void setSvcNm(String svcNm) { this.svcNm = svcNm; }

    public String getFeeProdNm() { return feeProdNm; }
    public void setFeeProdNm(String feeProdNm) { this.feeProdNm = feeProdNm; }

    public String getSubsStatusCd() { return subsStatusCd; }
    public void setSubsStatusCd(String subsStatusCd) { this.subsStatusCd = subsStatusCd; }

    public LocalDateTime getSubsDt() { return subsDt; }
    public void setSubsDt(LocalDateTime subsDt) { this.subsDt = subsDt; }

    public LocalDateTime getChgDt() { return chgDt; }
    public void setChgDt(LocalDateTime chgDt) { this.chgDt = chgDt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

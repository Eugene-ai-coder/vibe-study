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

    @Column(name = "svc_cd", length = 10)
    private String svcCd;

    @Column(name = "fee_prod_cd", length = 10)
    private String feeProdCd;

    @Column(name = "subs_status_cd", length = 20)
    private String subsStatusCd;

    @Column(name = "subs_dt")
    private LocalDateTime subsDt;

    @Column(name = "chg_dt")
    private LocalDateTime chgDt;

    @Column(name = "admin_id", length = 50)
    private String adminId;

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

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getFeeProdCd() { return feeProdCd; }
    public void setFeeProdCd(String feeProdCd) { this.feeProdCd = feeProdCd; }

    public String getSubsStatusCd() { return subsStatusCd; }
    public void setSubsStatusCd(String subsStatusCd) { this.subsStatusCd = subsStatusCd; }

    public LocalDateTime getSubsDt() { return subsDt; }
    public void setSubsDt(LocalDateTime subsDt) { this.subsDt = subsDt; }

    public LocalDateTime getChgDt() { return chgDt; }
    public void setChgDt(LocalDateTime chgDt) { this.chgDt = chgDt; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bill_std")
public class BillStd {

    /* ── Key ─────────────────────────────────────────────────── */
    @Id
    @Column(name = "bill_std_id", length = 20, nullable = false)
    private String billStdId;

    @Column(name = "subs_id", length = 20, nullable = false)
    private String subsId;

    /* ── 등록·유효 제어 ──────────────────────────────────────── */
    @Column(name = "bill_std_reg_dt")
    private LocalDateTime billStdRegDt;

    @Column(name = "svc_cd", length = 10)
    private String svcCd;

    @Column(name = "last_eff_yn", length = 1)
    private String lastEffYn;

    @Column(name = "eff_start_dt")
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt")
    private LocalDateTime effEndDt;

    /* ── 상태 코드 ───────────────────────────────────────────── */
    @Column(name = "std_reg_stat_cd", length = 10)
    private String stdRegStatCd;

    @Column(name = "bill_std_stat_cd", length = 10)
    private String billStdStatCd;

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

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public LocalDateTime getBillStdRegDt() { return billStdRegDt; }
    public void setBillStdRegDt(LocalDateTime billStdRegDt) { this.billStdRegDt = billStdRegDt; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getLastEffYn() { return lastEffYn; }
    public void setLastEffYn(String lastEffYn) { this.lastEffYn = lastEffYn; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public String getBillStdStatCd() { return billStdStatCd; }
    public void setBillStdStatCd(String billStdStatCd) { this.billStdStatCd = billStdStatCd; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

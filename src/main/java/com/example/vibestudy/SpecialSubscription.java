package com.example.vibestudy;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_special_subscription")
public class SpecialSubscription {

    @EmbeddedId
    private SpecialSubscriptionId id;

    @Column(name = "subs_id", length = 20, nullable = false)
    private String subsId;

    @Column(name = "svc_cd", length = 10)
    private String svcCd;

    @Column(name = "eff_end_dt", length = 8)
    private String effEndDt;

    @Column(name = "last_eff_yn", length = 1)
    private String lastEffYn;

    @Column(name = "stat_cd", length = 10)
    private String statCd;

    @Column(name = "cntrc_cap_kmh", precision = 18, scale = 4)
    private BigDecimal cntrcCapKmh;

    @Column(name = "cntrc_amt", precision = 18, scale = 2)
    private BigDecimal cntrcAmt;

    @Column(name = "dsc_rt", precision = 18, scale = 4)
    private BigDecimal dscRt;

    @Column(name = "rmk", length = 500)
    private String rmk;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public SpecialSubscriptionId getId() { return id; }
    public void setId(SpecialSubscriptionId id) { this.id = id; }
    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }
    public String getEffEndDt() { return effEndDt; }
    public void setEffEndDt(String effEndDt) { this.effEndDt = effEndDt; }
    public String getLastEffYn() { return lastEffYn; }
    public void setLastEffYn(String lastEffYn) { this.lastEffYn = lastEffYn; }
    public String getStatCd() { return statCd; }
    public void setStatCd(String statCd) { this.statCd = statCd; }
    public BigDecimal getCntrcCapKmh() { return cntrcCapKmh; }
    public void setCntrcCapKmh(BigDecimal cntrcCapKmh) { this.cntrcCapKmh = cntrcCapKmh; }
    public BigDecimal getCntrcAmt() { return cntrcAmt; }
    public void setCntrcAmt(BigDecimal cntrcAmt) { this.cntrcAmt = cntrcAmt; }
    public BigDecimal getDscRt() { return dscRt; }
    public void setDscRt(BigDecimal dscRt) { this.dscRt = dscRt; }
    public String getRmk() { return rmk; }
    public void setRmk(String rmk) { this.rmk = rmk; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

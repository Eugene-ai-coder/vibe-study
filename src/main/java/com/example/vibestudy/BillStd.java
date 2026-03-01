package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
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

    /* ── 과금 산정 방식 ──────────────────────────────────────── */
    @Column(name = "pwr_met_calc_meth_cd", length = 10)
    private String pwrMetCalcMethCd;

    @Column(name = "uprc_det_meth_cd", length = 10)
    private String uprcDetMethCd;

    @Column(name = "metering_unit_price_amt", precision = 18, scale = 4)
    private BigDecimal meteringUnitPriceAmt;

    @Column(name = "bill_qty", precision = 18, scale = 4)
    private BigDecimal billQty;

    /* ── PUE ─────────────────────────────────────────────────── */
    @Column(name = "pue_det_meth_cd", length = 10)
    private String pueDetMethCd;

    @Column(name = "pue1_rt", precision = 18, scale = 4)
    private BigDecimal pue1Rt;

    @Column(name = "pue2_rt", precision = 18, scale = 4)
    private BigDecimal pue2Rt;

    /* ── 할인·손실 ───────────────────────────────────────────── */
    @Column(name = "first_dsc_rt", precision = 18, scale = 4)
    private BigDecimal firstDscRt;

    @Column(name = "second_dsc_rt", precision = 18, scale = 4)
    private BigDecimal secondDscRt;

    @Column(name = "loss_comp_rt", precision = 18, scale = 4)
    private BigDecimal lossCompRt;

    /* ── 약정·정산 ───────────────────────────────────────────── */
    @Column(name = "cntrc_cap_kmh", precision = 18, scale = 4)
    private BigDecimal cntrcCapKmh;

    @Column(name = "cntrc_amt", precision = 18, scale = 2)
    private BigDecimal cntrcAmt;

    @Column(name = "dsc_amt", precision = 18, scale = 2)
    private BigDecimal dscAmt;

    @Column(name = "daily_unit_price", precision = 18, scale = 4)
    private BigDecimal dailyUnitPrice;

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

    public String getPwrMetCalcMethCd() { return pwrMetCalcMethCd; }
    public void setPwrMetCalcMethCd(String pwrMetCalcMethCd) { this.pwrMetCalcMethCd = pwrMetCalcMethCd; }

    public String getUprcDetMethCd() { return uprcDetMethCd; }
    public void setUprcDetMethCd(String uprcDetMethCd) { this.uprcDetMethCd = uprcDetMethCd; }

    public BigDecimal getMeteringUnitPriceAmt() { return meteringUnitPriceAmt; }
    public void setMeteringUnitPriceAmt(BigDecimal meteringUnitPriceAmt) { this.meteringUnitPriceAmt = meteringUnitPriceAmt; }

    public BigDecimal getBillQty() { return billQty; }
    public void setBillQty(BigDecimal billQty) { this.billQty = billQty; }

    public String getPueDetMethCd() { return pueDetMethCd; }
    public void setPueDetMethCd(String pueDetMethCd) { this.pueDetMethCd = pueDetMethCd; }

    public BigDecimal getPue1Rt() { return pue1Rt; }
    public void setPue1Rt(BigDecimal pue1Rt) { this.pue1Rt = pue1Rt; }

    public BigDecimal getPue2Rt() { return pue2Rt; }
    public void setPue2Rt(BigDecimal pue2Rt) { this.pue2Rt = pue2Rt; }

    public BigDecimal getFirstDscRt() { return firstDscRt; }
    public void setFirstDscRt(BigDecimal firstDscRt) { this.firstDscRt = firstDscRt; }

    public BigDecimal getSecondDscRt() { return secondDscRt; }
    public void setSecondDscRt(BigDecimal secondDscRt) { this.secondDscRt = secondDscRt; }

    public BigDecimal getLossCompRt() { return lossCompRt; }
    public void setLossCompRt(BigDecimal lossCompRt) { this.lossCompRt = lossCompRt; }

    public BigDecimal getCntrcCapKmh() { return cntrcCapKmh; }
    public void setCntrcCapKmh(BigDecimal cntrcCapKmh) { this.cntrcCapKmh = cntrcCapKmh; }

    public BigDecimal getCntrcAmt() { return cntrcAmt; }
    public void setCntrcAmt(BigDecimal cntrcAmt) { this.cntrcAmt = cntrcAmt; }

    public BigDecimal getDscAmt() { return dscAmt; }
    public void setDscAmt(BigDecimal dscAmt) { this.dscAmt = dscAmt; }

    public BigDecimal getDailyUnitPrice() { return dailyUnitPrice; }
    public void setDailyUnitPrice(BigDecimal dailyUnitPrice) { this.dailyUnitPrice = dailyUnitPrice; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

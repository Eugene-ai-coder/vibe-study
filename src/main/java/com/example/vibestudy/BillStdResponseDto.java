package com.example.vibestudy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillStdResponseDto {

    /* ── Key ─────────────────────────────────────────────────── */
    private String billStdId;
    private String subsId;

    /* ── 등록·유효 제어 ──────────────────────────────────────── */
    private LocalDateTime billStdRegDt;
    private String        svcCd;
    private String        lastEffYn;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;

    /* ── 상태 코드 ───────────────────────────────────────────── */
    private String stdRegStatCd;
    private String billStdStatCd;

    /* ── 과금 산정 방식 ──────────────────────────────────────── */
    private String     pwrMetCalcMethCd;
    private String     uprcDetMethCd;
    private BigDecimal meteringUnitPriceAmt;
    private BigDecimal billQty;

    /* ── PUE ─────────────────────────────────────────────────── */
    private String     pueDetMethCd;
    private BigDecimal pue1Rt;
    private BigDecimal pue2Rt;

    /* ── 할인·손실 ───────────────────────────────────────────── */
    private BigDecimal firstDscRt;
    private BigDecimal secondDscRt;
    private BigDecimal lossCompRt;

    /* ── 약정·정산 ───────────────────────────────────────────── */
    private BigDecimal cntrcCapKmh;
    private BigDecimal cntrcAmt;
    private BigDecimal dscAmt;
    private BigDecimal dailyUnitPrice;

    /* ── System Fields ───────────────────────────────────────── */
    private String        createdBy;
    private LocalDateTime createdDt;
    private String        updatedBy;
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

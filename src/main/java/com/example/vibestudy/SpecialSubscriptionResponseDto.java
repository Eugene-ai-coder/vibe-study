package com.example.vibestudy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SpecialSubscriptionResponseDto {
    private String subsBillStdId;
    private String effStartDt;
    private String subsId;
    private String svcCd;
    private String effEndDt;
    private String lastEffYn;
    private String specSubsStatCd;
    private BigDecimal cntrcCapKmh;
    private BigDecimal cntrcAmt;
    private BigDecimal dscRt;
    private String remark;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    public String getSubsBillStdId() { return subsBillStdId; }
    public void setSubsBillStdId(String subsBillStdId) { this.subsBillStdId = subsBillStdId; }
    public String getEffStartDt() { return effStartDt; }
    public void setEffStartDt(String effStartDt) { this.effStartDt = effStartDt; }
    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }
    public String getEffEndDt() { return effEndDt; }
    public void setEffEndDt(String effEndDt) { this.effEndDt = effEndDt; }
    public String getLastEffYn() { return lastEffYn; }
    public void setLastEffYn(String lastEffYn) { this.lastEffYn = lastEffYn; }
    public String getSpecSubsStatCd() { return specSubsStatCd; }
    public void setSpecSubsStatCd(String specSubsStatCd) { this.specSubsStatCd = specSubsStatCd; }
    public BigDecimal getCntrcCapKmh() { return cntrcCapKmh; }
    public void setCntrcCapKmh(BigDecimal cntrcCapKmh) { this.cntrcCapKmh = cntrcCapKmh; }
    public BigDecimal getCntrcAmt() { return cntrcAmt; }
    public void setCntrcAmt(BigDecimal cntrcAmt) { this.cntrcAmt = cntrcAmt; }
    public BigDecimal getDscRt() { return dscRt; }
    public void setDscRt(BigDecimal dscRt) { this.dscRt = dscRt; }
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

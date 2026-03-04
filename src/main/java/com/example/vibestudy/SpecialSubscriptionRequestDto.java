package com.example.vibestudy;

import java.math.BigDecimal;

public class SpecialSubscriptionRequestDto {
    private String subsBillStdId;
    private String effStaDt;
    private String subsId;
    private String svcCd;
    private String effEndDt;
    private String lastEffYn;
    private String statCd;
    private BigDecimal cntrcCapKmh;
    private BigDecimal cntrcAmt;
    private BigDecimal dscRt;
    private String rmk;
    private String createdBy;  // INSERT → created_by, UPDATE → updated_by

    public String getSubsBillStdId() { return subsBillStdId; }
    public void setSubsBillStdId(String subsBillStdId) { this.subsBillStdId = subsBillStdId; }
    public String getEffStaDt() { return effStaDt; }
    public void setEffStaDt(String effStaDt) { this.effStaDt = effStaDt; }
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
}

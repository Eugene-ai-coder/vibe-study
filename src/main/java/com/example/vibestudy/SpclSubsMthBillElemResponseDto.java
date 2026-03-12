package com.example.vibestudy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SpclSubsMthBillElemResponseDto {
    private String spclSubsId;
    private String billMth;
    private String subsId;
    private BigDecimal calcAmt;
    private BigDecimal billAmt;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    public String getSpclSubsId() { return spclSubsId; }
    public void setSpclSubsId(String spclSubsId) { this.spclSubsId = spclSubsId; }
    public String getBillMth() { return billMth; }
    public void setBillMth(String billMth) { this.billMth = billMth; }
    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public BigDecimal getCalcAmt() { return calcAmt; }
    public void setCalcAmt(BigDecimal calcAmt) { this.calcAmt = calcAmt; }
    public BigDecimal getBillAmt() { return billAmt; }
    public void setBillAmt(BigDecimal billAmt) { this.billAmt = billAmt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

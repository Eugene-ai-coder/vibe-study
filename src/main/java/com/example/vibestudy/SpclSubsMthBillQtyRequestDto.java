package com.example.vibestudy;

import java.math.BigDecimal;

public class SpclSubsMthBillQtyRequestDto {
    private String spclSubsId;
    private String useMth;
    private String subsId;
    private String billStdId;
    private BigDecimal pue;
    private String createdBy;  // INSERT -> created_by, UPDATE -> updated_by

    public String getSpclSubsId() { return spclSubsId; }
    public void setSpclSubsId(String spclSubsId) { this.spclSubsId = spclSubsId; }
    public String getUseMth() { return useMth; }
    public void setUseMth(String useMth) { this.useMth = useMth; }
    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }
    public BigDecimal getPue() { return pue; }
    public void setPue(BigDecimal pue) { this.pue = pue; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

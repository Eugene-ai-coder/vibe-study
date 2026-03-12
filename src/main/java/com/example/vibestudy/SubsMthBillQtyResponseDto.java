package com.example.vibestudy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubsMthBillQtyResponseDto {
    private String subsId;
    private String useMth;
    private String billStdId;
    private BigDecimal useQty;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getUseMth() { return useMth; }
    public void setUseMth(String useMth) { this.useMth = useMth; }
    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }
    public BigDecimal getUseQty() { return useQty; }
    public void setUseQty(BigDecimal useQty) { this.useQty = useQty; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

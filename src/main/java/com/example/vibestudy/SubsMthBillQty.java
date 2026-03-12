package com.example.vibestudy;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_subs_mth_bill_qty")
public class SubsMthBillQty {

    @EmbeddedId
    private SubsMthBillQtyId id;

    @Column(name = "bill_std_id", length = 20, nullable = false)
    private String billStdId;

    @Column(name = "use_qty", precision = 18, scale = 4, nullable = false)
    private BigDecimal useQty;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public SubsMthBillQtyId getId() { return id; }
    public void setId(SubsMthBillQtyId id) { this.id = id; }
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

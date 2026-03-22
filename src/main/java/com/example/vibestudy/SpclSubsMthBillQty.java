package com.example.vibestudy;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_spcl_subs_mth_bill_qty")
public class SpclSubsMthBillQty {

    @EmbeddedId
    private SpclSubsMthBillQtyId id;

    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "bill_std_id", length = 25, nullable = false)
    private String billStdId;

    @Column(name = "pue", precision = 10, scale = 4, nullable = false)
    private BigDecimal pue;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public SpclSubsMthBillQtyId getId() { return id; }
    public void setId(SpclSubsMthBillQtyId id) { this.id = id; }
    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }
    public BigDecimal getPue() { return pue; }
    public void setPue(BigDecimal pue) { this.pue = pue; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

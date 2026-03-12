package com.example.vibestudy;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_spcl_subs_mth_bill_elem")
public class SpclSubsMthBillElem {

    @EmbeddedId
    private SpclSubsMthBillElemId id;

    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "calc_amt", precision = 18, scale = 2, nullable = false)
    private BigDecimal calcAmt;

    @Column(name = "bill_amt", precision = 18, scale = 2, nullable = false)
    private BigDecimal billAmt;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public SpclSubsMthBillElemId getId() { return id; }
    public void setId(SpclSubsMthBillElemId id) { this.id = id; }
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

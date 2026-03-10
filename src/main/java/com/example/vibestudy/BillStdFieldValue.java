package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bill_std_field_value")
public class BillStdFieldValue {

    @EmbeddedId
    private BillStdFieldValueId id;

    @Column(name = "field_value", length = 500)
    private String fieldValue;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public BillStdFieldValueId getId() { return id; }
    public void setId(BillStdFieldValueId id) { this.id = id; }
    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

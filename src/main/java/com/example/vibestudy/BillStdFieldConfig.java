package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bill_std_field_config")
public class BillStdFieldConfig {

    @EmbeddedId
    private BillStdFieldConfigId id;

    @Column(name = "field_nm", length = 100, nullable = false)
    private String fieldNm;

    @Column(name = "field_type", length = 10, nullable = false)
    private String fieldType;

    @Column(name = "required_yn", length = 1)
    private String requiredYn;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "common_code", length = 50)
    private String commonCode;

    @Column(name = "default_value", length = 200)
    private String defaultValue;

    @Column(name = "eff_end_dt", length = 8)
    private String effEndDt;

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

    public BillStdFieldConfigId getId() { return id; }
    public void setId(BillStdFieldConfigId id) { this.id = id; }
    public String getFieldNm() { return fieldNm; }
    public void setFieldNm(String fieldNm) { this.fieldNm = fieldNm; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getRequiredYn() { return requiredYn; }
    public void setRequiredYn(String requiredYn) { this.requiredYn = requiredYn; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getCommonCode() { return commonCode; }
    public void setCommonCode(String commonCode) { this.commonCode = commonCode; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    public String getEffEndDt() { return effEndDt; }
    public void setEffEndDt(String effEndDt) { this.effEndDt = effEndDt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

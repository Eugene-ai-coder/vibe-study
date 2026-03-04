package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_common_dtl_code")
public class CommonDtlCode {

    @EmbeddedId
    private CommonDtlCodeId id;

    @Column(name = "common_dtl_code_nm", length = 100)
    private String commonDtlCodeNm;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "eff_start_dt")
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt")
    private LocalDateTime effEndDt;

    @Column(name = "remark", length = 500)
    private String remark;

    /* ── System Fields ── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public CommonDtlCodeId getId() { return id; }
    public void setId(CommonDtlCodeId id) { this.id = id; }
    public String getCommonDtlCodeNm() { return commonDtlCodeNm; }
    public void setCommonDtlCodeNm(String commonDtlCodeNm) { this.commonDtlCodeNm = commonDtlCodeNm; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }
    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }
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

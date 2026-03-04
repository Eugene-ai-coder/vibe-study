package com.example.vibestudy;

import java.time.LocalDateTime;

public class CommonDtlCodeRequestDto {
    private String commonDtlCode;
    private String commonDtlCodeNm;
    private Integer sortOrder;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;
    private String remark;
    private String createdBy;

    public String getCommonDtlCode() { return commonDtlCode; }
    public void setCommonDtlCode(String commonDtlCode) { this.commonDtlCode = commonDtlCode; }
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
}

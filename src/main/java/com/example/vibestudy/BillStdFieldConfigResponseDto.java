package com.example.vibestudy;

import java.time.LocalDateTime;

public class BillStdFieldConfigResponseDto {

    private String svcCd;
    private String fieldCd;
    private String effStartDt;
    private String fieldNm;
    private String fieldType;
    private String requiredYn;
    private Integer sortOrder;
    private String commonCode;
    private String defaultValue;
    private String effEndDt;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }
    public String getFieldCd() { return fieldCd; }
    public void setFieldCd(String fieldCd) { this.fieldCd = fieldCd; }
    public String getEffStartDt() { return effStartDt; }
    public void setEffStartDt(String effStartDt) { this.effStartDt = effStartDt; }
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

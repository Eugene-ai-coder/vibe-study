package com.example.vibestudy;

import java.time.LocalDateTime;

public class CommonCodeRequestDto {
    private String commonCode;
    private String commonCodeNm;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;
    private String remark;
    private String createdBy;

    public String getCommonCode() { return commonCode; }
    public void setCommonCode(String commonCode) { this.commonCode = commonCode; }
    public String getCommonCodeNm() { return commonCodeNm; }
    public void setCommonCodeNm(String commonCodeNm) { this.commonCodeNm = commonCodeNm; }
    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }
    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

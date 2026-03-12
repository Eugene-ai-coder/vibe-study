package com.example.vibestudy;

import java.time.LocalDateTime;

public class BillStdApprvReqResponseDto {

    private String apprvReqId;
    private String billStdId;
    private String subsId;
    private String apprvReqContent;
    private String apprvRemarks;
    private String approverId;
    private String createdBy;
    private LocalDateTime createdDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getApprvReqId() { return apprvReqId; }
    public void setApprvReqId(String apprvReqId) { this.apprvReqId = apprvReqId; }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getApprvReqContent() { return apprvReqContent; }
    public void setApprvReqContent(String apprvReqContent) { this.apprvReqContent = apprvReqContent; }

    public String getApprvRemarks() { return apprvRemarks; }
    public void setApprvRemarks(String apprvRemarks) { this.apprvRemarks = apprvRemarks; }

    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
}

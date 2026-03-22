package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class BillStdApprvReqRequestDto {

    @NotBlank(message = "과금기준신청ID는 필수입니다.")
    private String billStdReqId;

    @NotBlank(message = "가입ID는 필수입니다.")
    private String subsId;

    private String apprvRemarks;

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getBillStdReqId() { return billStdReqId; }
    public void setBillStdReqId(String billStdReqId) { this.billStdReqId = billStdReqId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getApprvRemarks() { return apprvRemarks; }
    public void setApprvRemarks(String apprvRemarks) { this.apprvRemarks = apprvRemarks; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

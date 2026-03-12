package com.example.vibestudy;

import java.time.LocalDateTime;

public class ApprvReqListResponseDto {

    private String subsId;
    private String subsNm;
    private String billStdId;
    private String stdRegStatCd;
    private LocalDateTime billStdRegDt;
    private LocalDateTime apprvReqDt;
    private String apprvReqId;

    public ApprvReqListResponseDto(String subsId, String subsNm, String billStdId,
                                   String stdRegStatCd, LocalDateTime billStdRegDt,
                                   LocalDateTime apprvReqDt, String apprvReqId) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.billStdId = billStdId;
        this.stdRegStatCd = stdRegStatCd;
        this.billStdRegDt = billStdRegDt;
        this.apprvReqDt = apprvReqDt;
        this.apprvReqId = apprvReqId;
    }

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public LocalDateTime getBillStdRegDt() { return billStdRegDt; }
    public void setBillStdRegDt(LocalDateTime billStdRegDt) { this.billStdRegDt = billStdRegDt; }

    public LocalDateTime getApprvReqDt() { return apprvReqDt; }
    public void setApprvReqDt(LocalDateTime apprvReqDt) { this.apprvReqDt = apprvReqDt; }

    public String getApprvReqId() { return apprvReqId; }
    public void setApprvReqId(String apprvReqId) { this.apprvReqId = apprvReqId; }
}

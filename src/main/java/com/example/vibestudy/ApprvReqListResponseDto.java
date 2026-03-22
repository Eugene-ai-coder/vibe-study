package com.example.vibestudy;

import java.time.LocalDateTime;

public class ApprvReqListResponseDto {

    private String subsId;
    private String subsNm;
    private String billStdReqId;
    private String stdRegStatCd;
    private LocalDateTime firstReqDt;
    private LocalDateTime apprvReqDt;
    private String apprvReqId;

    public ApprvReqListResponseDto(String subsId, String subsNm, String billStdReqId,
                                   String stdRegStatCd, LocalDateTime firstReqDt,
                                   LocalDateTime apprvReqDt, String apprvReqId) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.billStdReqId = billStdReqId;
        this.stdRegStatCd = stdRegStatCd;
        this.firstReqDt = firstReqDt;
        this.apprvReqDt = apprvReqDt;
        this.apprvReqId = apprvReqId;
    }

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getBillStdReqId() { return billStdReqId; }
    public void setBillStdReqId(String billStdReqId) { this.billStdReqId = billStdReqId; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public LocalDateTime getFirstReqDt() { return firstReqDt; }
    public void setFirstReqDt(LocalDateTime firstReqDt) { this.firstReqDt = firstReqDt; }

    public LocalDateTime getApprvReqDt() { return apprvReqDt; }
    public void setApprvReqDt(LocalDateTime apprvReqDt) { this.apprvReqDt = apprvReqDt; }

    public String getApprvReqId() { return apprvReqId; }
    public void setApprvReqId(String apprvReqId) { this.apprvReqId = apprvReqId; }
}

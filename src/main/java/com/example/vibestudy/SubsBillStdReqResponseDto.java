package com.example.vibestudy;

import java.time.LocalDateTime;

public class SubsBillStdReqResponseDto {

    private String subsId;
    private String subsNm;
    private String svcCd;
    private String reqTypeCd;
    private String stdRegStatCd;
    private String billStdReqId;
    private LocalDateTime firstReqDt;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;

    public SubsBillStdReqResponseDto(String subsId, String subsNm, String svcCd,
            String reqTypeCd, String stdRegStatCd, String billStdReqId,
            LocalDateTime firstReqDt, LocalDateTime effStartDt, LocalDateTime effEndDt) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.svcCd = svcCd;
        this.reqTypeCd = reqTypeCd;
        this.stdRegStatCd = stdRegStatCd;
        this.billStdReqId = billStdReqId;
        this.firstReqDt = firstReqDt;
        this.effStartDt = effStartDt;
        this.effEndDt = effEndDt;
    }

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getReqTypeCd() { return reqTypeCd; }
    public void setReqTypeCd(String reqTypeCd) { this.reqTypeCd = reqTypeCd; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public String getBillStdReqId() { return billStdReqId; }
    public void setBillStdReqId(String billStdReqId) { this.billStdReqId = billStdReqId; }

    public LocalDateTime getFirstReqDt() { return firstReqDt; }
    public void setFirstReqDt(LocalDateTime firstReqDt) { this.firstReqDt = firstReqDt; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }
}

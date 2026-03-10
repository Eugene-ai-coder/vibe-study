package com.example.vibestudy;

import java.time.LocalDateTime;

public class SubsBillStdResponseDto {
    private String subsId;
    private String subsNm;
    private String subsStatusCd;
    private String billStdId;
    private String billStdNm;
    private String stdRegStatCd;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;

    public SubsBillStdResponseDto(String subsId, String subsNm, String subsStatusCd,
            String billStdId, String billStdNm, String stdRegStatCd,
            LocalDateTime effStartDt, LocalDateTime effEndDt) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.subsStatusCd = subsStatusCd;
        this.billStdId = billStdId;
        this.billStdNm = billStdNm;
        this.stdRegStatCd = stdRegStatCd;
        this.effStartDt = effStartDt;
        this.effEndDt = effEndDt;
    }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getSubsStatusCd() { return subsStatusCd; }
    public void setSubsStatusCd(String subsStatusCd) { this.subsStatusCd = subsStatusCd; }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getBillStdNm() { return billStdNm; }
    public void setBillStdNm(String billStdNm) { this.billStdNm = billStdNm; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }
}

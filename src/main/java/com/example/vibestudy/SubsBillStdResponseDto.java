package com.example.vibestudy;

import java.time.LocalDateTime;

public class SubsBillStdResponseDto {
    private String subsId;
    private String subsNm;
    private String subsStatusCd;
    private String svcCd;
    private String basicProdCd;
    private String billStdId;
    private String billStdNm;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;

    public SubsBillStdResponseDto(String subsId, String subsNm, String subsStatusCd,
            String svcCd, String basicProdCd,
            String billStdId, String billStdNm,
            LocalDateTime effStartDt, LocalDateTime effEndDt) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.subsStatusCd = subsStatusCd;
        this.svcCd = svcCd;
        this.basicProdCd = basicProdCd;
        this.billStdId = billStdId;
        this.billStdNm = billStdNm;
        this.effStartDt = effStartDt;
        this.effEndDt = effEndDt;
    }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getSubsStatusCd() { return subsStatusCd; }
    public void setSubsStatusCd(String subsStatusCd) { this.subsStatusCd = subsStatusCd; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getBasicProdCd() { return basicProdCd; }
    public void setBasicProdCd(String basicProdCd) { this.basicProdCd = basicProdCd; }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getBillStdNm() { return billStdNm; }
    public void setBillStdNm(String billStdNm) { this.billStdNm = billStdNm; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }
}

package com.example.vibestudy;

public class SubscriptionMainListResponseDto {
    private String subsId;
    private String subsNm;
    private String svcCd;
    private String feeProdCd;
    private String mainSubsYn;
    private String mainSubsId;

    public SubscriptionMainListResponseDto() {}

    public SubscriptionMainListResponseDto(String subsId, String subsNm, String svcCd, String feeProdCd,
                                           String mainSubsYn, String mainSubsId) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.svcCd = svcCd;
        this.feeProdCd = feeProdCd;
        this.mainSubsYn = mainSubsYn;
        this.mainSubsId = mainSubsId;
    }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getFeeProdCd() { return feeProdCd; }
    public void setFeeProdCd(String feeProdCd) { this.feeProdCd = feeProdCd; }

    public String getMainSubsYn() { return mainSubsYn; }
    public void setMainSubsYn(String mainSubsYn) { this.mainSubsYn = mainSubsYn; }

    public String getMainSubsId() { return mainSubsId; }
    public void setMainSubsId(String mainSubsId) { this.mainSubsId = mainSubsId; }
}

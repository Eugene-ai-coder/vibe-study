package com.example.vibestudy;

public class SubscriptionMainListResponseDto {
    private String subsId;
    private String svcNm;
    private String feeProdNm;
    private String mainSubsYn;
    private String mainSubsId;

    public SubscriptionMainListResponseDto() {}

    public SubscriptionMainListResponseDto(String subsId, String svcNm, String feeProdNm,
                                           String mainSubsYn, String mainSubsId) {
        this.subsId = subsId;
        this.svcNm = svcNm;
        this.feeProdNm = feeProdNm;
        this.mainSubsYn = mainSubsYn;
        this.mainSubsId = mainSubsId;
    }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSvcNm() { return svcNm; }
    public void setSvcNm(String svcNm) { this.svcNm = svcNm; }

    public String getFeeProdNm() { return feeProdNm; }
    public void setFeeProdNm(String feeProdNm) { this.feeProdNm = feeProdNm; }

    public String getMainSubsYn() { return mainSubsYn; }
    public void setMainSubsYn(String mainSubsYn) { this.mainSubsYn = mainSubsYn; }

    public String getMainSubsId() { return mainSubsId; }
    public void setMainSubsId(String mainSubsId) { this.mainSubsId = mainSubsId; }
}

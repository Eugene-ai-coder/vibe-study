package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class SubscriptionRequestDto {

    @NotBlank(message = "가입ID는 필수입니다.")
    private String subsId;

    private String subsNm;
    private String svcNm;
    private String feeProdNm;
    private String subsStatusCd;
    private LocalDateTime subsDt;
    private LocalDateTime chgDt;

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSubsNm() { return subsNm; }
    public void setSubsNm(String subsNm) { this.subsNm = subsNm; }

    public String getSvcNm() { return svcNm; }
    public void setSvcNm(String svcNm) { this.svcNm = svcNm; }

    public String getFeeProdNm() { return feeProdNm; }
    public void setFeeProdNm(String feeProdNm) { this.feeProdNm = feeProdNm; }

    public String getSubsStatusCd() { return subsStatusCd; }
    public void setSubsStatusCd(String subsStatusCd) { this.subsStatusCd = subsStatusCd; }

    public LocalDateTime getSubsDt() { return subsDt; }
    public void setSubsDt(LocalDateTime subsDt) { this.subsDt = subsDt; }

    public LocalDateTime getChgDt() { return chgDt; }
    public void setChgDt(LocalDateTime chgDt) { this.chgDt = chgDt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

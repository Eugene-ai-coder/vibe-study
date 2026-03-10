package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class SubscriptionRequestDto {

    @NotBlank(message = "가입ID는 필수입니다.")
    private String subsId;

    private String subsNm;
    private String svcCd;
    private String feeProdCd;
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

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getFeeProdCd() { return feeProdCd; }
    public void setFeeProdCd(String feeProdCd) { this.feeProdCd = feeProdCd; }

    public String getSubsStatusCd() { return subsStatusCd; }
    public void setSubsStatusCd(String subsStatusCd) { this.subsStatusCd = subsStatusCd; }

    public LocalDateTime getSubsDt() { return subsDt; }
    public void setSubsDt(LocalDateTime subsDt) { this.subsDt = subsDt; }

    public LocalDateTime getChgDt() { return chgDt; }
    public void setChgDt(LocalDateTime chgDt) { this.chgDt = chgDt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

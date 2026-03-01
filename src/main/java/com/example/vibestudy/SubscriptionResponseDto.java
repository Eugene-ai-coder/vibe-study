package com.example.vibestudy;

import java.time.LocalDateTime;

public class SubscriptionResponseDto {

    private String        subsId;
    private String        subsNm;
    private String        svcNm;
    private String        feeProdNm;
    private String        subsStatusCd;
    private LocalDateTime subsDt;
    private LocalDateTime chgDt;
    private String        createdBy;
    private LocalDateTime createdDt;
    private String        updatedBy;
    private LocalDateTime updatedDt;

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

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.Map;

public class BillStdReqResponseDto {

    private Long billStdReqSeq;
    private String billStdReqId;
    private LocalDateTime firstReqDt;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;
    private String reqTypeCd;
    private String stdRegStatCd;
    private String billStdId;
    private String subsId;
    private String svcCd;
    private String basicProdCd;
    private Map<String, String> fieldValues;

    /* ── System Fields ───────────────────────────────────────── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public Long getBillStdReqSeq() { return billStdReqSeq; }
    public void setBillStdReqSeq(Long billStdReqSeq) { this.billStdReqSeq = billStdReqSeq; }

    public String getBillStdReqId() { return billStdReqId; }
    public void setBillStdReqId(String billStdReqId) { this.billStdReqId = billStdReqId; }

    public LocalDateTime getFirstReqDt() { return firstReqDt; }
    public void setFirstReqDt(LocalDateTime firstReqDt) { this.firstReqDt = firstReqDt; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }

    public String getReqTypeCd() { return reqTypeCd; }
    public void setReqTypeCd(String reqTypeCd) { this.reqTypeCd = reqTypeCd; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getBasicProdCd() { return basicProdCd; }
    public void setBasicProdCd(String basicProdCd) { this.basicProdCd = basicProdCd; }

    public Map<String, String> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, String> fieldValues) { this.fieldValues = fieldValues; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

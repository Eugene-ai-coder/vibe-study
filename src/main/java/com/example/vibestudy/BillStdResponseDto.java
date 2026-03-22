package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.Map;

public class BillStdResponseDto {

    /* ── Key ─────────────────────────────────────────────────── */
    private String billStdId;
    private String subsId;

    /* ── 등록·유효 제어 ──────────────────────────────────────── */
    private LocalDateTime billStdRegDt;
    private String        svcCd;
    private String        basicProdCd;
    private String        lastEffYn;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;

    /* ── 상태 코드 ───────────────────────────────────────────── */
    private String billStdStatCd;

    /* ── 동적 필드값 ──────────────────────────────────────────── */
    private Map<String, String> fieldValues;

    /* ── System Fields ───────────────────────────────────────── */
    private String        createdBy;
    private LocalDateTime createdDt;
    private String        updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public LocalDateTime getBillStdRegDt() { return billStdRegDt; }
    public void setBillStdRegDt(LocalDateTime billStdRegDt) { this.billStdRegDt = billStdRegDt; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getBasicProdCd() { return basicProdCd; }
    public void setBasicProdCd(String basicProdCd) { this.basicProdCd = basicProdCd; }

    public String getLastEffYn() { return lastEffYn; }
    public void setLastEffYn(String lastEffYn) { this.lastEffYn = lastEffYn; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }

    public String getBillStdStatCd() { return billStdStatCd; }
    public void setBillStdStatCd(String billStdStatCd) { this.billStdStatCd = billStdStatCd; }

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

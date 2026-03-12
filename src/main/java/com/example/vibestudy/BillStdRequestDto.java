package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

public class BillStdRequestDto {

    /* ── POST 필수 / PUT 무시 ─────────────────────────────────── */
    @NotBlank(message = "가입ID는 필수입니다.")
    private String subsId;

    /* ── 등록·유효 제어 ──────────────────────────────────────── */
    private LocalDateTime billStdRegDt;
    private String        svcCd;
    private String        lastEffYn;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;

    /* ── 상태 코드 ───────────────────────────────────────────── */
    private String billStdStatCd;

    /* ── 동적 필드값 ──────────────────────────────────────────── */
    private Map<String, String> fieldValues;

    /* ── System Field ────────────────────────────────────────── */
    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public LocalDateTime getBillStdRegDt() { return billStdRegDt; }
    public void setBillStdRegDt(LocalDateTime billStdRegDt) { this.billStdRegDt = billStdRegDt; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

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
}

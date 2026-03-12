package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class BillStdReqRequestDto {

    @NotBlank(message = "가입ID는 필수입니다.")
    private String subsId;

    @NotBlank(message = "서비스코드는 필수입니다.")
    private String svcCd;

    @NotBlank(message = "신청구분은 필수입니다.")
    private String reqTypeCd;

    private String stdRegStatCd;

    private String billStdId;

    private Map<String, String> fieldValues;

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getReqTypeCd() { return reqTypeCd; }
    public void setReqTypeCd(String reqTypeCd) { this.reqTypeCd = reqTypeCd; }

    public String getStdRegStatCd() { return stdRegStatCd; }
    public void setStdRegStatCd(String stdRegStatCd) { this.stdRegStatCd = stdRegStatCd; }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }

    public Map<String, String> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, String> fieldValues) { this.fieldValues = fieldValues; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

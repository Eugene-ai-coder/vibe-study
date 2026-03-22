package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bill_std_apprv_req")
public class BillStdApprvReq {

    @Id
    @Column(name = "apprv_req_id", length = 25, nullable = false)
    private String apprvReqId;

    @Column(name = "bill_std_req_id", length = 25, nullable = false)
    private String billStdReqId;

    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "apprv_req_content", columnDefinition = "TEXT", nullable = false)
    private String apprvReqContent;

    @Column(name = "apprv_remarks", length = 500)
    private String apprvRemarks;

    @Column(name = "approver_id", length = 50)
    private String approverId;

    /* ── System Fields ───────────────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getApprvReqId() { return apprvReqId; }
    public void setApprvReqId(String apprvReqId) { this.apprvReqId = apprvReqId; }

    public String getBillStdReqId() { return billStdReqId; }
    public void setBillStdReqId(String billStdReqId) { this.billStdReqId = billStdReqId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getApprvReqContent() { return apprvReqContent; }
    public void setApprvReqContent(String apprvReqContent) { this.apprvReqContent = apprvReqContent; }

    public String getApprvRemarks() { return apprvRemarks; }
    public void setApprvRemarks(String apprvRemarks) { this.apprvRemarks = apprvRemarks; }

    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bill_std_req")
public class BillStdReq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_std_req_seq")
    private Long billStdReqSeq;

    @Column(name = "bill_std_req_id", length = 25, nullable = false)
    private String billStdReqId;

    @Column(name = "first_req_dt", nullable = false)
    private LocalDateTime firstReqDt;

    @Column(name = "eff_start_dt", nullable = false)
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt", nullable = false)
    private LocalDateTime effEndDt;

    @Column(name = "req_type_cd", length = 20, nullable = false)
    private String reqTypeCd;

    @Column(name = "std_reg_stat_cd", length = 20, nullable = false)
    private String stdRegStatCd;

    @Column(name = "bill_std_id", length = 25, nullable = false)
    private String billStdId;

    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "svc_cd", length = 20, nullable = false)
    private String svcCd;

    @Column(name = "basic_prod_cd", length = 20)
    private String basicProdCd;

    /* ── Optimistic Lock ───────────────────────────────────────── */
    @Version
    @Column(name = "version")
    private Long version;

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

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

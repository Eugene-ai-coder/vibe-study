package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BillStdReqFieldValueId implements Serializable {

    @Column(name = "bill_std_req_seq")
    private Long billStdReqSeq;

    @Column(name = "field_cd")
    private String fieldCd;

    public BillStdReqFieldValueId() {}

    public BillStdReqFieldValueId(Long billStdReqSeq, String fieldCd) {
        this.billStdReqSeq = billStdReqSeq;
        this.fieldCd = fieldCd;
    }

    public Long getBillStdReqSeq() { return billStdReqSeq; }
    public void setBillStdReqSeq(Long billStdReqSeq) { this.billStdReqSeq = billStdReqSeq; }

    public String getFieldCd() { return fieldCd; }
    public void setFieldCd(String fieldCd) { this.fieldCd = fieldCd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillStdReqFieldValueId)) return false;
        BillStdReqFieldValueId that = (BillStdReqFieldValueId) o;
        return Objects.equals(billStdReqSeq, that.billStdReqSeq) && Objects.equals(fieldCd, that.fieldCd);
    }

    @Override
    public int hashCode() { return Objects.hash(billStdReqSeq, fieldCd); }
}

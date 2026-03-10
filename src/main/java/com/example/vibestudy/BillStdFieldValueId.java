package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BillStdFieldValueId implements Serializable {
    private String billStdId;
    private String fieldCd;

    public BillStdFieldValueId() {}
    public BillStdFieldValueId(String billStdId, String fieldCd) {
        this.billStdId = billStdId;
        this.fieldCd = fieldCd;
    }

    public String getBillStdId() { return billStdId; }
    public void setBillStdId(String billStdId) { this.billStdId = billStdId; }
    public String getFieldCd() { return fieldCd; }
    public void setFieldCd(String fieldCd) { this.fieldCd = fieldCd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillStdFieldValueId)) return false;
        BillStdFieldValueId that = (BillStdFieldValueId) o;
        return Objects.equals(billStdId, that.billStdId) && Objects.equals(fieldCd, that.fieldCd);
    }

    @Override
    public int hashCode() { return Objects.hash(billStdId, fieldCd); }
}

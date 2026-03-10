package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BillStdFieldConfigId implements Serializable {
    private String svcCd;
    private String fieldCd;
    private String effStartDt;

    public BillStdFieldConfigId() {}
    public BillStdFieldConfigId(String svcCd, String fieldCd, String effStartDt) {
        this.svcCd = svcCd;
        this.fieldCd = fieldCd;
        this.effStartDt = effStartDt;
    }

    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }
    public String getFieldCd() { return fieldCd; }
    public void setFieldCd(String fieldCd) { this.fieldCd = fieldCd; }
    public String getEffStartDt() { return effStartDt; }
    public void setEffStartDt(String effStartDt) { this.effStartDt = effStartDt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillStdFieldConfigId)) return false;
        BillStdFieldConfigId that = (BillStdFieldConfigId) o;
        return Objects.equals(svcCd, that.svcCd)
                && Objects.equals(fieldCd, that.fieldCd)
                && Objects.equals(effStartDt, that.effStartDt);
    }

    @Override
    public int hashCode() { return Objects.hash(svcCd, fieldCd, effStartDt); }
}

package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SpclSubsMthBillElemId implements Serializable {

    @Column(name = "spcl_subs_id")
    private String spclSubsId;

    @Column(name = "bill_mth")
    private String billMth;

    public SpclSubsMthBillElemId() {}

    public SpclSubsMthBillElemId(String spclSubsId, String billMth) {
        this.spclSubsId = spclSubsId;
        this.billMth = billMth;
    }

    public String getSpclSubsId() { return spclSubsId; }
    public void setSpclSubsId(String spclSubsId) { this.spclSubsId = spclSubsId; }
    public String getBillMth() { return billMth; }
    public void setBillMth(String billMth) { this.billMth = billMth; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpclSubsMthBillElemId)) return false;
        SpclSubsMthBillElemId that = (SpclSubsMthBillElemId) o;
        return Objects.equals(spclSubsId, that.spclSubsId) && Objects.equals(billMth, that.billMth);
    }

    @Override
    public int hashCode() { return Objects.hash(spclSubsId, billMth); }
}

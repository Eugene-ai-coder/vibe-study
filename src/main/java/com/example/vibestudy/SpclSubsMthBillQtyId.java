package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SpclSubsMthBillQtyId implements Serializable {

    @Column(name = "spcl_subs_id")
    private String spclSubsId;

    @Column(name = "use_mth")
    private String useMth;

    public SpclSubsMthBillQtyId() {}

    public SpclSubsMthBillQtyId(String spclSubsId, String useMth) {
        this.spclSubsId = spclSubsId;
        this.useMth = useMth;
    }

    public String getSpclSubsId() { return spclSubsId; }
    public void setSpclSubsId(String spclSubsId) { this.spclSubsId = spclSubsId; }
    public String getUseMth() { return useMth; }
    public void setUseMth(String useMth) { this.useMth = useMth; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpclSubsMthBillQtyId)) return false;
        SpclSubsMthBillQtyId that = (SpclSubsMthBillQtyId) o;
        return Objects.equals(spclSubsId, that.spclSubsId) && Objects.equals(useMth, that.useMth);
    }

    @Override
    public int hashCode() { return Objects.hash(spclSubsId, useMth); }
}

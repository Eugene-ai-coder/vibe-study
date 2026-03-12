package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubsMthBillQtyId implements Serializable {

    @Column(name = "subs_id")
    private String subsId;

    @Column(name = "use_mth")
    private String useMth;

    public SubsMthBillQtyId() {}

    public SubsMthBillQtyId(String subsId, String useMth) {
        this.subsId = subsId;
        this.useMth = useMth;
    }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getUseMth() { return useMth; }
    public void setUseMth(String useMth) { this.useMth = useMth; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubsMthBillQtyId)) return false;
        SubsMthBillQtyId that = (SubsMthBillQtyId) o;
        return Objects.equals(subsId, that.subsId) && Objects.equals(useMth, that.useMth);
    }

    @Override
    public int hashCode() { return Objects.hash(subsId, useMth); }
}

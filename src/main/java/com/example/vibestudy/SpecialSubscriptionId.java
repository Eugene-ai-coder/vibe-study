package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SpecialSubscriptionId implements Serializable {
    private String subsBillStdId;
    private String effStaDt;

    public SpecialSubscriptionId() {}
    public SpecialSubscriptionId(String subsBillStdId, String effStaDt) {
        this.subsBillStdId = subsBillStdId;
        this.effStaDt = effStaDt;
    }

    public String getSubsBillStdId() { return subsBillStdId; }
    public void setSubsBillStdId(String subsBillStdId) { this.subsBillStdId = subsBillStdId; }
    public String getEffStaDt() { return effStaDt; }
    public void setEffStaDt(String effStaDt) { this.effStaDt = effStaDt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecialSubscriptionId)) return false;
        SpecialSubscriptionId that = (SpecialSubscriptionId) o;
        return Objects.equals(subsBillStdId, that.subsBillStdId) && Objects.equals(effStaDt, that.effStaDt);
    }

    @Override
    public int hashCode() { return Objects.hash(subsBillStdId, effStaDt); }
}

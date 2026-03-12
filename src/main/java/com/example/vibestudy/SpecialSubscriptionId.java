package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SpecialSubscriptionId implements Serializable {
    private String subsBillStdId;
    private String effStartDt;

    public SpecialSubscriptionId() {}
    public SpecialSubscriptionId(String subsBillStdId, String effStartDt) {
        this.subsBillStdId = subsBillStdId;
        this.effStartDt = effStartDt;
    }

    public String getSubsBillStdId() { return subsBillStdId; }
    public void setSubsBillStdId(String subsBillStdId) { this.subsBillStdId = subsBillStdId; }
    public String getEffStartDt() { return effStartDt; }
    public void setEffStartDt(String effStartDt) { this.effStartDt = effStartDt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecialSubscriptionId)) return false;
        SpecialSubscriptionId that = (SpecialSubscriptionId) o;
        return Objects.equals(subsBillStdId, that.subsBillStdId) && Objects.equals(effStartDt, that.effStartDt);
    }

    @Override
    public int hashCode() { return Objects.hash(subsBillStdId, effStartDt); }
}

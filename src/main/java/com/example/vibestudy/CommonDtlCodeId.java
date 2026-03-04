package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommonDtlCodeId implements Serializable {
    private String commonCode;
    private String commonDtlCode;

    public CommonDtlCodeId() {}
    public CommonDtlCodeId(String commonCode, String commonDtlCode) {
        this.commonCode = commonCode;
        this.commonDtlCode = commonDtlCode;
    }

    public String getCommonCode() { return commonCode; }
    public void setCommonCode(String commonCode) { this.commonCode = commonCode; }
    public String getCommonDtlCode() { return commonDtlCode; }
    public void setCommonDtlCode(String commonDtlCode) { this.commonDtlCode = commonDtlCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonDtlCodeId)) return false;
        CommonDtlCodeId that = (CommonDtlCodeId) o;
        return Objects.equals(commonCode, that.commonCode) && Objects.equals(commonDtlCode, that.commonDtlCode);
    }

    @Override
    public int hashCode() { return Objects.hash(commonCode, commonDtlCode); }
}

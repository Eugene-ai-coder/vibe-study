package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfStateDefRequestDto {

    @NotBlank(message = "상태명은 필수입니다.")
    private String stateNm;

    @NotBlank(message = "상태유형은 필수입니다.")
    private String stateType;

    private Integer sortOrder;

    private String entityStatusCd;

    /* ── System Field ── */
    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getStateNm() { return stateNm; }
    public void setStateNm(String stateNm) { this.stateNm = stateNm; }

    public String getStateType() { return stateType; }
    public void setStateType(String stateType) { this.stateType = stateType; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getEntityStatusCd() { return entityStatusCd; }
    public void setEntityStatusCd(String entityStatusCd) { this.entityStatusCd = entityStatusCd; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

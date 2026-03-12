package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfTransitionDefRequestDto {

    @NotBlank(message = "출발상태ID는 필수입니다.")
    private String fromStateDefId;

    @NotBlank(message = "도착상태ID는 필수입니다.")
    private String toStateDefId;

    private String transitionCode;
    private String eventNm;
    private Integer sortOrder;

    /* ── System Field ── */
    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getFromStateDefId() { return fromStateDefId; }
    public void setFromStateDefId(String fromStateDefId) { this.fromStateDefId = fromStateDefId; }

    public String getToStateDefId() { return toStateDefId; }
    public void setToStateDefId(String toStateDefId) { this.toStateDefId = toStateDefId; }

    public String getTransitionCode() { return transitionCode; }
    public void setTransitionCode(String transitionCode) { this.transitionCode = transitionCode; }

    public String getEventNm() { return eventNm; }
    public void setEventNm(String eventNm) { this.eventNm = eventNm; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

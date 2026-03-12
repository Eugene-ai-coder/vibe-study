package com.example.vibestudy;

import java.time.LocalDateTime;

public class WfTransitionDefResponseDto {

    private String transitionDefId;
    private String processDefId;
    private String fromStateDefId;
    private String toStateDefId;
    private String transitionCode;
    private String eventNm;
    private Integer sortOrder;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getTransitionDefId() { return transitionDefId; }
    public void setTransitionDefId(String transitionDefId) { this.transitionDefId = transitionDefId; }

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

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

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

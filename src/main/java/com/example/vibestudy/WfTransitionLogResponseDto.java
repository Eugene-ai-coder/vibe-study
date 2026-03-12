package com.example.vibestudy;

import java.time.LocalDateTime;

public class WfTransitionLogResponseDto {

    private String transitionLogId;
    private String processInstId;
    private String transitionDefId;
    private String fromStateDefId;
    private String fromStateNm;
    private String toStateDefId;
    private String toStateNm;
    private LocalDateTime transitionedDt;
    private String remark;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getTransitionLogId() { return transitionLogId; }
    public void setTransitionLogId(String transitionLogId) { this.transitionLogId = transitionLogId; }

    public String getProcessInstId() { return processInstId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }

    public String getTransitionDefId() { return transitionDefId; }
    public void setTransitionDefId(String transitionDefId) { this.transitionDefId = transitionDefId; }

    public String getFromStateDefId() { return fromStateDefId; }
    public void setFromStateDefId(String fromStateDefId) { this.fromStateDefId = fromStateDefId; }

    public String getFromStateNm() { return fromStateNm; }
    public void setFromStateNm(String fromStateNm) { this.fromStateNm = fromStateNm; }

    public String getToStateDefId() { return toStateDefId; }
    public void setToStateDefId(String toStateDefId) { this.toStateDefId = toStateDefId; }

    public String getToStateNm() { return toStateNm; }
    public void setToStateNm(String toStateNm) { this.toStateNm = toStateNm; }

    public LocalDateTime getTransitionedDt() { return transitionedDt; }
    public void setTransitionedDt(LocalDateTime transitionedDt) { this.transitionedDt = transitionedDt; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
}

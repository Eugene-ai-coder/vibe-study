package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.List;

public class WfProcessDefResponseDto {

    private String processDefId;
    private String processNm;
    private String processDesc;
    private String entityType;
    private String useYn;

    /* ── 단건 조회 시 하위 데이터 ── */
    private List<WfStateDefResponseDto> states;
    private List<WfTransitionDefResponseDto> transitions;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

    public String getProcessNm() { return processNm; }
    public void setProcessNm(String processNm) { this.processNm = processNm; }

    public String getProcessDesc() { return processDesc; }
    public void setProcessDesc(String processDesc) { this.processDesc = processDesc; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }

    public List<WfStateDefResponseDto> getStates() { return states; }
    public void setStates(List<WfStateDefResponseDto> states) { this.states = states; }

    public List<WfTransitionDefResponseDto> getTransitions() { return transitions; }
    public void setTransitions(List<WfTransitionDefResponseDto> transitions) { this.transitions = transitions; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

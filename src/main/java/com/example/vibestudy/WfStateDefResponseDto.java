package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.List;

public class WfStateDefResponseDto {

    private String stateDefId;
    private String processDefId;
    private String stateNm;
    private String stateType;
    private Integer sortOrder;

    private String entityStatusCd;

    /* ── 단건 조회 시 하위 Task 템플릿 ── */
    private List<WfTaskTemplateResponseDto> taskTemplates;

    /* ── System Fields ── */
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // ── Getters / Setters ────────────────────────────────────────

    public String getStateDefId() { return stateDefId; }
    public void setStateDefId(String stateDefId) { this.stateDefId = stateDefId; }

    public String getProcessDefId() { return processDefId; }
    public void setProcessDefId(String processDefId) { this.processDefId = processDefId; }

    public String getStateNm() { return stateNm; }
    public void setStateNm(String stateNm) { this.stateNm = stateNm; }

    public String getStateType() { return stateType; }
    public void setStateType(String stateType) { this.stateType = stateType; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getEntityStatusCd() { return entityStatusCd; }
    public void setEntityStatusCd(String entityStatusCd) { this.entityStatusCd = entityStatusCd; }

    public List<WfTaskTemplateResponseDto> getTaskTemplates() { return taskTemplates; }
    public void setTaskTemplates(List<WfTaskTemplateResponseDto> taskTemplates) { this.taskTemplates = taskTemplates; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfStartProcessRequestDto {

    @NotBlank(message = "업무유형은 필수입니다.")
    private String entityType;

    @NotBlank(message = "업무ID는 필수입니다.")
    private String entityId;

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

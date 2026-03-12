package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfProcessDefRequestDto {

    @NotBlank(message = "프로세스명은 필수입니다.")
    private String processNm;

    private String processDesc;
    private String entityType;
    private String useYn;

    /* ── System Field ── */
    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getProcessNm() { return processNm; }
    public void setProcessNm(String processNm) { this.processNm = processNm; }

    public String getProcessDesc() { return processDesc; }
    public void setProcessDesc(String processDesc) { this.processDesc = processDesc; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

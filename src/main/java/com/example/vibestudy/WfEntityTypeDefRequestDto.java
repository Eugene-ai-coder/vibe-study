package com.example.vibestudy;

public class WfEntityTypeDefRequestDto {

    private String entityTypeCd;
    private String entityTypeNm;
    private String tableNm;
    private String pkColumn;
    private String statusColumn;
    private String statusCdGroup;
    private String bizKeyColumn;
    private String bizKeyLabel;
    private String routePath;

    /* ── System Field ── */
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getEntityTypeCd() { return entityTypeCd; }
    public void setEntityTypeCd(String entityTypeCd) { this.entityTypeCd = entityTypeCd; }

    public String getEntityTypeNm() { return entityTypeNm; }
    public void setEntityTypeNm(String entityTypeNm) { this.entityTypeNm = entityTypeNm; }

    public String getTableNm() { return tableNm; }
    public void setTableNm(String tableNm) { this.tableNm = tableNm; }

    public String getPkColumn() { return pkColumn; }
    public void setPkColumn(String pkColumn) { this.pkColumn = pkColumn; }

    public String getStatusColumn() { return statusColumn; }
    public void setStatusColumn(String statusColumn) { this.statusColumn = statusColumn; }

    public String getStatusCdGroup() { return statusCdGroup; }
    public void setStatusCdGroup(String statusCdGroup) { this.statusCdGroup = statusCdGroup; }

    public String getBizKeyColumn() { return bizKeyColumn; }
    public void setBizKeyColumn(String bizKeyColumn) { this.bizKeyColumn = bizKeyColumn; }

    public String getBizKeyLabel() { return bizKeyLabel; }
    public void setBizKeyLabel(String bizKeyLabel) { this.bizKeyLabel = bizKeyLabel; }

    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

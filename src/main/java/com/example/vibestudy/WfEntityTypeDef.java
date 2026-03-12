package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_wf_entity_type_def")
public class WfEntityTypeDef {

    @Id
    @Column(name = "entity_type_cd", length = 50)
    private String entityTypeCd;

    @Column(name = "entity_type_nm", length = 100, nullable = false)
    private String entityTypeNm;

    @Column(name = "table_nm", length = 100, nullable = false)
    private String tableNm;

    @Column(name = "pk_column", length = 100, nullable = false)
    private String pkColumn;

    @Column(name = "status_column", length = 100)
    private String statusColumn;

    @Column(name = "status_cd_group", length = 100)
    private String statusCdGroup;

    @Column(name = "biz_key_column", length = 100)
    private String bizKeyColumn;

    @Column(name = "biz_key_label", length = 100)
    private String bizKeyLabel;

    @Column(name = "route_path", length = 200)
    private String routePath;

    /* ── System Fields ───────────────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

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

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

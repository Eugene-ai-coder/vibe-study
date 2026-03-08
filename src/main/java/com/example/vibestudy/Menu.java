package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_menu")
public class Menu {

    @Id
    @Column(name = "menu_id", length = 20, nullable = false)
    private String menuId;

    @Column(name = "menu_nm", length = 100, nullable = false)
    private String menuNm;

    @Column(name = "menu_url", length = 200)
    private String menuUrl;

    @Column(name = "parent_menu_id", length = 20)
    private String parentMenuId;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "use_yn", length = 1)
    private String useYn = "Y";

    @Column(name = "menu_level")
    private int menuLevel = 1;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }

    public String getMenuNm() { return menuNm; }
    public void setMenuNm(String menuNm) { this.menuNm = menuNm; }

    public String getMenuUrl() { return menuUrl; }
    public void setMenuUrl(String menuUrl) { this.menuUrl = menuUrl; }

    public String getParentMenuId() { return parentMenuId; }
    public void setParentMenuId(String parentMenuId) { this.parentMenuId = parentMenuId; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }

    public int getMenuLevel() { return menuLevel; }
    public void setMenuLevel(int menuLevel) { this.menuLevel = menuLevel; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

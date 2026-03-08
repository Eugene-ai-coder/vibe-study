package com.example.vibestudy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MenuResponseDto {

    private String menuId;
    private String menuNm;
    private String menuUrl;
    private String parentMenuId;
    private int sortOrder;
    private String useYn;
    private int menuLevel;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;
    private List<String> roleCds = new ArrayList<>();
    private List<MenuResponseDto> children = new ArrayList<>();

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

    public List<String> getRoleCds() { return roleCds; }
    public void setRoleCds(List<String> roleCds) { this.roleCds = roleCds; }

    public List<MenuResponseDto> getChildren() { return children; }
    public void setChildren(List<MenuResponseDto> children) { this.children = children; }
}

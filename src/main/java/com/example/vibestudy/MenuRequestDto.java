package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class MenuRequestDto {

    private String menuId;

    @NotBlank(message = "메뉴명은 필수입니다.")
    private String menuNm;

    private String menuUrl;
    private String parentMenuId;
    private int sortOrder;
    private String useYn = "Y";
    private int menuLevel = 1;
    private String createdBy;
    private List<String> roleCds;

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

    public List<String> getRoleCds() { return roleCds; }
    public void setRoleCds(List<String> roleCds) { this.roleCds = roleCds; }
}

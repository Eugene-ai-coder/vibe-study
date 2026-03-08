package com.example.vibestudy;

import java.io.Serializable;
import java.util.Objects;

public class MenuRoleId implements Serializable {

    private String menuId;
    private String roleCd;

    public MenuRoleId() {}

    public MenuRoleId(String menuId, String roleCd) {
        this.menuId = menuId;
        this.roleCd = roleCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuRoleId that = (MenuRoleId) o;
        return Objects.equals(menuId, that.menuId) && Objects.equals(roleCd, that.roleCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId, roleCd);
    }
}

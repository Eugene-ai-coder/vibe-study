package com.example.vibestudy;

import java.io.Serializable;
import java.util.Objects;

public class UserRoleId implements Serializable {

    private String userId;
    private String roleCd;

    public UserRoleId() {}

    public UserRoleId(String userId, String roleCd) {
        this.userId = userId;
        this.roleCd = roleCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(roleCd, that.roleCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleCd);
    }
}

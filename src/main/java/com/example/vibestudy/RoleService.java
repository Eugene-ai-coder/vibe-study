package com.example.vibestudy;

import java.util.List;

public interface RoleService {

    List<RoleUserDto> getUsersByRole(String roleCd);

    List<RoleUserDto> getAvailableUsers(String roleCd);

    void saveRoleUsers(String roleCd, List<String> userIds);
}

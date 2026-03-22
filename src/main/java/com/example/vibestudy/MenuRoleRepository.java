package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRoleRepository extends JpaRepository<MenuRole, MenuRoleId> {

    List<MenuRole> findByMenuId(String menuId);

    List<MenuRole> findByMenuIdIn(List<String> menuIds);

    List<MenuRole> findByRoleCdIn(List<String> roleCds);

    void deleteByMenuId(String menuId);
}

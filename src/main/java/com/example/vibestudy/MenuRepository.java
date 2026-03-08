package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, String> {

    List<Menu> findByParentMenuIdIsNullOrderBySortOrder();

    List<Menu> findByParentMenuIdOrderBySortOrder(String parentMenuId);

    List<Menu> findByUseYnOrderByMenuLevelAscSortOrderAsc(String useYn);

    List<Menu> findAllByOrderByMenuLevelAscSortOrderAsc();

    List<Menu> findByMenuIdIn(List<String> menuIds);

    boolean existsByParentMenuId(String parentMenuId);
}

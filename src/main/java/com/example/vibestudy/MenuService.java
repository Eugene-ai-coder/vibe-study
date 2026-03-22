package com.example.vibestudy;

import java.util.List;

public interface MenuService {

    List<MenuResponseDto> getAllMenuTree();

    List<MenuResponseDto> getMenuTreeByRoles(List<String> roleCds);

    MenuResponseDto getMenu(String menuId);

    MenuResponseDto createMenu(MenuRequestDto dto);

    MenuResponseDto updateMenu(String menuId, MenuRequestDto dto);

    void moveMenuUp(String menuId);

    void moveMenuDown(String menuId);

    void moveMenu(String menuId, MenuMoveRequestDto dto);

    void deleteMenu(String menuId);
}

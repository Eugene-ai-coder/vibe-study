package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;
    private final UserRoleRepository userRoleRepository;

    public MenuController(MenuService menuService,
                          UserRoleRepository userRoleRepository) {
        this.menuService = menuService;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/tree")
    public List<MenuResponseDto> getAllMenuTree() {
        return menuService.getAllMenuTree();
    }

    @GetMapping("/tree/my")
    public List<MenuResponseDto> getMyMenuTree() {
        String userId = SecurityUtils.getCurrentUserId();
        List<String> roleCds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleCd)
                .collect(Collectors.toList());
        return menuService.getMenuTreeByRoles(roleCds);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> getMenu(@PathVariable String menuId) {
        return ResponseEntity.ok(menuService.getMenu(menuId));
    }

    @PostMapping
    public ResponseEntity<MenuResponseDto> createMenu(@Valid @RequestBody MenuRequestDto dto) {
        return ResponseEntity.status(201).body(menuService.createMenu(dto));
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable String menuId,
                                                       @Valid @RequestBody MenuRequestDto dto) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, dto));
    }

    @PostMapping("/{menuId}/move-up")
    public ResponseEntity<Void> moveMenuUp(@PathVariable String menuId) {
        menuService.moveMenuUp(menuId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{menuId}/move-down")
    public ResponseEntity<Void> moveMenuDown(@PathVariable String menuId) {
        menuService.moveMenuDown(menuId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable String menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }
}

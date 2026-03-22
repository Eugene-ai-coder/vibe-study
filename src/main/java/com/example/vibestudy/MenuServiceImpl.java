package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuRoleRepository menuRoleRepository;

    public MenuServiceImpl(MenuRepository menuRepository,
                           MenuRoleRepository menuRoleRepository) {
        this.menuRepository = menuRepository;
        this.menuRoleRepository = menuRoleRepository;
    }

    @Override
    public List<MenuResponseDto> getAllMenuTree() {
        List<Menu> allMenus = menuRepository.findAllByOrderByMenuLevelAscSortOrderAsc();
        Map<String, List<String>> menuRolesMap = loadMenuRolesMap(
                allMenus.stream().map(Menu::getMenuId).collect(Collectors.toList()));
        return buildTree(allMenus, menuRolesMap);
    }

    @Override
    public List<MenuResponseDto> getMenuTreeByRoles(List<String> roleCds) {
        if (roleCds == null || roleCds.isEmpty()) {
            return new ArrayList<>();
        }

        List<MenuRole> menuRoles = menuRoleRepository.findByRoleCdIn(roleCds);
        Set<String> allowedMenuIds = menuRoles.stream()
                .map(MenuRole::getMenuId)
                .collect(Collectors.toSet());

        if (allowedMenuIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Menu> activeMenus = menuRepository.findByUseYnOrderByMenuLevelAscSortOrderAsc("Y");

        // 허용된 메뉴 + 그 상위 메뉴(그룹)도 포함
        Set<String> visibleMenuIds = new HashSet<>(allowedMenuIds);
        for (Menu menu : activeMenus) {
            if (allowedMenuIds.contains(menu.getMenuId()) && menu.getParentMenuId() != null) {
                addParentMenuIds(menu.getParentMenuId(), activeMenus, visibleMenuIds);
            }
        }

        List<Menu> filteredMenus = activeMenus.stream()
                .filter(m -> visibleMenuIds.contains(m.getMenuId()))
                .collect(Collectors.toList());

        return buildTree(filteredMenus, Collections.emptyMap());
    }

    @Override
    public MenuResponseDto getMenu(String menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));
        MenuResponseDto dto = toDto(menu);
        List<MenuRole> roles = menuRoleRepository.findByMenuId(menuId);
        dto.setRoleCds(roles.stream().map(MenuRole::getRoleCd).collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public MenuResponseDto createMenu(MenuRequestDto dto) {
        Menu menu = new Menu();
        menu.setMenuId(generateId());
        menu.setMenuNm(dto.getMenuNm());
        menu.setMenuUrl(dto.getMenuUrl());
        menu.setParentMenuId(dto.getParentMenuId());
        int maxSort = getSiblingMenus(dto.getParentMenuId()).stream()
                .mapToInt(Menu::getSortOrder).max().orElse(0);
        menu.setSortOrder(maxSort + 1);
        menu.setUseYn(dto.getUseYn() != null ? dto.getUseYn() : "Y");

        if (dto.getParentMenuId() != null) {
            Menu parent = menuRepository.findById(dto.getParentMenuId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "상위 메뉴를 찾을 수 없습니다."));
            if (parent.getMenuUrl() != null && !parent.getMenuUrl().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "화면 URL이 있는 메뉴 하위에는 메뉴를 추가할 수 없습니다.");
            }
            menu.setMenuLevel(parent.getMenuLevel() + 1);
        } else {
            menu.setMenuLevel(1);
        }

        String currentUser = SecurityUtils.getCurrentUserId();
        menu.setCreatedBy(currentUser);
        menu.setCreatedDt(LocalDateTime.now());

        menuRepository.save(menu);
        syncMenuRoles(menu.getMenuId(), dto.getRoleCds(), currentUser);

        return getMenu(menu.getMenuId());
    }

    @Override
    @Transactional
    public MenuResponseDto updateMenu(String menuId, MenuRequestDto dto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));

        menu.setMenuNm(dto.getMenuNm());
        menu.setMenuUrl(dto.getMenuUrl());
        menu.setUseYn(dto.getUseYn() != null ? dto.getUseYn() : "Y");

        String currentUser = SecurityUtils.getCurrentUserId();
        menu.setUpdatedBy(currentUser);
        menu.setUpdatedDt(LocalDateTime.now());

        menuRepository.save(menu);
        syncMenuRoles(menuId, dto.getRoleCds(), currentUser);

        return getMenu(menuId);
    }

    @Override
    @Transactional
    public void deleteMenu(String menuId) {
        if (!menuRepository.existsById(menuId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다.");
        }
        if (menuRepository.existsByParentMenuId(menuId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "하위 메뉴가 존재하여 삭제할 수 없습니다.");
        }
        Menu menu = menuRepository.findById(menuId).get();
        String parentMenuId = menu.getParentMenuId();
        menuRoleRepository.deleteByMenuId(menuId);
        menuRepository.deleteById(menuId);
        compactSortOrders(parentMenuId);
    }

    @Override
    @Transactional
    public void moveMenuUp(String menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));
        List<Menu> siblings = getSiblingMenus(menu.getParentMenuId());
        int idx = findIndex(siblings, menuId);
        if (idx <= 0) return;
        swapSortOrder(siblings.get(idx), siblings.get(idx - 1));
    }

    @Override
    @Transactional
    public void moveMenuDown(String menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));
        List<Menu> siblings = getSiblingMenus(menu.getParentMenuId());
        int idx = findIndex(siblings, menuId);
        if (idx < 0 || idx >= siblings.size() - 1) return;
        swapSortOrder(siblings.get(idx), siblings.get(idx + 1));
    }

    @Override
    @Transactional
    public void moveMenu(String menuId, MenuMoveRequestDto dto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."));

        String oldParentId = menu.getParentMenuId();
        String newParentId = dto.getNewParentId();

        // 1. 순환참조 검증: newParentId가 menuId의 자손이면 거부
        if (newParentId != null) {
            if (newParentId.equals(menuId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신의 하위로 이동할 수 없습니다.");
            }
            Set<String> descendantIds = collectDescendantIds(menuId);
            if (descendantIds.contains(newParentId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자신의 하위 메뉴로 이동할 수 없습니다.");
            }
            Menu newParent = menuRepository.findById(newParentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "대상 상위 메뉴를 찾을 수 없습니다."));
            if (newParent.getMenuUrl() != null && !newParent.getMenuUrl().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "화면 URL이 있는 메뉴 하위로는 이동할 수 없습니다.");
            }
        }

        // 2. 출발지에서 제거 (sortOrder 갱신은 컴팩팅으로 처리)
        menu.setParentMenuId(newParentId);
        menu.setSortOrder(dto.getNewSortOrder());

        // 3. menuLevel 재계산 (자신 + 하위 전체)
        int newLevel = (newParentId == null) ? 1
                : menuRepository.findById(newParentId).get().getMenuLevel() + 1;
        int levelDiff = newLevel - menu.getMenuLevel();
        menu.setMenuLevel(newLevel);

        String currentUser = SecurityUtils.getCurrentUserId();
        menu.setUpdatedBy(currentUser);
        menu.setUpdatedDt(LocalDateTime.now());
        menuRepository.save(menu);

        // 하위 메뉴 menuLevel 재귀 갱신
        if (levelDiff != 0) {
            updateDescendantLevels(menuId, levelDiff);
        }

        // 4. 도착지 형제 sortOrder 재정렬 (삽입 위치 반영)
        compactSortOrdersWithInsert(newParentId, menuId, dto.getNewSortOrder());

        // 5. 출발지 형제 sortOrder 컴팩팅 (빈 자리 정리)
        if (!Objects.equals(oldParentId, newParentId)) {
            compactSortOrders(oldParentId);
        }
    }

    private Set<String> collectDescendantIds(String menuId) {
        Set<String> ids = new HashSet<>();
        List<Menu> children = menuRepository.findByParentMenuIdOrderBySortOrder(menuId);
        for (Menu child : children) {
            ids.add(child.getMenuId());
            ids.addAll(collectDescendantIds(child.getMenuId()));
        }
        return ids;
    }

    private void updateDescendantLevels(String parentMenuId, int levelDiff) {
        Queue<String> queue = new LinkedList<>();
        queue.add(parentMenuId);
        List<Menu> toUpdate = new ArrayList<>();
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            List<Menu> children = menuRepository.findByParentMenuIdOrderBySortOrder(currentId);
            for (Menu child : children) {
                child.setMenuLevel(child.getMenuLevel() + levelDiff);
                toUpdate.add(child);
                queue.add(child.getMenuId());
            }
        }
        if (!toUpdate.isEmpty()) {
            menuRepository.saveAll(toUpdate);
        }
    }

    private void compactSortOrdersWithInsert(String parentMenuId, String insertedMenuId, int insertPosition) {
        List<Menu> siblings = getSiblingMenus(parentMenuId);
        // insertedMenuId를 제외한 나머지를 순서대로 정렬하되, insertPosition에 삽입 메뉴 배치
        List<Menu> others = siblings.stream()
                .filter(m -> !m.getMenuId().equals(insertedMenuId))
                .collect(Collectors.toList());
        Menu inserted = siblings.stream()
                .filter(m -> m.getMenuId().equals(insertedMenuId))
                .findFirst().orElse(null);
        if (inserted == null) return;

        int pos = Math.max(0, Math.min(insertPosition - 1, others.size()));
        others.add(pos, inserted);

        for (int i = 0; i < others.size(); i++) {
            if (others.get(i).getSortOrder() != i + 1) {
                others.get(i).setSortOrder(i + 1);
                menuRepository.save(others.get(i));
            }
        }
    }

    private List<Menu> getSiblingMenus(String parentMenuId) {
        if (parentMenuId == null) {
            return menuRepository.findByParentMenuIdIsNullOrderBySortOrder();
        }
        return menuRepository.findByParentMenuIdOrderBySortOrder(parentMenuId);
    }

    private int findIndex(List<Menu> menus, String menuId) {
        for (int i = 0; i < menus.size(); i++) {
            if (menus.get(i).getMenuId().equals(menuId)) return i;
        }
        return -1;
    }

    private void swapSortOrder(Menu a, Menu b) {
        int tmp = a.getSortOrder();
        a.setSortOrder(b.getSortOrder());
        b.setSortOrder(tmp);
        menuRepository.save(a);
        menuRepository.save(b);
    }

    private void compactSortOrders(String parentMenuId) {
        List<Menu> siblings = getSiblingMenus(parentMenuId);
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i).getSortOrder() != i + 1) {
                siblings.get(i).setSortOrder(i + 1);
                menuRepository.save(siblings.get(i));
            }
        }
    }

    private void syncMenuRoles(String menuId, List<String> roleCds, String currentUser) {
        menuRoleRepository.deleteByMenuId(menuId);
        if (roleCds != null) {
            for (String roleCd : roleCds) {
                MenuRole menuRole = new MenuRole();
                menuRole.setMenuId(menuId);
                menuRole.setRoleCd(roleCd);
                menuRole.setCreatedBy(currentUser);
                menuRole.setCreatedDt(LocalDateTime.now());
                menuRoleRepository.save(menuRole);
            }
        }
    }

    private void addParentMenuIds(String parentMenuId, List<Menu> allMenus, Set<String> visibleMenuIds) {
        if (parentMenuId == null || visibleMenuIds.contains(parentMenuId)) return;
        visibleMenuIds.add(parentMenuId);
        for (Menu menu : allMenus) {
            if (menu.getMenuId().equals(parentMenuId) && menu.getParentMenuId() != null) {
                addParentMenuIds(menu.getParentMenuId(), allMenus, visibleMenuIds);
                break;
            }
        }
    }

    private List<MenuResponseDto> buildTree(List<Menu> allMenus, Map<String, List<String>> menuRolesMap) {
        Map<String, MenuResponseDto> dtoMap = new LinkedHashMap<>();
        allMenus.forEach(m -> {
            MenuResponseDto dto = toDto(m);
            if (menuRolesMap.containsKey(m.getMenuId())) {
                dto.setRoleCds(menuRolesMap.get(m.getMenuId()));
            }
            dtoMap.put(m.getMenuId(), dto);
        });

        List<MenuResponseDto> roots = new ArrayList<>();
        dtoMap.values().forEach(dto -> {
            if (dto.getParentMenuId() == null) {
                roots.add(dto);
            } else {
                MenuResponseDto parent = dtoMap.get(dto.getParentMenuId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        });
        return roots;
    }

    private Map<String, List<String>> loadMenuRolesMap(List<String> menuIds) {
        if (menuIds.isEmpty()) return Collections.emptyMap();
        return menuRoleRepository.findByMenuIdIn(menuIds).stream()
                .collect(Collectors.groupingBy(
                        MenuRole::getMenuId,
                        Collectors.mapping(MenuRole::getRoleCd, Collectors.toList())
                ));
    }

    private MenuResponseDto toDto(Menu menu) {
        MenuResponseDto dto = new MenuResponseDto();
        dto.setMenuId(menu.getMenuId());
        dto.setMenuNm(menu.getMenuNm());
        dto.setMenuUrl(menu.getMenuUrl());
        dto.setParentMenuId(menu.getParentMenuId());
        dto.setSortOrder(menu.getSortOrder());
        dto.setUseYn(menu.getUseYn());
        dto.setMenuLevel(menu.getMenuLevel());
        dto.setCreatedBy(menu.getCreatedBy());
        dto.setCreatedDt(menu.getCreatedDt());
        dto.setUpdatedBy(menu.getUpdatedBy());
        dto.setUpdatedDt(menu.getUpdatedDt());
        return dto;
    }

    private String generateId() {
        return IdGenerator.generate("MNU");
    }
}

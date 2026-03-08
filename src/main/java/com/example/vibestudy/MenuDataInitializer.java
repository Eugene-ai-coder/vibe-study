package com.example.vibestudy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(2)
public class MenuDataInitializer implements CommandLineRunner {

    private final MenuRepository menuRepository;
    private final MenuRoleRepository menuRoleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonDtlCodeRepository commonDtlCodeRepository;

    public MenuDataInitializer(MenuRepository menuRepository,
                               MenuRoleRepository menuRoleRepository,
                               UserRoleRepository userRoleRepository,
                               UserRepository userRepository,
                               CommonCodeRepository commonCodeRepository,
                               CommonDtlCodeRepository commonDtlCodeRepository) {
        this.menuRepository = menuRepository;
        this.menuRoleRepository = menuRoleRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.commonCodeRepository = commonCodeRepository;
        this.commonDtlCodeRepository = commonDtlCodeRepository;
    }

    @Override
    public void run(String... args) {
        initRoleCommonCode();
        initMenuData();
        initUserRoles();
    }

    private void initRoleCommonCode() {
        if (commonCodeRepository.existsById("ROLE")) return;

        CommonCode roleCode = new CommonCode();
        roleCode.setCommonCode("ROLE");
        roleCode.setCommonCodeNm("역할코드");
        roleCode.setCreatedBy("SYSTEM");
        roleCode.setCreatedDt(LocalDateTime.now());
        commonCodeRepository.save(roleCode);

        createDtlCode("ROLE", "ADMIN", "관리자", 1);
        createDtlCode("ROLE", "USER", "일반사용자", 2);
    }

    private void createDtlCode(String commonCode, String dtlCode, String dtlCodeNm, int sortOrder) {
        CommonDtlCode dtl = new CommonDtlCode();
        dtl.setId(new CommonDtlCodeId(commonCode, dtlCode));
        dtl.setCommonDtlCodeNm(dtlCodeNm);
        dtl.setSortOrder(sortOrder);
        dtl.setCreatedBy("SYSTEM");
        dtl.setCreatedDt(LocalDateTime.now());
        commonDtlCodeRepository.save(dtl);
    }

    private void initMenuData() {
        if (menuRepository.count() > 0) return;

        // 최상위 메뉴
        createMenu("MNU001", "Main", "/main", null, 1, 1);
        createMenu("MNU002", "가입관리", null, null, 2, 1);
        createMenu("MNU007", "시스템 설정", null, null, 3, 1);
        createMenu("MNU011", "게시판", null, null, 4, 1);

        // 가입관리 하위
        createMenu("MNU003", "가입관리", "/subscriptions", "MNU002", 1, 2);
        createMenu("MNU004", "과금기준", "/bill-std", "MNU002", 2, 2);
        createMenu("MNU005", "대표가입 관리", "/subscription-main", "MNU002", 3, 2);
        createMenu("MNU006", "특수가입관리", "/special-subscription", "MNU002", 4, 2);

        // 시스템 설정 하위
        createMenu("MNU008", "사용자관리", "/users", "MNU007", 1, 2);
        createMenu("MNU009", "공통코드관리", "/code", "MNU007", 2, 2);
        createMenu("MNU010", "메뉴관리", "/menu", "MNU007", 3, 2);
        createMenu("MNU013", "권한관리", "/role", "MNU007", 4, 2);

        // 게시판 하위
        createMenu("MNU012", "Q&A", "/qna", "MNU011", 1, 2);

        // 메뉴-역할 매핑: 모든 메뉴에 ADMIN, 메뉴관리 제외하고 USER
        List<String> allMenuIds = List.of(
            "MNU001", "MNU002", "MNU003", "MNU004", "MNU005", "MNU006",
            "MNU007", "MNU008", "MNU009", "MNU010", "MNU011", "MNU012", "MNU013");

        for (String menuId : allMenuIds) {
            createMenuRole(menuId, "ADMIN");
            if (!"MNU010".equals(menuId) && !"MNU013".equals(menuId)) {
                createMenuRole(menuId, "USER");
            }
        }
    }

    private void createMenu(String menuId, String menuNm, String menuUrl,
                             String parentMenuId, int sortOrder, int menuLevel) {
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuNm(menuNm);
        menu.setMenuUrl(menuUrl);
        menu.setParentMenuId(parentMenuId);
        menu.setSortOrder(sortOrder);
        menu.setUseYn("Y");
        menu.setMenuLevel(menuLevel);
        menu.setCreatedBy("SYSTEM");
        menu.setCreatedDt(LocalDateTime.now());
        menuRepository.save(menu);
    }

    private void createMenuRole(String menuId, String roleCd) {
        MenuRole mr = new MenuRole();
        mr.setMenuId(menuId);
        mr.setRoleCd(roleCd);
        mr.setCreatedBy("SYSTEM");
        mr.setCreatedDt(LocalDateTime.now());
        menuRoleRepository.save(mr);
    }

    private void initUserRoles() {
        if (userRoleRepository.count() > 0) return;

        // 기존 사용자에게 역할 부여: user01 = ADMIN, 나머지 = USER
        List<User> users = userRepository.findAll();
        for (User user : users) {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleCd("user01".equals(user.getUserId()) ? "ADMIN" : "USER");
            ur.setCreatedBy("SYSTEM");
            ur.setCreatedDt(LocalDateTime.now());
            userRoleRepository.save(ur);
        }
    }
}

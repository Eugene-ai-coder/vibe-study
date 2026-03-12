package com.example.vibestudy;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final CommonCodeRepository commonCodeRepository;
    private final CommonDtlCodeRepository commonDtlCodeRepository;
    private final MenuRepository menuRepository;
    private final MenuRoleRepository menuRoleRepository;

    public ConfigController(CommonCodeRepository commonCodeRepository,
                            CommonDtlCodeRepository commonDtlCodeRepository,
                            MenuRepository menuRepository,
                            MenuRoleRepository menuRoleRepository) {
        this.commonCodeRepository = commonCodeRepository;
        this.commonDtlCodeRepository = commonDtlCodeRepository;
        this.menuRepository = menuRepository;
        this.menuRoleRepository = menuRoleRepository;
    }

    @GetMapping(value = "/export-sql", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportSql() {
        StringBuilder sb = new StringBuilder();

        // 공통코드 헤더
        sb.append("-- ================================================================\n");
        sb.append("-- 공통코드 초기 데이터\n");
        sb.append("-- ================================================================\n\n");
        sb.append("-- ── 1. 공통코드 헤더 ─────────────────────────────────────────────\n");

        List<CommonCode> codes = commonCodeRepository.findAll();
        codes.sort(Comparator.comparing(CommonCode::getCommonCode));
        for (CommonCode c : codes) {
            sb.append(String.format(
                "INSERT INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) VALUES\n" +
                "('%s', '%s', %s, 'SYSTEM', CURRENT_TIMESTAMP)\n" +
                "ON DUPLICATE KEY UPDATE common_code_nm = VALUES(common_code_nm), remark = VALUES(remark);\n\n",
                esc(c.getCommonCode()), esc(c.getCommonCodeNm()), sqlStr(c.getRemark())));
        }

        // 공통상세코드
        sb.append("-- ── 2. 공통상세코드 ──────────────────────────────────────────────\n");
        List<CommonDtlCode> dtls = commonDtlCodeRepository.findAll();
        dtls.sort(Comparator.comparing((CommonDtlCode d) -> d.getId().getCommonCode())
                .thenComparing(d -> d.getSortOrder() != null ? d.getSortOrder() : 0));

        String prevCode = "";
        for (CommonDtlCode d : dtls) {
            String code = d.getId().getCommonCode();
            if (!code.equals(prevCode)) {
                sb.append("\n-- ").append(code).append("\n");
                prevCode = code;
            }
            sb.append(String.format(
                "INSERT INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, " +
                "eff_start_dt, eff_end_dt, created_by, created_dt) VALUES\n" +
                "('%s', '%s', '%s', %d, '2000-01-01 00:00:00', '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP)\n" +
                "ON DUPLICATE KEY UPDATE common_dtl_code_nm = VALUES(common_dtl_code_nm), sort_order = VALUES(sort_order);\n\n",
                esc(code), esc(d.getId().getCommonDtlCode()), esc(d.getCommonDtlCodeNm()),
                d.getSortOrder() != null ? d.getSortOrder() : 0));
        }

        // 메뉴
        sb.append("-- ================================================================\n");
        sb.append("-- 메뉴 초기 데이터 (부모 메뉴 → 자식 메뉴 순서, FK 제약 준수)\n");
        sb.append("-- ================================================================\n\n");

        List<Menu> menus = menuRepository.findAll();
        // Level 1 먼저, Level 2 나중에 (FK 순서)
        menus.sort(Comparator.comparingInt(Menu::getMenuLevel)
                .thenComparingInt(Menu::getSortOrder));

        int prevLevel = 0;
        for (Menu m : menus) {
            if (m.getMenuLevel() != prevLevel) {
                sb.append(String.format("-- Level %d\n", m.getMenuLevel()));
                prevLevel = m.getMenuLevel();
            }
            sb.append(String.format(
                "INSERT INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) VALUES\n" +
                "('%s', '%s', %s, %s, %d, '%s', %d, 'SYSTEM', CURRENT_TIMESTAMP)\n" +
                "ON DUPLICATE KEY UPDATE menu_nm = VALUES(menu_nm), menu_url = VALUES(menu_url), sort_order = VALUES(sort_order);\n\n",
                esc(m.getMenuId()), esc(m.getMenuNm()), sqlStr(m.getMenuUrl()),
                sqlStr(m.getParentMenuId()), m.getSortOrder(), m.getUseYn(), m.getMenuLevel()));
        }

        // 메뉴-역할 매핑
        sb.append("-- ================================================================\n");
        sb.append("-- 메뉴-역할 매핑\n");
        sb.append("-- ================================================================\n");

        List<MenuRole> roles = menuRoleRepository.findAll();
        roles.sort(Comparator.comparing(MenuRole::getMenuId).thenComparing(MenuRole::getRoleCd));
        for (MenuRole r : roles) {
            sb.append(String.format(
                "INSERT INTO tb_menu_role (menu_id, role_cd, created_by, created_dt) VALUES " +
                "('%s', '%s', 'SYSTEM', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE created_by = VALUES(created_by);\n",
                esc(r.getMenuId()), esc(r.getRoleCd())));
        }

        return sb.toString();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("'", "''");
    }

    private static String sqlStr(String s) {
        return s == null ? "NULL" : "'" + s.replace("'", "''") + "'";
    }
}

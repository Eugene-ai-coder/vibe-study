package com.example.vibestudy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;
    private final CommonDtlCodeRepository commonDtlCodeRepository;

    public RoleController(RoleService roleService,
                          CommonDtlCodeRepository commonDtlCodeRepository) {
        this.roleService = roleService;
        this.commonDtlCodeRepository = commonDtlCodeRepository;
    }

    @GetMapping
    public List<CommonDtlCode> getRoles() {
        return commonDtlCodeRepository.findByIdCommonCodeOrderBySortOrder("ROLE");
    }

    @GetMapping("/{roleCd}/users")
    public List<RoleUserDto> getUsersByRole(@PathVariable String roleCd) {
        return roleService.getUsersByRole(roleCd);
    }

    @GetMapping("/available-users")
    public List<RoleUserDto> getAvailableUsers(@RequestParam String roleCd) {
        return roleService.getAvailableUsers(roleCd);
    }

    @PutMapping("/{roleCd}/users")
    public ResponseEntity<Void> saveRoleUsers(@PathVariable String roleCd,
                                               @RequestBody List<String> userIds) {
        roleService.saveRoleUsers(roleCd, userIds);
        return ResponseEntity.ok().build();
    }
}

package com.example.vibestudy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(UserRoleRepository userRoleRepository,
                           UserRepository userRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RoleUserDto> getUsersByRole(String roleCd) {
        List<UserRole> userRoles = userRoleRepository.findByRoleCd(roleCd);
        Set<String> userIds = userRoles.stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return List.of();
        }
        return userRepository.findAllById(userIds).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleUserDto> getAvailableUsers(String roleCd) {
        Set<String> assignedIds = userRoleRepository.findByRoleCd(roleCd).stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toSet());
        return userRepository.findAll().stream()
                .filter(u -> !assignedIds.contains(u.getUserId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveRoleUsers(String roleCd, List<String> userIds) {
        userRoleRepository.deleteByRoleCd(roleCd);
        String currentUser = SecurityUtils.getCurrentUserId();
        for (String userId : userIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleCd(roleCd);
            ur.setCreatedBy(currentUser);
            ur.setCreatedDt(LocalDateTime.now());
            userRoleRepository.save(ur);
        }
    }

    private RoleUserDto toDto(User user) {
        RoleUserDto dto = new RoleUserDto();
        dto.setUserId(user.getUserId());
        dto.setNickname(user.getNickname());
        dto.setAccountStatus(user.getAccountStatus());
        return dto;
    }
}

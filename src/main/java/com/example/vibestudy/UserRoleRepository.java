package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByUserId(String userId);

    List<UserRole> findByRoleCd(String roleCd);

    void deleteByRoleCd(String roleCd);
}

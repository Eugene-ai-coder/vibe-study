package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserSessionDto getUserSession(String userId);
    UserSessionDto register(RegisterRequestDto dto);
    List<Map<String, Object>> listUsers();
    Page<UserResponseDto> listUsersPage(String userId, String nickname, String email, Pageable pageable);
}

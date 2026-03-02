package com.example.vibestudy;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserSessionDto authenticate(String userId, String password);
    UserSessionDto register(RegisterRequestDto dto);
    List<Map<String, Object>> listUsers();
}

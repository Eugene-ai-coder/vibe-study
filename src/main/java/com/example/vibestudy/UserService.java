package com.example.vibestudy;

public interface UserService {
    UserSessionDto authenticate(String userId, String password);
    UserSessionDto register(RegisterRequestDto dto);
}

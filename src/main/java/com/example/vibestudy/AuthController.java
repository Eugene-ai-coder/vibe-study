package com.example.vibestudy;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SESSION_KEY = "SESSION_USER";
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserSessionDto> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpSession session) {
        UserSessionDto userInfo = userService.authenticate(dto.getUserId(), dto.getPassword());
        session.setAttribute(SESSION_KEY, userInfo);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserSessionDto> me(HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute(SESSION_KEY);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserSessionDto> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.status(201).body(userService.register(dto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }
}

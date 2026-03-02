package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final String AUTH_ERROR_MSG = "아이디 또는 비밀번호가 일치하지 않습니다.";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserSessionDto authenticate(String userId, String rawPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTH_ERROR_MSG));

        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTH_ERROR_MSG);
        }

        if (user.getAccountStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용이 제한된 계정입니다.");
        }

        return new UserSessionDto(user.getUserId(), user.getNickname(), user.getAccountStatus());
    }

    @Override
    public UserSessionDto register(RegisterRequestDto dto) {
        if (userRepository.existsById(dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        }
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setNickname(dto.getNickname());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setAccountStatus(1);
        user.setCreatedBy("SYSTEM");
        user.setCreatedDt(LocalDateTime.now());
        userRepository.save(user);
        return new UserSessionDto(user.getUserId(), user.getNickname(), user.getAccountStatus());
    }

    @Override
    public List<Map<String, Object>> listUsers() {
        return userRepository.findAll().stream()
            .map(u -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", u.getUserId());
                m.put("nickname", u.getNickname());
                m.put("email", u.getEmail());
                m.put("accountStatus", u.getAccountStatus());
                return m;
            })
            .collect(Collectors.toList());
    }
}

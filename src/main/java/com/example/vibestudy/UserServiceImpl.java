package com.example.vibestudy;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserSessionDto getUserSession(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
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
        user.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
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

    @Override
    public Page<UserResponseDto> listUsersPage(String userId, String nickname, String email, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null && !userId.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("userId")), "%" + userId.toLowerCase() + "%"));
            }
            if (nickname != null && !nickname.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nickname")), "%" + nickname.toLowerCase() + "%"));
            }
            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec, pageable).map(this::toDto);
    }

    private UserResponseDto toDto(User u) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(u.getUserId());
        dto.setNickname(u.getNickname());
        dto.setEmail(u.getEmail());
        dto.setAccountStatus(u.getAccountStatus());
        dto.setCreatedBy(u.getCreatedBy());
        dto.setCreatedDt(u.getCreatedDt());
        return dto;
    }
}

package com.example.vibestudy;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        List<SimpleGrantedAuthority> authorities = userRoleRepository.findByUserId(userId)
            .stream()
            .map(ur -> new SimpleGrantedAuthority(ur.getRoleCd()))
            .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities = List.of(new SimpleGrantedAuthority("USER"));
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUserId(),
            user.getPassword(),
            user.getAccountStatus() == 1,
            true,
            true,
            true,
            authorities
        );
    }
}

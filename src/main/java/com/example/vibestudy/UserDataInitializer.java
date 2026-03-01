package com.example.vibestudy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserDataInitializer(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        String hashedPwd = encoder.encode("password123");
        for (int i = 1; i <= 10; i++) {
            User u = new User();
            u.setUserId("user" + String.format("%02d", i));
            u.setNickname("사용자" + i);
            u.setPassword(hashedPwd);
            u.setEmail("user" + i + "@example.com");
            u.setAccountStatus(i <= 8 ? 1 : (i == 9 ? 2 : 0));
            u.setCreatedBy("SYSTEM");
            u.setCreatedDt(LocalDateTime.now());
            repo.save(u);
        }
    }
}

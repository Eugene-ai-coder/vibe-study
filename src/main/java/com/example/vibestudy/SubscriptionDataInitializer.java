package com.example.vibestudy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class SubscriptionDataInitializer implements CommandLineRunner {

    private final SubscriptionRepository repository;

    public SubscriptionDataInitializer(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;  // 이미 데이터 존재 시 스킵

        String[] statuses = {"ACTIVE", "SUSPENDED", "TERMINATED", "PENDING"};
        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 10; i++) {
            Subscription s = new Subscription();
            s.setSubsId(String.format("SUBS%04d", i));
            s.setSubsNm("가입자" + i);
            s.setSvcNm("서비스" + ((i % 3) + 1));
            s.setFeeProdNm("요금상품" + ((i % 2) + 1));
            s.setSubsStatusCd(statuses[(i - 1) % 4]);
            s.setSubsDt(now.minusDays(30L * i));
            s.setChgDt(i % 3 == 0 ? now.minusDays(5) : null);
            s.setCreatedBy("SYSTEM");
            s.setCreatedDt(now);
            repository.save(s);
        }
    }
}

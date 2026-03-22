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
        String[] svcCodes = {"SVC01", "SVC02", "SVC03"};
        String[] basicProdCodes = {"FP_A", "FP_B", "FP_C", "FP_D", "FP_E", "FP_F"};
        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 30; i++) {
            Subscription s = new Subscription();
            s.setSubsId(String.format("SUBS%04d", i));
            s.setSubsNm("가입자" + i);
            s.setSvcCd(svcCodes[i % 3]);
            s.setBasicProdCd(basicProdCodes[i % 6]);
            s.setSubsStatusCd(statuses[(i - 1) % 4]);
            s.setSubsDt(now.minusDays(30L * i));
            s.setChgDt(i % 3 == 0 ? now.minusDays(5) : null);
            s.setCreatedBy("SYSTEM");
            s.setCreatedDt(now);
            repository.save(s);
        }
    }
}

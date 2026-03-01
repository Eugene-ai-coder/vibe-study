package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findBySubsIdContainingIgnoreCase(String subsId);
    List<Subscription> findBySvcNmContainingIgnoreCase(String svcNm);
    List<Subscription> findByFeeProdNmContainingIgnoreCase(String feeProdNm);
    List<Subscription> findBySubsStatusCd(String subsStatusCd);

    long countBySubsId(String subsId);
}

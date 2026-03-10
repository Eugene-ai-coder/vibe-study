package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpecialSubscriptionRepository extends JpaRepository<SpecialSubscription, SpecialSubscriptionId>,
        JpaSpecificationExecutor<SpecialSubscription> {
}

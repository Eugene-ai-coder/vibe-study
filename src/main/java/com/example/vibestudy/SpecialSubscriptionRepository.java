package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpecialSubscriptionRepository extends JpaRepository<SpecialSubscription, SpecialSubscriptionId> {
    List<SpecialSubscription> findByIdSubsBillStdIdContaining(String subsBillStdId);
    List<SpecialSubscription> findBySubsIdContaining(String subsId);
    List<SpecialSubscription> findByIdSubsBillStdIdContainingAndSubsIdContaining(String subsBillStdId, String subsId);
}

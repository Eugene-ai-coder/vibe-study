package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, String>,
        JpaSpecificationExecutor<Subscription> {

    long countBySubsId(String subsId);

    @Query("SELECT s.subsId FROM Subscription s WHERE s.subsId IN :ids")
    Set<String> findExistingIds(@Param("ids") List<String> ids);
}

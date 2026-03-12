package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfProcessInstRepository extends JpaRepository<WfProcessInst, String> {
    List<WfProcessInst> findByEntityTypeAndEntityId(String entityType, String entityId);
    boolean existsByEntityTypeAndEntityIdAndStatus(String entityType, String entityId, String status);
}

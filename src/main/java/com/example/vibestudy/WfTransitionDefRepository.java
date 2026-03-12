package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfTransitionDefRepository extends JpaRepository<WfTransitionDef, String> {
    List<WfTransitionDef> findByProcessDefIdOrderBySortOrder(String processDefId);
    boolean existsByProcessDefId(String processDefId);
    boolean existsByFromStateDefIdOrToStateDefId(String fromId, String toId);
    List<WfTransitionDef> findByFromStateDefIdAndTransitionCode(String fromStateDefId, String transitionCode);
}

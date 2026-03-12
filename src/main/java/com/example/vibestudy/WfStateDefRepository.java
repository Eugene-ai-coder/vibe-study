package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfStateDefRepository extends JpaRepository<WfStateDef, String> {
    List<WfStateDef> findByProcessDefIdOrderBySortOrder(String processDefId);
    boolean existsByProcessDefId(String processDefId);
    List<WfStateDef> findByProcessDefIdAndStateType(String processDefId, String stateType);
}

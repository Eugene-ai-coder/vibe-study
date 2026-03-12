package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfTaskInstRepository extends JpaRepository<WfTaskInst, String> {
    List<WfTaskInst> findByProcessInstIdOrderByCreatedDt(String processInstId);
    List<WfTaskInst> findByAssigneeIdAndStatusOrderByPriorityAscCreatedDtAsc(String assigneeId, String status);
    List<WfTaskInst> findByAssigneeIdOrderByPriorityAscCreatedDtAsc(String assigneeId);
    List<WfTaskInst> findByProcessInstIdAndStateDefIdAndStatusNotIn(String processInstId, String stateDefId, List<String> statuses);
    List<WfTaskInst> findByStatusOrderByPriorityAscCreatedDtAsc(String status);
    List<WfTaskInst> findAllByOrderByPriorityAscCreatedDtAsc();
}

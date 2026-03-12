package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfTransitionLogRepository extends JpaRepository<WfTransitionLog, String> {
    List<WfTransitionLog> findByProcessInstIdOrderByTransitionedDt(String processInstId);
}

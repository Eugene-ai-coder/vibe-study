package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfTaskTemplateRepository extends JpaRepository<WfTaskTemplate, String> {
    List<WfTaskTemplate> findByStateDefIdOrderBySortOrder(String stateDefId);
    boolean existsByStateDefId(String stateDefId);
}

package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfProcessDefRepository extends JpaRepository<WfProcessDef, String> {
    List<WfProcessDef> findByEntityTypeAndUseYn(String entityType, String useYn);
}

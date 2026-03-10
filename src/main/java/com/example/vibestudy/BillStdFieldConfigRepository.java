package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillStdFieldConfigRepository
        extends JpaRepository<BillStdFieldConfig, BillStdFieldConfigId>,
                JpaSpecificationExecutor<BillStdFieldConfig> {

    @Query("SELECT c FROM BillStdFieldConfig c WHERE c.id.svcCd = :svcCd " +
           "AND c.id.effStartDt <= :today AND c.effEndDt >= :today " +
           "ORDER BY c.sortOrder")
    List<BillStdFieldConfig> findEffectiveBySvcCd(
        @Param("svcCd") String svcCd, @Param("today") String today);
}

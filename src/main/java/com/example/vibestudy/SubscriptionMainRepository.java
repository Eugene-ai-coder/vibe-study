package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SubscriptionMainRepository extends JpaRepository<SubscriptionMain, String> {

    @Query("SELECT sm FROM SubscriptionMain sm " +
           "WHERE sm.subsId = :subsId " +
           "AND sm.effStartDt <= :now AND sm.effEndDt >= :now")
    Optional<SubscriptionMain> findActiveBySubsId(
        @Param("subsId") String subsId,
        @Param("now") LocalDateTime now);

    boolean existsBySubsId(String subsId);

    @Query(value = """
        SELECT s.subs_id, s.subs_nm, s.svc_cd, s.basic_prod_cd,
               sm.main_subs_yn, sm.main_subs_id
        FROM tb_subscription s
        LEFT JOIN tb_subscription_main sm
          ON s.subs_id = sm.subs_id
         AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
        WHERE (:svcCd IS NULL OR s.svc_cd = :svcCd)
          AND (
            (:searchType = '서비스'    AND LOWER(s.svc_cd)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '상품'      AND LOWER(s.basic_prod_cd)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
          )
        ORDER BY s.subs_id
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM tb_subscription s
        LEFT JOIN tb_subscription_main sm
          ON s.subs_id = sm.subs_id
         AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
        WHERE (:svcCd IS NULL OR s.svc_cd = :svcCd)
          AND (
            (:searchType = '서비스'    AND LOWER(s.svc_cd)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '상품'      AND LOWER(s.basic_prod_cd)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
          )
        """,
        nativeQuery = true)
    Page<Object[]> findListRaw(
        @Param("svcCd") String svcCd,
        @Param("searchType") String searchType,
        @Param("keyword") String keyword,
        @Param("now") LocalDateTime now,
        Pageable pageable);
}

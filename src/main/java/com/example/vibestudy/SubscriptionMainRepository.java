package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
        SELECT s.subs_id, s.svc_nm, s.fee_prod_nm,
               sm.main_subs_yn, sm.main_subs_id
        FROM tb_subscription s
        LEFT JOIN tb_subscription_main sm
          ON s.subs_id = sm.subs_id
         AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
        WHERE (:svcNm IS NULL OR s.svc_nm = :svcNm)
          AND (
            (:searchType = '서비스'    AND LOWER(s.svc_nm)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '상품'      AND LOWER(s.fee_prod_nm)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
            (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
          )
        ORDER BY s.subs_id
        """, nativeQuery = true)
    List<Object[]> findListRaw(
        @Param("svcNm") String svcNm,
        @Param("searchType") String searchType,
        @Param("keyword") String keyword,
        @Param("now") LocalDateTime now);
}

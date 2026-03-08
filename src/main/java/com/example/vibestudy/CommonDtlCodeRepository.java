package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CommonDtlCodeRepository extends JpaRepository<CommonDtlCode, CommonDtlCodeId> {
    List<CommonDtlCode> findByIdCommonCodeOrderBySortOrder(String commonCode);
    boolean existsByIdCommonCode(String commonCode);

    List<CommonDtlCode> findByIdCommonCodeAndEffStartDtLessThanEqualAndEffEndDtGreaterThanEqualOrderBySortOrder(
            String commonCode, LocalDateTime now1, LocalDateTime now2);
}

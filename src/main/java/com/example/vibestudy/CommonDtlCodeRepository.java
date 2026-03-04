package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommonDtlCodeRepository extends JpaRepository<CommonDtlCode, CommonDtlCodeId> {
    List<CommonDtlCode> findByIdCommonCodeOrderBySortOrder(String commonCode);
    boolean existsByIdCommonCode(String commonCode);
}

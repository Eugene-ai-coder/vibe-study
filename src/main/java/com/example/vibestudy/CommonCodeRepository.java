package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
    List<CommonCode> findByCommonCodeContainingAndCommonCodeNmContaining(
            String commonCode, String commonCodeNm);
}

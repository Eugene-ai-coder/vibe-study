package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubsBillStdService {
    Page<SubsBillStdResponseDto> findList(String keyword, Pageable pageable);
}

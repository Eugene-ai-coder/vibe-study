package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubsMthBillQtyService {
    Page<SubsMthBillQtyResponseDto> findPage(String keyword, String useMthFrom, String useMthTo, Pageable pageable);
}

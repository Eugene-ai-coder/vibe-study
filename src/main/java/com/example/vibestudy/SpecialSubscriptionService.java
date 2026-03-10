package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialSubscriptionService {
    Page<SpecialSubscriptionResponseDto> findPage(String subsBillStdId, String subsId, Pageable pageable);
    SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStaDt);
    SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto);
    SpecialSubscriptionResponseDto update(String subsBillStdId, String effStaDt, SpecialSubscriptionRequestDto dto);
    void delete(String subsBillStdId, String effStaDt);
}

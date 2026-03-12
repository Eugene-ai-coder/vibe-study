package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialSubscriptionService {
    Page<SpecialSubscriptionResponseDto> findPage(String subsBillStdId, String subsId, Pageable pageable);
    SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStartDt);
    SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto);
    SpecialSubscriptionResponseDto update(String subsBillStdId, String effStartDt, SpecialSubscriptionRequestDto dto);
    void delete(String subsBillStdId, String effStartDt);
}

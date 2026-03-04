package com.example.vibestudy;

import java.util.List;

public interface SpecialSubscriptionService {
    List<SpecialSubscriptionResponseDto> findAll(String subsBillStdId, String subsId);
    SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStaDt);
    SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto);
    SpecialSubscriptionResponseDto update(String subsBillStdId, String effStaDt, SpecialSubscriptionRequestDto dto);
    void delete(String subsBillStdId, String effStaDt);
}

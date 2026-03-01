package com.example.vibestudy;

import java.util.List;

public interface SubscriptionMainService {
    List<SubscriptionMainListResponseDto> findList(String svcNm, String searchType, String keyword);
    SubscriptionMainResponseDto save(SubscriptionMainRequestDto dto);
}

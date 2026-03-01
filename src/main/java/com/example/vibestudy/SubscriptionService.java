package com.example.vibestudy;

import java.util.List;

public interface SubscriptionService {

    /** 검색 유형·키워드 기반 목록 조회 */
    List<SubscriptionResponseDto> search(String type, String keyword);

    /** 단건 조회 — 없으면 404 */
    SubscriptionResponseDto findById(String subsId);

    /** 등록 — PK 중복 시 400 */
    SubscriptionResponseDto create(SubscriptionRequestDto dto);

    /** 수정 — subsId/createdBy/createdDt 변경 불가 */
    SubscriptionResponseDto update(String subsId, SubscriptionRequestDto dto);

    /** 삭제 — BillStd 존재 시 409 */
    void delete(String subsId);
}

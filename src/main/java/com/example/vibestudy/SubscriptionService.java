package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionService {

    /** 검색 유형·키워드 기반 페이징 목록 조회 */
    Page<SubscriptionResponseDto> searchPage(String type, String keyword, Pageable pageable);

    /** 단건 조회 — 없으면 404 */
    SubscriptionResponseDto findById(String subsId);

    /** 등록 — PK 중복 시 400 */
    SubscriptionResponseDto create(SubscriptionRequestDto dto);

    /** 수정 — subsId/createdBy/createdDt 변경 불가 */
    SubscriptionResponseDto update(String subsId, SubscriptionRequestDto dto);

    /** 삭제 — BillStd 존재 시 409 */
    void delete(String subsId);
}

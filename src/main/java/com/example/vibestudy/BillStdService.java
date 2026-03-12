package com.example.vibestudy;

import java.util.List;

public interface BillStdService {

    /** 전체 목록 조회 */
    List<BillStdResponseDto> findAll();

    /** 단건 조회 — 존재하지 않으면 예외 */
    BillStdResponseDto findById(String billStdId);

    /** 가입ID로 현재 활성 과금기준 조회 — 존재하지 않으면 404 예외 */
    BillStdResponseDto findBySubsId(String subsId);

    /** 신규 등록 — billStdId 채번, createdDt 자동 설정 */
    BillStdResponseDto create(BillStdRequestDto dto);

    /** 수정 — subsId / createdBy / createdDt 변경 불가, updatedBy·updatedDt 자동 설정 */
    BillStdResponseDto update(String billStdId, BillStdRequestDto dto);

    /** 삭제 — 존재하지 않으면 예외 */
    void delete(String billStdId);
}

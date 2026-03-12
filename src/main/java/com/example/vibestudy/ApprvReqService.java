package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprvReqService {
    Page<ApprvReqListResponseDto> findList(String subsId, String stdRegStatCd,
            String startDt, String endDt, Pageable pageable);
    BillStdApprvReqResponseDto createApprvReq(BillStdApprvReqRequestDto dto);
    BillStdApprvReqResponseDto findByApprvReqId(String apprvReqId);
}

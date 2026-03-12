package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpclSubsMthBillQtyService {
    Page<SpclSubsMthBillQtyResponseDto> findPage(String spclSubsId, Pageable pageable);
    SpclSubsMthBillQtyResponseDto findById(String spclSubsId, String useMth);
    SpclSubsMthBillQtyResponseDto create(SpclSubsMthBillQtyRequestDto dto);
    SpclSubsMthBillQtyResponseDto update(String spclSubsId, String useMth, SpclSubsMthBillQtyRequestDto dto);
    void delete(String spclSubsId, String useMth);
}

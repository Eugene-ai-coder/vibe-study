package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpclSubsMthBillElemService {
    Page<SpclSubsMthBillElemResponseDto> findPage(String spclSubsId, Pageable pageable);
    SpclSubsMthBillElemResponseDto findById(String spclSubsId, String billMth);
    SpclSubsMthBillElemResponseDto create(SpclSubsMthBillElemRequestDto dto);
    SpclSubsMthBillElemResponseDto update(String spclSubsId, String billMth, SpclSubsMthBillElemRequestDto dto);
    void delete(String spclSubsId, String billMth);
}

package com.example.vibestudy;

import java.util.List;

public interface BillStdFieldConfigService {

    List<BillStdFieldConfigResponseDto> findAll(String svcCd, String fieldCd);

    BillStdFieldConfigResponseDto findById(String svcCd, String fieldCd, String effStartDt);

    List<BillStdFieldConfigResponseDto> findEffective(String svcCd);

    BillStdFieldConfigResponseDto create(BillStdFieldConfigRequestDto dto);

    BillStdFieldConfigResponseDto update(String svcCd, String fieldCd, String effStartDt, BillStdFieldConfigRequestDto dto);

    void delete(String svcCd, String fieldCd, String effStartDt);

    BillStdFieldConfigResponseDto expire(String svcCd, String fieldCd, String effStartDt);
}

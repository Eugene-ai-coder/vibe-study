package com.example.vibestudy;

import java.util.List;

public interface CommonCodeService {
    List<CommonCodeResponseDto> findAll(String commonCode, String commonCodeNm);
    CommonCodeResponseDto create(CommonCodeRequestDto dto);
    CommonCodeResponseDto update(String commonCode, CommonCodeRequestDto dto);
    void delete(String commonCode);

    List<CommonDtlCodeResponseDto> findDetails(String commonCode);
    CommonDtlCodeResponseDto createDetail(String commonCode, CommonDtlCodeRequestDto dto);
    CommonDtlCodeResponseDto updateDetail(String commonCode, String dtlCode, CommonDtlCodeRequestDto dto);
    void deleteDetail(String commonCode, String dtlCode);
}

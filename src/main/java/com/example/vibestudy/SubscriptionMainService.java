package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubscriptionMainService {
    Page<SubscriptionMainListResponseDto> findListPage(String svcCd, String searchType, String keyword, Pageable pageable);
    SubscriptionMainResponseDto save(SubscriptionMainRequestDto dto);

    byte[] generateExcel(List<SubscriptionMainRequestDto> items);
    List<SubscriptionMainExcelResponseDto> parseExcel(MultipartFile file);
    List<SubscriptionMainExcelResponseDto> saveBulk(SubscriptionMainBulkRequestDto dto);
}

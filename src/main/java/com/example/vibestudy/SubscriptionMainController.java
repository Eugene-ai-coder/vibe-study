package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-main")
public class SubscriptionMainController {

    private final SubscriptionMainService service;

    public SubscriptionMainController(SubscriptionMainService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SubscriptionMainListResponseDto> getList(
        @RequestParam(required = false) String svcCd,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword,
        Pageable pageable) {
        return service.findListPage(svcCd, searchType, keyword, pageable);
    }

    @PostMapping
    public ResponseEntity<SubscriptionMainResponseDto> save(
        @Valid @RequestBody SubscriptionMainRequestDto dto) {
        return ResponseEntity.status(201).body(service.save(dto));
    }

    @PostMapping("/excel/download")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestBody List<SubscriptionMainRequestDto> items) {
        byte[] bytes = service.generateExcel(items);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=subscription_main.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(bytes);
    }

    @PostMapping("/excel/upload")
    public ResponseEntity<List<SubscriptionMainExcelResponseDto>> uploadExcel(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.parseExcel(file));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SubscriptionMainExcelResponseDto>> saveBulk(
            @RequestBody SubscriptionMainBulkRequestDto dto) {
        return ResponseEntity.ok(service.saveBulk(dto));
    }
}

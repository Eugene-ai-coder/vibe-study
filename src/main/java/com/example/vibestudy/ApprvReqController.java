package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apprv-req")
public class ApprvReqController {

    private final ApprvReqService apprvReqService;

    public ApprvReqController(ApprvReqService apprvReqService) {
        this.apprvReqService = apprvReqService;
    }

    @GetMapping("/list")
    public Page<ApprvReqListResponseDto> getList(
            @RequestParam(required = false) String subsId,
            @RequestParam(required = false) String stdRegStatCd,
            @RequestParam(required = false) String startDt,
            @RequestParam(required = false) String endDt,
            Pageable pageable) {
        return apprvReqService.findList(subsId, stdRegStatCd, startDt, endDt, pageable);
    }

    @PostMapping
    public ResponseEntity<BillStdApprvReqResponseDto> create(
            @Valid @RequestBody BillStdApprvReqRequestDto dto) {
        return ResponseEntity.status(201).body(apprvReqService.createApprvReq(dto));
    }

    @GetMapping("/{apprvReqId}")
    public ResponseEntity<BillStdApprvReqResponseDto> getApprvReq(@PathVariable String apprvReqId) {
        return ResponseEntity.ok(apprvReqService.findByApprvReqId(apprvReqId));
    }
}

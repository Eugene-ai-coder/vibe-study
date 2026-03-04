package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common-codes")
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    public CommonCodeController(CommonCodeService commonCodeService) {
        this.commonCodeService = commonCodeService;
    }

    @GetMapping
    public List<CommonCodeResponseDto> getAll(
            @RequestParam(required = false) String commonCode,
            @RequestParam(required = false) String commonCodeNm) {
        return commonCodeService.findAll(commonCode, commonCodeNm);
    }

    @PostMapping
    public ResponseEntity<CommonCodeResponseDto> create(@Valid @RequestBody CommonCodeRequestDto dto) {
        return ResponseEntity.status(201).body(commonCodeService.create(dto));
    }

    @PutMapping("/{commonCode}")
    public ResponseEntity<CommonCodeResponseDto> update(
            @PathVariable String commonCode,
            @Valid @RequestBody CommonCodeRequestDto dto) {
        return ResponseEntity.ok(commonCodeService.update(commonCode, dto));
    }

    @DeleteMapping("/{commonCode}")
    public ResponseEntity<Void> delete(@PathVariable String commonCode) {
        commonCodeService.delete(commonCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{commonCode}/details")
    public List<CommonDtlCodeResponseDto> getDetails(@PathVariable String commonCode) {
        return commonCodeService.findDetails(commonCode);
    }

    @PostMapping("/{commonCode}/details")
    public ResponseEntity<CommonDtlCodeResponseDto> createDetail(
            @PathVariable String commonCode,
            @Valid @RequestBody CommonDtlCodeRequestDto dto) {
        return ResponseEntity.status(201).body(commonCodeService.createDetail(commonCode, dto));
    }

    @PutMapping("/{commonCode}/details/{dtlCode}")
    public ResponseEntity<CommonDtlCodeResponseDto> updateDetail(
            @PathVariable String commonCode,
            @PathVariable String dtlCode,
            @Valid @RequestBody CommonDtlCodeRequestDto dto) {
        return ResponseEntity.ok(commonCodeService.updateDetail(commonCode, dtlCode, dto));
    }

    @DeleteMapping("/{commonCode}/details/{dtlCode}")
    public ResponseEntity<Void> deleteDetail(
            @PathVariable String commonCode,
            @PathVariable String dtlCode) {
        commonCodeService.deleteDetail(commonCode, dtlCode);
        return ResponseEntity.noContent().build();
    }
}

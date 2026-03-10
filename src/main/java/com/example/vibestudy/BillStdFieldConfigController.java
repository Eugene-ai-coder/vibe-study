package com.example.vibestudy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill-std-field-configs")
public class BillStdFieldConfigController {

    private final BillStdFieldConfigService service;

    public BillStdFieldConfigController(BillStdFieldConfigService service) {
        this.service = service;
    }

    @GetMapping
    public List<BillStdFieldConfigResponseDto> getAll(
            @RequestParam(required = false) String svcCd,
            @RequestParam(required = false) String fieldCd) {
        return service.findAll(svcCd, fieldCd);
    }

    @GetMapping("/{svcCd}/{fieldCd}/{effStartDt}")
    public ResponseEntity<BillStdFieldConfigResponseDto> getById(
            @PathVariable String svcCd,
            @PathVariable String fieldCd,
            @PathVariable String effStartDt) {
        return ResponseEntity.ok(service.findById(svcCd, fieldCd, effStartDt));
    }

    @GetMapping("/effective/{svcCd}")
    public List<BillStdFieldConfigResponseDto> getEffective(@PathVariable String svcCd) {
        return service.findEffective(svcCd);
    }

    @PostMapping
    public ResponseEntity<BillStdFieldConfigResponseDto> create(
            @RequestBody BillStdFieldConfigRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{svcCd}/{fieldCd}/{effStartDt}")
    public ResponseEntity<BillStdFieldConfigResponseDto> update(
            @PathVariable String svcCd,
            @PathVariable String fieldCd,
            @PathVariable String effStartDt,
            @RequestBody BillStdFieldConfigRequestDto dto) {
        return ResponseEntity.ok(service.update(svcCd, fieldCd, effStartDt, dto));
    }

    @DeleteMapping("/{svcCd}/{fieldCd}/{effStartDt}")
    public ResponseEntity<Void> delete(
            @PathVariable String svcCd,
            @PathVariable String fieldCd,
            @PathVariable String effStartDt) {
        service.delete(svcCd, fieldCd, effStartDt);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{svcCd}/{fieldCd}/{effStartDt}/expire")
    public ResponseEntity<BillStdFieldConfigResponseDto> expire(
            @PathVariable String svcCd,
            @PathVariable String fieldCd,
            @PathVariable String effStartDt) {
        return ResponseEntity.ok(service.expire(svcCd, fieldCd, effStartDt));
    }
}

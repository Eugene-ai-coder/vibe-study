package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spcl-subs-mth-bill-qty")
public class SpclSubsMthBillQtyController {

    private final SpclSubsMthBillQtyService service;

    public SpclSubsMthBillQtyController(SpclSubsMthBillQtyService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SpclSubsMthBillQtyResponseDto> getAll(
            @RequestParam(required = false) String spclSubsId,
            Pageable pageable) {
        return service.findPage(spclSubsId, pageable);
    }

    @GetMapping("/{spclSubsId}/{useMth}")
    public ResponseEntity<SpclSubsMthBillQtyResponseDto> getById(
            @PathVariable String spclSubsId,
            @PathVariable String useMth) {
        return ResponseEntity.ok(service.findById(spclSubsId, useMth));
    }

    @PostMapping
    public ResponseEntity<SpclSubsMthBillQtyResponseDto> create(
            @RequestBody SpclSubsMthBillQtyRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{spclSubsId}/{useMth}")
    public ResponseEntity<SpclSubsMthBillQtyResponseDto> update(
            @PathVariable String spclSubsId,
            @PathVariable String useMth,
            @RequestBody SpclSubsMthBillQtyRequestDto dto) {
        return ResponseEntity.ok(service.update(spclSubsId, useMth, dto));
    }

    @DeleteMapping("/{spclSubsId}/{useMth}")
    public ResponseEntity<Void> delete(
            @PathVariable String spclSubsId,
            @PathVariable String useMth) {
        service.delete(spclSubsId, useMth);
        return ResponseEntity.noContent().build();
    }
}

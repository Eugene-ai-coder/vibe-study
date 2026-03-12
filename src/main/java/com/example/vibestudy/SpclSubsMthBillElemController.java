package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spcl-subs-mth-bill-elem")
public class SpclSubsMthBillElemController {

    private final SpclSubsMthBillElemService service;

    public SpclSubsMthBillElemController(SpclSubsMthBillElemService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SpclSubsMthBillElemResponseDto> getAll(
            @RequestParam(required = false) String spclSubsId,
            Pageable pageable) {
        return service.findPage(spclSubsId, pageable);
    }

    @GetMapping("/{spclSubsId}/{billMth}")
    public ResponseEntity<SpclSubsMthBillElemResponseDto> getById(
            @PathVariable String spclSubsId,
            @PathVariable String billMth) {
        return ResponseEntity.ok(service.findById(spclSubsId, billMth));
    }

    @PostMapping
    public ResponseEntity<SpclSubsMthBillElemResponseDto> create(
            @RequestBody SpclSubsMthBillElemRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{spclSubsId}/{billMth}")
    public ResponseEntity<SpclSubsMthBillElemResponseDto> update(
            @PathVariable String spclSubsId,
            @PathVariable String billMth,
            @RequestBody SpclSubsMthBillElemRequestDto dto) {
        return ResponseEntity.ok(service.update(spclSubsId, billMth, dto));
    }

    @DeleteMapping("/{spclSubsId}/{billMth}")
    public ResponseEntity<Void> delete(
            @PathVariable String spclSubsId,
            @PathVariable String billMth) {
        service.delete(spclSubsId, billMth);
        return ResponseEntity.noContent().build();
    }
}

package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill-std")
public class BillStdController {

    private final BillStdService billStdService;

    public BillStdController(BillStdService billStdService) {
        this.billStdService = billStdService;
    }

    @GetMapping
    public List<BillStdResponseDto> getAll() {
        return billStdService.findAll();
    }

    @GetMapping("/by-subs/{subsId}")
    public ResponseEntity<BillStdResponseDto> getBySubsId(@PathVariable String subsId) {
        return ResponseEntity.ok(billStdService.findBySubsId(subsId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillStdResponseDto> getOne(@PathVariable String id) {
        return ResponseEntity.ok(billStdService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BillStdResponseDto> create(@Valid @RequestBody BillStdRequestDto dto) {
        return ResponseEntity.status(201).body(billStdService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillStdResponseDto> update(
            @PathVariable String id,
            @Valid @RequestBody BillStdRequestDto dto) {
        return ResponseEntity.ok(billStdService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        billStdService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

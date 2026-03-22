package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill-std-req")
public class BillStdReqController {

    private final BillStdReqService service;

    public BillStdReqController(BillStdReqService service) {
        this.service = service;
    }

    @GetMapping("/todo")
    public List<BillStdReqResponseDto> getTodoList() {
        return service.findTodoList();
    }

    @GetMapping("/search-by-subs")
    public ResponseEntity<BillStdReqResponseDto> searchBySubsId(@RequestParam String subsId) {
        return ResponseEntity.ok(service.findBySubsId(subsId));
    }

    @GetMapping("/{billStdReqId}")
    public ResponseEntity<BillStdReqResponseDto> getOne(@PathVariable String billStdReqId) {
        return ResponseEntity.ok(service.findCurrentByReqId(billStdReqId));
    }

    @PostMapping
    public ResponseEntity<BillStdReqResponseDto> create(@Valid @RequestBody BillStdReqRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{billStdReqId}")
    public ResponseEntity<BillStdReqResponseDto> save(
            @PathVariable String billStdReqId,
            @Valid @RequestBody BillStdReqRequestDto dto) {
        return ResponseEntity.ok(service.save(billStdReqId, dto));
    }

    @PutMapping("/{billStdReqId}/status")
    public ResponseEntity<BillStdReqResponseDto> changeStatus(
            @PathVariable String billStdReqId,
            @Valid @RequestBody StatusChangeRequestDto dto) {
        return ResponseEntity.ok(service.changeStatus(billStdReqId, dto.getStatus(), dto.getCreatedBy()));
    }

    @DeleteMapping("/{billStdReqId}")
    public ResponseEntity<Void> delete(@PathVariable String billStdReqId) {
        service.delete(billStdReqId);
        return ResponseEntity.noContent().build();
    }
}

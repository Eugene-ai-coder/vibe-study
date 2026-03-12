package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String createdBy = body.get("createdBy");
        return ResponseEntity.ok(service.changeStatus(billStdReqId, status, createdBy));
    }

    @DeleteMapping("/{billStdReqId}")
    public ResponseEntity<Void> delete(@PathVariable String billStdReqId) {
        service.delete(billStdReqId);
        return ResponseEntity.noContent().build();
    }
}

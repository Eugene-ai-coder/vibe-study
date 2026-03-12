package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/special-subscriptions")
public class SpecialSubscriptionController {

    private final SpecialSubscriptionService service;

    public SpecialSubscriptionController(SpecialSubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SpecialSubscriptionResponseDto> getAll(
            @RequestParam(required = false) String subsBillStdId,
            @RequestParam(required = false) String subsId,
            Pageable pageable) {
        return service.findPage(subsBillStdId, subsId, pageable);
    }

    @GetMapping("/{subsBillStdId}/{effStartDt}")
    public ResponseEntity<SpecialSubscriptionResponseDto> getById(
            @PathVariable String subsBillStdId,
            @PathVariable String effStartDt) {
        return ResponseEntity.ok(service.findById(subsBillStdId, effStartDt));
    }

    @PostMapping
    public ResponseEntity<SpecialSubscriptionResponseDto> create(
            @RequestBody SpecialSubscriptionRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{subsBillStdId}/{effStartDt}")
    public ResponseEntity<SpecialSubscriptionResponseDto> update(
            @PathVariable String subsBillStdId,
            @PathVariable String effStartDt,
            @RequestBody SpecialSubscriptionRequestDto dto) {
        return ResponseEntity.ok(service.update(subsBillStdId, effStartDt, dto));
    }

    @DeleteMapping("/{subsBillStdId}/{effStartDt}")
    public ResponseEntity<Void> delete(
            @PathVariable String subsBillStdId,
            @PathVariable String effStartDt) {
        service.delete(subsBillStdId, effStartDt);
        return ResponseEntity.noContent().build();
    }
}

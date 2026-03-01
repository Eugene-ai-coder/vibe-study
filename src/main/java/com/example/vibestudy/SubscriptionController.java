package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public List<SubscriptionResponseDto> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return subscriptionService.search(type, keyword);
    }

    @GetMapping("/{subsId}")
    public ResponseEntity<SubscriptionResponseDto> getOne(@PathVariable String subsId) {
        return ResponseEntity.ok(subscriptionService.findById(subsId));
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> create(
            @Valid @RequestBody SubscriptionRequestDto dto) {
        return ResponseEntity.status(201).body(subscriptionService.create(dto));
    }

    @PutMapping("/{subsId}")
    public ResponseEntity<SubscriptionResponseDto> update(
            @PathVariable String subsId,
            @Valid @RequestBody SubscriptionRequestDto dto) {
        return ResponseEntity.ok(subscriptionService.update(subsId, dto));
    }

    @DeleteMapping("/{subsId}")
    public ResponseEntity<Void> delete(@PathVariable String subsId) {
        subscriptionService.delete(subsId);
        return ResponseEntity.noContent().build();
    }
}

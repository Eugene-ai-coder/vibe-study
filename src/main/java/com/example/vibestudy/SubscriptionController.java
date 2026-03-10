package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public Page<SubscriptionResponseDto> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return subscriptionService.searchPage(type, keyword, pageable);
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

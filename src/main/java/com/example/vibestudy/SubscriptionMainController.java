package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-main")
public class SubscriptionMainController {

    private final SubscriptionMainService service;

    public SubscriptionMainController(SubscriptionMainService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubscriptionMainListResponseDto> getList(
        @RequestParam(required = false) String svcNm,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword) {
        return service.findList(svcNm, searchType, keyword);
    }

    @PostMapping
    public ResponseEntity<SubscriptionMainResponseDto> save(
        @Valid @RequestBody SubscriptionMainRequestDto dto) {
        return ResponseEntity.status(201).body(service.save(dto));
    }
}

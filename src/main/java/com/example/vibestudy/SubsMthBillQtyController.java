package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subs-mth-bill-qty")
public class SubsMthBillQtyController {

    private final SubsMthBillQtyService service;

    public SubsMthBillQtyController(SubsMthBillQtyService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SubsMthBillQtyResponseDto> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String useMthFrom,
            @RequestParam(required = false) String useMthTo,
            Pageable pageable) {
        return service.findPage(keyword, useMthFrom, useMthTo, pageable);
    }
}

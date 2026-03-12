package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subs-bill-std-req")
public class SubsBillStdReqController {

    private final BillStdReqService service;

    public SubsBillStdReqController(BillStdReqService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public Page<SubsBillStdReqResponseDto> getList(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return service.findList(keyword, pageable);
    }
}

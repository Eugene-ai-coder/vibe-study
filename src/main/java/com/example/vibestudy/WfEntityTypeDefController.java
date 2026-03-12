package com.example.vibestudy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wf/entity-types")
public class WfEntityTypeDefController {

    private final WfEntityTypeDefService service;

    public WfEntityTypeDefController(WfEntityTypeDefService service) {
        this.service = service;
    }

    @GetMapping
    public List<WfEntityTypeDefResponseDto> findAll() {
        return service.findAll();
    }
}

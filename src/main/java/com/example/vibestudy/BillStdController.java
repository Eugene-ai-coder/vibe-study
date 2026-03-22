package com.example.vibestudy;

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

}

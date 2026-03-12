package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wf/engine")
public class WfEngineController {

    private final WfEngineService wfEngineService;

    public WfEngineController(WfEngineService wfEngineService) {
        this.wfEngineService = wfEngineService;
    }

    @PostMapping("/start")
    public ResponseEntity<WfProcessInstResponseDto> startProcess(
            @Valid @RequestBody WfStartProcessRequestDto dto) {
        return ResponseEntity.status(201).body(wfEngineService.startProcess(dto));
    }

    @PostMapping("/process-inst/{processInstId}/transition")
    public ResponseEntity<WfProcessInstResponseDto> transition(
            @PathVariable String processInstId,
            @Valid @RequestBody WfTransitionRequestDto dto) {
        return ResponseEntity.ok(wfEngineService.transition(processInstId, dto));
    }

    @GetMapping("/process-inst/{processInstId}")
    public ResponseEntity<WfProcessInstResponseDto> getProcessInstance(
            @PathVariable String processInstId) {
        return ResponseEntity.ok(wfEngineService.getProcessInstance(processInstId));
    }

    @GetMapping("/process-inst")
    public ResponseEntity<WfProcessInstResponseDto> getProcessInstanceByEntity(
            @RequestParam String entityType, @RequestParam String entityId) {
        return ResponseEntity.ok(wfEngineService.getProcessInstanceByEntity(entityType, entityId));
    }
}

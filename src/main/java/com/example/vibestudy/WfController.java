package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wf")
public class WfController {

    private final WfService wfService;

    public WfController(WfService wfService) {
        this.wfService = wfService;
    }

    // ── 프로세스 정의 ──────────────────────────────────────────

    @GetMapping("/process-def")
    public List<WfProcessDefResponseDto> getAllProcessDefs() {
        return wfService.findAllProcessDefs();
    }

    @GetMapping("/process-def/{id}")
    public ResponseEntity<WfProcessDefResponseDto> getProcessDef(@PathVariable String id) {
        return ResponseEntity.ok(wfService.findProcessDefById(id));
    }

    @PostMapping("/process-def")
    public ResponseEntity<WfProcessDefResponseDto> createProcessDef(
            @Valid @RequestBody WfProcessDefRequestDto dto) {
        return ResponseEntity.status(201).body(wfService.createProcessDef(dto));
    }

    @PutMapping("/process-def/{id}")
    public ResponseEntity<WfProcessDefResponseDto> updateProcessDef(
            @PathVariable String id, @Valid @RequestBody WfProcessDefRequestDto dto) {
        return ResponseEntity.ok(wfService.updateProcessDef(id, dto));
    }

    @DeleteMapping("/process-def/{id}")
    public ResponseEntity<Void> deleteProcessDef(@PathVariable String id) {
        wfService.deleteProcessDef(id);
        return ResponseEntity.noContent().build();
    }

    // ── 상태 정의 (부모-자식 패턴: /process-def/{pid}/states) ──

    @GetMapping("/process-def/{processDefId}/states")
    public List<WfStateDefResponseDto> getStates(@PathVariable String processDefId) {
        return wfService.findStatesByProcessDefId(processDefId);
    }

    @PostMapping("/process-def/{processDefId}/states")
    public ResponseEntity<WfStateDefResponseDto> createState(
            @PathVariable String processDefId,
            @Valid @RequestBody WfStateDefRequestDto dto) {
        return ResponseEntity.status(201).body(wfService.createStateDef(processDefId, dto));
    }

    @PutMapping("/states/{id}")
    public ResponseEntity<WfStateDefResponseDto> updateState(
            @PathVariable String id, @Valid @RequestBody WfStateDefRequestDto dto) {
        return ResponseEntity.ok(wfService.updateStateDef(id, dto));
    }

    @DeleteMapping("/states/{id}")
    public ResponseEntity<Void> deleteState(@PathVariable String id) {
        wfService.deleteStateDef(id);
        return ResponseEntity.noContent().build();
    }

    // ── 전이 정의 (/process-def/{pid}/transitions) ────────────

    @GetMapping("/process-def/{processDefId}/transitions")
    public List<WfTransitionDefResponseDto> getTransitions(@PathVariable String processDefId) {
        return wfService.findTransitionsByProcessDefId(processDefId);
    }

    @PostMapping("/process-def/{processDefId}/transitions")
    public ResponseEntity<WfTransitionDefResponseDto> createTransition(
            @PathVariable String processDefId,
            @Valid @RequestBody WfTransitionDefRequestDto dto) {
        return ResponseEntity.status(201).body(wfService.createTransitionDef(processDefId, dto));
    }

    @PutMapping("/transitions/{id}")
    public ResponseEntity<WfTransitionDefResponseDto> updateTransition(
            @PathVariable String id, @Valid @RequestBody WfTransitionDefRequestDto dto) {
        return ResponseEntity.ok(wfService.updateTransitionDef(id, dto));
    }

    @DeleteMapping("/transitions/{id}")
    public ResponseEntity<Void> deleteTransition(@PathVariable String id) {
        wfService.deleteTransitionDef(id);
        return ResponseEntity.noContent().build();
    }

    // ── Task 템플릿 (/states/{sid}/task-templates) ────────────

    @GetMapping("/states/{stateDefId}/task-templates")
    public List<WfTaskTemplateResponseDto> getTaskTemplates(@PathVariable String stateDefId) {
        return wfService.findTaskTemplatesByStateDefId(stateDefId);
    }

    @PostMapping("/states/{stateDefId}/task-templates")
    public ResponseEntity<WfTaskTemplateResponseDto> createTaskTemplate(
            @PathVariable String stateDefId,
            @Valid @RequestBody WfTaskTemplateRequestDto dto) {
        return ResponseEntity.status(201).body(wfService.createTaskTemplate(stateDefId, dto));
    }

    @PutMapping("/task-templates/{id}")
    public ResponseEntity<WfTaskTemplateResponseDto> updateTaskTemplate(
            @PathVariable String id, @Valid @RequestBody WfTaskTemplateRequestDto dto) {
        return ResponseEntity.ok(wfService.updateTaskTemplate(id, dto));
    }

    @DeleteMapping("/task-templates/{id}")
    public ResponseEntity<Void> deleteTaskTemplate(@PathVariable String id) {
        wfService.deleteTaskTemplate(id);
        return ResponseEntity.noContent().build();
    }
}

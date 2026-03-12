package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wf/tasks")
public class WfTaskController {

    private final WfTaskService wfTaskService;

    public WfTaskController(WfTaskService wfTaskService) {
        this.wfTaskService = wfTaskService;
    }

    @GetMapping("/my")
    public List<WfTaskInstResponseDto> getMyTasks(
            @RequestParam(required = false) String assigneeId,
            @RequestParam(required = false) String status) {
        return wfTaskService.getMyTasks(assigneeId, status);
    }

    @GetMapping("/process-inst/{processInstId}")
    public List<WfTaskInstResponseDto> getTasksByProcessInst(
            @PathVariable String processInstId) {
        return wfTaskService.getTasksByProcessInst(processInstId);
    }

    @PostMapping("/{taskInstId}/claim")
    public ResponseEntity<WfTaskInstResponseDto> claimTask(
            @PathVariable String taskInstId,
            @Valid @RequestBody WfClaimTaskRequestDto dto) {
        return ResponseEntity.ok(wfTaskService.claimTask(taskInstId, dto));
    }

    @PostMapping("/{taskInstId}/complete")
    public ResponseEntity<WfTaskInstResponseDto> completeTask(
            @PathVariable String taskInstId,
            @Valid @RequestBody WfCompleteTaskRequestDto dto) {
        return ResponseEntity.ok(wfTaskService.completeTask(taskInstId, dto));
    }
}

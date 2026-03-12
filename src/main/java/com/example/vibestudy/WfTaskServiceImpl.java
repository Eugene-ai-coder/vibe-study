package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WfTaskServiceImpl implements WfTaskService {

    private final WfTaskInstRepository taskInstRepo;
    private final WfEngineService wfEngineService;
    private final WfStateDefRepository stateDefRepo;

    public WfTaskServiceImpl(WfTaskInstRepository taskInstRepo,
                             WfEngineService wfEngineService,
                             WfStateDefRepository stateDefRepo) {
        this.taskInstRepo = taskInstRepo;
        this.wfEngineService = wfEngineService;
        this.stateDefRepo = stateDefRepo;
    }

    // ── getMyTasks ────────────────────────────────────────────────

    @Override
    public List<WfTaskInstResponseDto> getMyTasks(String assigneeId, String status) {
        List<WfTaskInst> tasks;
        if (assigneeId != null && !assigneeId.isBlank()) {
            if (status != null && !status.isBlank()) {
                tasks = taskInstRepo.findByAssigneeIdAndStatusOrderByPriorityAscCreatedDtAsc(assigneeId, status);
            } else {
                tasks = taskInstRepo.findByAssigneeIdOrderByPriorityAscCreatedDtAsc(assigneeId);
            }
        } else {
            if (status != null && !status.isBlank()) {
                tasks = taskInstRepo.findByStatusOrderByPriorityAscCreatedDtAsc(status);
            } else {
                tasks = taskInstRepo.findAllByOrderByPriorityAscCreatedDtAsc();
            }
        }
        Map<String, String> stateNmMap = buildStateNmMap(tasks);
        return tasks.stream().map(t -> WfTaskInstResponseDto.from(t, stateNmMap)).toList();
    }

    // ── getTasksByProcessInst ─────────────────────────────────────

    @Override
    public List<WfTaskInstResponseDto> getTasksByProcessInst(String processInstId) {
        List<WfTaskInst> tasks = taskInstRepo.findByProcessInstIdOrderByCreatedDt(processInstId);
        Map<String, String> stateNmMap = buildStateNmMap(tasks);
        return tasks.stream().map(t -> WfTaskInstResponseDto.from(t, stateNmMap)).toList();
    }

    // ── claimTask ─────────────────────────────────────────────────

    @Override
    @Transactional
    public WfTaskInstResponseDto claimTask(String taskInstId, WfClaimTaskRequestDto dto) {
        WfTaskInst task = findTaskInstOrThrow(taskInstId);

        if (!WfStatus.PENDING.equals(task.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "PENDING 상태의 Task만 Claim할 수 있습니다: " + task.getStatus());
        }

        task.setStatus(WfStatus.IN_PROGRESS);
        task.setAssigneeId(dto.getCreatedBy());
        task.setUpdatedBy(dto.getCreatedBy());
        task.setUpdatedDt(LocalDateTime.now());
        taskInstRepo.save(task);

        return WfTaskInstResponseDto.from(task, buildStateNmMap(List.of(task)));
    }

    // ── completeTask (자동 전이 포함) ─────────────────────────────

    @Override
    @Transactional
    public WfTaskInstResponseDto completeTask(String taskInstId, WfCompleteTaskRequestDto dto) {
        WfTaskInst task = findTaskInstOrThrow(taskInstId);

        if (!WfStatus.IN_PROGRESS.equals(task.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "IN_PROGRESS 상태의 Task만 완료할 수 있습니다: " + task.getStatus());
        }

        // 완료 처리
        task.setStatus(WfStatus.COMPLETED);
        task.setResult(dto.getResult());
        task.setComment(dto.getComment());
        task.setCompletedBy(dto.getCreatedBy());
        task.setCompletedDt(LocalDateTime.now());
        task.setUpdatedBy(dto.getCreatedBy());
        task.setUpdatedDt(LocalDateTime.now());
        taskInstRepo.save(task);

        // 자동 전이 판단: 같은 processInstId + stateDefId에서 미완료 Task 존재 여부
        List<WfTaskInst> remainingTasks = taskInstRepo
                .findByProcessInstIdAndStateDefIdAndStatusNotIn(
                        task.getProcessInstId(), task.getStateDefId(),
                        List.of(WfStatus.COMPLETED, WfStatus.CANCELLED));

        if (remainingTasks.isEmpty()) {
            // result 기반 자동 전이 시도
            String transitionCode = mapResultToTransitionCode(dto.getResult());
            if (transitionCode != null) {
                try {
                    wfEngineService.performTransition(
                            task.getProcessInstId(), transitionCode,
                            dto.getCreatedBy(), dto.getComment());
                } catch (ResponseStatusException e) {
                    // 매칭 TransitionDef 없으면 예외 무시 (전이 없이 완료만 처리)
                }
            }
        }

        return WfTaskInstResponseDto.from(task, buildStateNmMap(List.of(task)));
    }

    // ── result → transitionCode 매핑 ──────────────────────────────

    private String mapResultToTransitionCode(String result) {
        if (result == null) return null;
        return switch (result) {
            case WfStatus.RESULT_APPROVED -> WfStatus.TRANSITION_APPROVE;
            case WfStatus.RESULT_REJECTED -> WfStatus.TRANSITION_REJECT;
            case WfStatus.RESULT_DONE -> WfStatus.TRANSITION_SUBMIT;
            default -> null;
        };
    }

    // ── findOrThrow ──────────────────────────────────────────────

    private WfTaskInst findTaskInstOrThrow(String id) {
        return taskInstRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task 인스턴스를 찾을 수 없습니다: " + id));
    }

    // ── buildStateNmMap (batch) ─────────────────────────────────

    private Map<String, String> buildStateNmMap(List<WfTaskInst> tasks) {
        List<String> stateDefIds = tasks.stream()
                .map(WfTaskInst::getStateDefId)
                .distinct()
                .toList();
        return stateDefRepo.findAllById(stateDefIds).stream()
                .collect(Collectors.toMap(WfStateDef::getStateDefId, WfStateDef::getStateNm));
    }

}

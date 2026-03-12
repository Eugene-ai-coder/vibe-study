package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WfEngineServiceImpl implements WfEngineService {

    private final WfProcessInstRepository processInstRepo;
    private final WfTaskInstRepository taskInstRepo;
    private final WfTransitionLogRepository transitionLogRepo;
    private final WfProcessDefRepository processDefRepo;
    private final WfStateDefRepository stateDefRepo;
    private final WfTransitionDefRepository transitionDefRepo;
    private final WfTaskTemplateRepository taskTemplateRepo;
    private final WfEntityTypeDefRepository entityTypeDefRepo;
    private final JdbcTemplate jdbcTemplate;

    public WfEngineServiceImpl(WfProcessInstRepository processInstRepo,
                               WfTaskInstRepository taskInstRepo,
                               WfTransitionLogRepository transitionLogRepo,
                               WfProcessDefRepository processDefRepo,
                               WfStateDefRepository stateDefRepo,
                               WfTransitionDefRepository transitionDefRepo,
                               WfTaskTemplateRepository taskTemplateRepo,
                               WfEntityTypeDefRepository entityTypeDefRepo,
                               JdbcTemplate jdbcTemplate) {
        this.processInstRepo = processInstRepo;
        this.taskInstRepo = taskInstRepo;
        this.transitionLogRepo = transitionLogRepo;
        this.processDefRepo = processDefRepo;
        this.stateDefRepo = stateDefRepo;
        this.transitionDefRepo = transitionDefRepo;
        this.taskTemplateRepo = taskTemplateRepo;
        this.entityTypeDefRepo = entityTypeDefRepo;
        this.jdbcTemplate = jdbcTemplate;
    }

    // ── startProcess ──────────────────────────────────────────────

    @Override
    @Transactional
    public WfProcessInstResponseDto startProcess(WfStartProcessRequestDto dto) {
        // 1. entityType + use_yn='Y' 프로세스 정의 조회
        List<WfProcessDef> activeDefs = processDefRepo.findByEntityTypeAndUseYn(dto.getEntityType(), "Y");
        if (activeDefs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "활성 프로세스 정의가 없습니다: " + dto.getEntityType());
        }
        if (activeDefs.size() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "동일 업무유형에 활성 프로세스 정의가 복수 존재합니다: " + dto.getEntityType());
        }
        WfProcessDef processDef = activeDefs.get(0);

        // 2. 동일 entity에 ACTIVE 인스턴스 중복 체크
        if (processInstRepo.existsByEntityTypeAndEntityIdAndStatus(
                dto.getEntityType(), dto.getEntityId(), WfStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "동일 업무 건에 진행 중인 프로세스가 이미 존재합니다.");
        }

        // 3. START 타입 상태 조회
        List<WfStateDef> startStates = stateDefRepo.findByProcessDefIdAndStateType(
                processDef.getProcessDefId(), WfStatus.STATE_START);
        if (startStates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "시작 상태가 정의되지 않았습니다.");
        }
        WfStateDef startState = startStates.get(0);

        // 4. ProcessInst 생성
        WfProcessInst inst = new WfProcessInst();
        inst.setProcessInstId(generateProcessInstId());
        inst.setProcessDefId(processDef.getProcessDefId());
        inst.setCurrentStateDefId(startState.getStateDefId());
        inst.setEntityType(dto.getEntityType());
        inst.setEntityId(dto.getEntityId());
        inst.setStatus(WfStatus.ACTIVE);
        inst.setStartedBy(dto.getCreatedBy());
        inst.setCreatedBy(dto.getCreatedBy());
        inst.setCreatedDt(LocalDateTime.now());
        processInstRepo.save(inst);

        // 5. 시작 상태의 TaskTemplate으로 TaskInst 자동 생성
        createTasksForState(inst, startState.getStateDefId(), dto.getCreatedBy());

        // 6. TransitionLog 기록 (시작: from=to=시작상태)
        createTransitionLog(inst.getProcessInstId(), null,
                startState.getStateDefId(), startState.getStateDefId(),
                "프로세스 시작", dto.getCreatedBy());

        return toProcessInstDto(inst);
    }

    // ── transition ────────────────────────────────────────────────

    @Override
    @Transactional
    public WfProcessInstResponseDto transition(String processInstId, WfTransitionRequestDto dto) {
        return performTransition(processInstId, dto.getTransitionCode(),
                dto.getCreatedBy(), dto.getComment());
    }

    // ── performTransition (자동 전이 공용) ────────────────────────

    @Override
    @Transactional
    public WfProcessInstResponseDto performTransition(String processInstId,
                                                       String transitionCode,
                                                       String performedBy,
                                                       String comment) {
        // 1. ProcessInst 조회 + ACTIVE 확인
        WfProcessInst inst = findProcessInstOrThrow(processInstId);
        if (!WfStatus.ACTIVE.equals(inst.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "활성 상태가 아닌 프로세스입니다: " + inst.getStatus());
        }

        // 2. 현재 상태에서 transitionCode에 해당하는 TransitionDef 조회
        String currentStateDefId = inst.getCurrentStateDefId();
        List<WfTransitionDef> transitions = transitionDefRepo
                .findByFromStateDefIdAndTransitionCode(currentStateDefId, transitionCode);
        if (transitions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "유효하지 않은 전이입니다: " + transitionCode);
        }
        WfTransitionDef transitionDef = transitions.get(0);
        String toStateDefId = transitionDef.getToStateDefId();

        // 3. 이전 상태의 미완료 Task들 CANCELLED 처리
        List<WfTaskInst> pendingTasks = taskInstRepo
                .findByProcessInstIdAndStateDefIdAndStatusNotIn(
                        processInstId, currentStateDefId,
                        List.of(WfStatus.COMPLETED, WfStatus.CANCELLED));
        LocalDateTime now = LocalDateTime.now();
        for (WfTaskInst task : pendingTasks) {
            task.setStatus(WfStatus.CANCELLED);
            task.setUpdatedBy(performedBy);
            task.setUpdatedDt(now);
        }
        if (!pendingTasks.isEmpty()) {
            taskInstRepo.saveAll(pendingTasks);
        }

        // 4. currentStateDefId를 toState로 변경
        inst.setCurrentStateDefId(toStateDefId);
        inst.setUpdatedBy(performedBy);
        inst.setUpdatedDt(LocalDateTime.now());

        // 4-1. 도착 상태의 entity_status_cd가 있으면 엔티티 상태 동기화
        WfStateDef toState = stateDefRepo.findById(toStateDefId).orElse(null);
        if (toState != null && toState.getEntityStatusCd() != null) {
            updateEntityStatus(inst.getEntityType(), inst.getEntityId(), toState.getEntityStatusCd());
        }

        // 5. toState가 END 타입인지 확인
        List<WfStateDef> endCheck = stateDefRepo.findByProcessDefIdAndStateType(
                inst.getProcessDefId(), WfStatus.STATE_END);
        boolean isEnd = endCheck.stream()
                .anyMatch(s -> s.getStateDefId().equals(toStateDefId));

        if (isEnd) {
            inst.setStatus(WfStatus.COMPLETED);
            inst.setCompletedDt(LocalDateTime.now());
        } else {
            createTasksForState(inst, toStateDefId, performedBy);
        }
        processInstRepo.save(inst);

        // 6. TransitionLog 기록
        createTransitionLog(processInstId, transitionDef.getTransitionDefId(),
                currentStateDefId, toStateDefId, comment, performedBy);

        return toProcessInstDto(inst);
    }

    // ── getProcessInstance ────────────────────────────────────────

    @Override
    public WfProcessInstResponseDto getProcessInstance(String processInstId) {
        WfProcessInst inst = findProcessInstOrThrow(processInstId);
        return toProcessInstDto(inst);
    }

    // ── getProcessInstanceByEntity ────────────────────────────────

    @Override
    public WfProcessInstResponseDto getProcessInstanceByEntity(String entityType, String entityId) {
        List<WfProcessInst> instances = processInstRepo.findByEntityTypeAndEntityId(entityType, entityId);
        if (instances.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "프로세스 인스턴스를 찾을 수 없습니다: " + entityType + "/" + entityId);
        }
        return toProcessInstDto(instances.get(0));
    }

    // ── 내부: Task 자동 생성 ──────────────────────────────────────

    private void createTasksForState(WfProcessInst processInst, String stateDefId, String createdBy) {
        List<WfTaskTemplate> templates = taskTemplateRepo.findByStateDefIdOrderBySortOrder(stateDefId);
        LocalDateTime now = LocalDateTime.now();
        List<WfTaskInst> tasks = templates.stream().map(tmpl -> {
            WfTaskInst task = new WfTaskInst();
            task.setTaskInstId(generateTaskInstId());
            task.setProcessInstId(processInst.getProcessInstId());
            task.setTaskTemplateId(tmpl.getTaskTemplateId());
            task.setStateDefId(stateDefId);
            task.setTaskNm(tmpl.getTaskNm());
            task.setTaskType(tmpl.getTaskType());
            task.setPriority(tmpl.getPriority() != null ? tmpl.getPriority() : 5);
            task.setStatus(WfStatus.PENDING);

            if (WfStatus.ASSIGNEE_USER.equals(tmpl.getAssigneeType())) {
                task.setAssigneeId(tmpl.getAssigneeValue());
            } else if (WfStatus.ASSIGNEE_FIELD.equals(tmpl.getAssigneeType())) {
                String fieldAssignee = resolveFieldAssignee(
                        processInst.getEntityType(), processInst.getEntityId(), tmpl.getAssigneeValue());
                task.setAssigneeId(fieldAssignee);
            }

            if (tmpl.getSlaHours() != null) {
                task.setDueDt(now.plusHours(tmpl.getSlaHours()));
            }

            task.setCreatedBy(createdBy);
            task.setCreatedDt(now);
            return task;
        }).toList();
        if (!tasks.isEmpty()) {
            taskInstRepo.saveAll(tasks);
        }
    }

    // ── 내부: 엔티티 상태 동기화 (동적 UPDATE) ──────────────────

    private void updateEntityStatus(String entityType, String entityId, String statusCd) {
        WfEntityTypeDef meta = entityTypeDefRepo.findById(entityType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "엔티티 유형을 찾을 수 없습니다: " + entityType));
        if (meta.getStatusColumn() == null) {
            return; // 상태 컬럼이 없는 엔티티는 스킵
        }
        String sql = "UPDATE " + meta.getTableNm()
                + " SET " + meta.getStatusColumn() + " = ?"
                + " WHERE " + meta.getPkColumn() + " = ?";
        jdbcTemplate.update(sql, statusCd, entityId);
    }

    // ── 내부: FIELD 담당자 조회 (동적 SELECT) ──────────────────

    private String resolveFieldAssignee(String entityType, String entityId, String fieldName) {
        WfEntityTypeDef meta = entityTypeDefRepo.findById(entityType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "엔티티 유형을 찾을 수 없습니다: " + entityType));
        String sql = "SELECT " + fieldName
                + " FROM " + meta.getTableNm()
                + " WHERE " + meta.getPkColumn() + " = ?";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, entityId);
        return results.isEmpty() ? null : results.get(0);
    }

    // ── 내부: TransitionLog 기록 ──────────────────────────────────

    private void createTransitionLog(String processInstId, String transitionDefId,
                                      String fromStateDefId, String toStateDefId,
                                      String remark, String createdBy) {
        WfTransitionLog log = new WfTransitionLog();
        log.setTransitionLogId(generateTransitionLogId());
        log.setProcessInstId(processInstId);
        log.setTransitionDefId(transitionDefId);
        log.setFromStateDefId(fromStateDefId);
        log.setToStateDefId(toStateDefId);
        log.setTransitionedDt(LocalDateTime.now());
        log.setRemark(remark);
        log.setCreatedBy(createdBy);
        log.setCreatedDt(LocalDateTime.now());
        transitionLogRepo.save(log);
    }

    // ── ID 생성 ──────────────────────────────────────────────────

    private String generateProcessInstId() { return WfIdGenerator.generate("WI"); }
    private String generateTaskInstId()    { return WfIdGenerator.generate("TI"); }
    private String generateTransitionLogId() { return WfIdGenerator.generate("WL"); }

    // ── findOrThrow ──────────────────────────────────────────────

    private WfProcessInst findProcessInstOrThrow(String id) {
        return processInstRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "프로세스 인스턴스를 찾을 수 없습니다: " + id));
    }

    // ── toDto (private) ──────────────────────────────────────────

    private WfProcessInstResponseDto toProcessInstDto(WfProcessInst e) {
        WfProcessInstResponseDto dto = new WfProcessInstResponseDto();
        dto.setProcessInstId(e.getProcessInstId());
        dto.setProcessDefId(e.getProcessDefId());
        dto.setCurrentStateDefId(e.getCurrentStateDefId());
        dto.setEntityType(e.getEntityType());
        dto.setEntityId(e.getEntityId());
        dto.setStatus(e.getStatus());
        dto.setStartedBy(e.getStartedBy());
        dto.setCompletedDt(e.getCompletedDt());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());

        // 프로세스명 조인
        processDefRepo.findById(e.getProcessDefId())
                .ifPresent(pd -> dto.setProcessNm(pd.getProcessNm()));

        // Task, Log 목록 조회
        List<WfTaskInst> tasks = taskInstRepo.findByProcessInstIdOrderByCreatedDt(e.getProcessInstId());
        List<WfTransitionLog> logs = transitionLogRepo.findByProcessInstIdOrderByTransitionedDt(e.getProcessInstId());

        // 상태명 일괄 조회 (N+1 방지)
        Set<String> stateDefIds = Stream.of(
                Stream.ofNullable(e.getCurrentStateDefId()),
                tasks.stream().map(WfTaskInst::getStateDefId),
                logs.stream().flatMap(l -> Stream.of(l.getFromStateDefId(), l.getToStateDefId()))
        ).flatMap(s -> s).filter(id -> id != null).collect(Collectors.toSet());

        Map<String, String> stateNmMap = stateDefRepo.findAllById(stateDefIds).stream()
                .collect(Collectors.toMap(WfStateDef::getStateDefId, WfStateDef::getStateNm));

        // 현재상태명
        dto.setCurrentStateNm(stateNmMap.get(e.getCurrentStateDefId()));

        // Task 목록
        dto.setTasks(tasks.stream().map(t -> WfTaskInstResponseDto.from(t, stateNmMap)).toList());

        // 전이 로그
        dto.setLogs(logs.stream().map(l -> toTransitionLogDto(l, stateNmMap)).toList());

        return dto;
    }

    private WfTransitionLogResponseDto toTransitionLogDto(WfTransitionLog e, Map<String, String> stateNmMap) {
        WfTransitionLogResponseDto dto = new WfTransitionLogResponseDto();
        dto.setTransitionLogId(e.getTransitionLogId());
        dto.setProcessInstId(e.getProcessInstId());
        dto.setTransitionDefId(e.getTransitionDefId());
        dto.setFromStateDefId(e.getFromStateDefId());
        dto.setToStateDefId(e.getToStateDefId());
        dto.setTransitionedDt(e.getTransitionedDt());
        dto.setRemark(e.getRemark());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setFromStateNm(stateNmMap.get(e.getFromStateDefId()));
        dto.setToStateNm(stateNmMap.get(e.getToStateDefId()));
        return dto;
    }
}

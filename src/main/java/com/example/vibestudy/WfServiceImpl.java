package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WfServiceImpl implements WfService {

    private final WfProcessDefRepository processDefRepo;
    private final WfStateDefRepository stateDefRepo;
    private final WfTransitionDefRepository transitionDefRepo;
    private final WfTaskTemplateRepository taskTemplateRepo;

    public WfServiceImpl(WfProcessDefRepository processDefRepo,
                         WfStateDefRepository stateDefRepo,
                         WfTransitionDefRepository transitionDefRepo,
                         WfTaskTemplateRepository taskTemplateRepo) {
        this.processDefRepo = processDefRepo;
        this.stateDefRepo = stateDefRepo;
        this.transitionDefRepo = transitionDefRepo;
        this.taskTemplateRepo = taskTemplateRepo;
    }

    // ── 프로세스 정의 CRUD ──────────────────────────────────────

    @Override
    public List<WfProcessDefResponseDto> findAllProcessDefs() {
        return processDefRepo.findAll().stream()
                .map(this::toProcessDefDto)
                .toList();
    }

    @Override
    public WfProcessDefResponseDto findProcessDefById(String processDefId) {
        WfProcessDef entity = findProcessDefOrThrow(processDefId);
        WfProcessDefResponseDto dto = toProcessDefDto(entity);

        List<WfStateDefResponseDto> states = stateDefRepo
                .findByProcessDefIdOrderBySortOrder(processDefId).stream()
                .map(s -> {
                    WfStateDefResponseDto sDto = toStateDefDto(s);
                    sDto.setTaskTemplates(
                        taskTemplateRepo.findByStateDefIdOrderBySortOrder(s.getStateDefId())
                            .stream().map(this::toTaskTemplateDto).toList()
                    );
                    return sDto;
                }).toList();
        dto.setStates(states);

        dto.setTransitions(
            transitionDefRepo.findByProcessDefIdOrderBySortOrder(processDefId)
                .stream().map(this::toTransitionDefDto).toList()
        );

        return dto;
    }

    @Override
    @Transactional
    public WfProcessDefResponseDto createProcessDef(WfProcessDefRequestDto dto) {
        WfProcessDef entity = new WfProcessDef();
        entity.setProcessDefId(generateProcessDefId());
        entity.setProcessNm(dto.getProcessNm());
        entity.setProcessDesc(dto.getProcessDesc());
        entity.setEntityType(dto.getEntityType());
        entity.setUseYn(dto.getUseYn() != null ? dto.getUseYn() : "Y");
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toProcessDefDto(processDefRepo.save(entity));
    }

    @Override
    @Transactional
    public WfProcessDefResponseDto updateProcessDef(String processDefId, WfProcessDefRequestDto dto) {
        WfProcessDef entity = findProcessDefOrThrow(processDefId);
        entity.setProcessNm(dto.getProcessNm());
        entity.setProcessDesc(dto.getProcessDesc());
        entity.setEntityType(dto.getEntityType());
        if (dto.getUseYn() != null) {
            entity.setUseYn(dto.getUseYn());
        }
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toProcessDefDto(processDefRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteProcessDef(String processDefId) {
        findProcessDefOrThrow(processDefId);
        if (stateDefRepo.existsByProcessDefId(processDefId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "하위 상태 정의가 존재하여 삭제할 수 없습니다.");
        }
        if (transitionDefRepo.existsByProcessDefId(processDefId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "하위 전이 정의가 존재하여 삭제할 수 없습니다.");
        }
        processDefRepo.deleteById(processDefId);
    }

    // ── 상태 정의 CRUD ──────────────────────────────────────────

    @Override
    public List<WfStateDefResponseDto> findStatesByProcessDefId(String processDefId) {
        return stateDefRepo.findByProcessDefIdOrderBySortOrder(processDefId).stream()
                .map(this::toStateDefDto)
                .toList();
    }

    @Override
    @Transactional
    public WfStateDefResponseDto createStateDef(String processDefId, WfStateDefRequestDto dto) {
        findProcessDefOrThrow(processDefId);
        WfStateDef entity = new WfStateDef();
        entity.setStateDefId(generateStateDefId());
        entity.setProcessDefId(processDefId);
        entity.setStateNm(dto.getStateNm());
        entity.setStateType(dto.getStateType());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setEntityStatusCd(dto.getEntityStatusCd());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toStateDefDto(stateDefRepo.save(entity));
    }

    @Override
    @Transactional
    public WfStateDefResponseDto updateStateDef(String stateDefId, WfStateDefRequestDto dto) {
        WfStateDef entity = findStateDefOrThrow(stateDefId);
        entity.setStateNm(dto.getStateNm());
        entity.setStateType(dto.getStateType());
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setEntityStatusCd(dto.getEntityStatusCd());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toStateDefDto(stateDefRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteStateDef(String stateDefId) {
        findStateDefOrThrow(stateDefId);
        if (taskTemplateRepo.existsByStateDefId(stateDefId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "하위 Task 템플릿이 존재하여 삭제할 수 없습니다.");
        }
        if (transitionDefRepo.existsByFromStateDefIdOrToStateDefId(stateDefId, stateDefId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "전이 정의에서 참조 중이므로 삭제할 수 없습니다.");
        }
        stateDefRepo.deleteById(stateDefId);
    }

    // ── 전이 정의 CRUD ──────────────────────────────────────────

    @Override
    public List<WfTransitionDefResponseDto> findTransitionsByProcessDefId(String processDefId) {
        return transitionDefRepo.findByProcessDefIdOrderBySortOrder(processDefId).stream()
                .map(this::toTransitionDefDto)
                .toList();
    }

    @Override
    @Transactional
    public WfTransitionDefResponseDto createTransitionDef(String processDefId, WfTransitionDefRequestDto dto) {
        findProcessDefOrThrow(processDefId);
        WfTransitionDef entity = new WfTransitionDef();
        entity.setTransitionDefId(generateTransitionDefId());
        entity.setProcessDefId(processDefId);
        entity.setFromStateDefId(dto.getFromStateDefId());
        entity.setToStateDefId(dto.getToStateDefId());
        entity.setTransitionCode(dto.getTransitionCode());
        entity.setEventNm(dto.getEventNm());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toTransitionDefDto(transitionDefRepo.save(entity));
    }

    @Override
    @Transactional
    public WfTransitionDefResponseDto updateTransitionDef(String transitionDefId, WfTransitionDefRequestDto dto) {
        WfTransitionDef entity = findTransitionDefOrThrow(transitionDefId);
        entity.setFromStateDefId(dto.getFromStateDefId());
        entity.setToStateDefId(dto.getToStateDefId());
        entity.setTransitionCode(dto.getTransitionCode());
        entity.setEventNm(dto.getEventNm());
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toTransitionDefDto(transitionDefRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteTransitionDef(String transitionDefId) {
        findTransitionDefOrThrow(transitionDefId);
        transitionDefRepo.deleteById(transitionDefId);
    }

    // ── Task 템플릿 CRUD ────────────────────────────────────────

    @Override
    public List<WfTaskTemplateResponseDto> findTaskTemplatesByStateDefId(String stateDefId) {
        return taskTemplateRepo.findByStateDefIdOrderBySortOrder(stateDefId).stream()
                .map(this::toTaskTemplateDto)
                .toList();
    }

    @Override
    @Transactional
    public WfTaskTemplateResponseDto createTaskTemplate(String stateDefId, WfTaskTemplateRequestDto dto) {
        findStateDefOrThrow(stateDefId);
        WfTaskTemplate entity = new WfTaskTemplate();
        entity.setTaskTemplateId(generateTaskTemplateId());
        entity.setStateDefId(stateDefId);
        entity.setTaskNm(dto.getTaskNm());
        entity.setTaskDesc(dto.getTaskDesc());
        entity.setAssigneeType(dto.getAssigneeType());
        entity.setAssigneeValue(dto.getAssigneeValue());
        entity.setTaskType(dto.getTaskType());
        entity.setPriority(dto.getPriority());
        entity.setSlaHours(dto.getSlaHours());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toTaskTemplateDto(taskTemplateRepo.save(entity));
    }

    @Override
    @Transactional
    public WfTaskTemplateResponseDto updateTaskTemplate(String taskTemplateId, WfTaskTemplateRequestDto dto) {
        WfTaskTemplate entity = findTaskTemplateOrThrow(taskTemplateId);
        entity.setTaskNm(dto.getTaskNm());
        entity.setTaskDesc(dto.getTaskDesc());
        entity.setAssigneeType(dto.getAssigneeType());
        entity.setAssigneeValue(dto.getAssigneeValue());
        entity.setTaskType(dto.getTaskType());
        entity.setPriority(dto.getPriority());
        entity.setSlaHours(dto.getSlaHours());
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toTaskTemplateDto(taskTemplateRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteTaskTemplate(String taskTemplateId) {
        findTaskTemplateOrThrow(taskTemplateId);
        taskTemplateRepo.deleteById(taskTemplateId);
    }

    // ── ID 생성 ──────────────────────────────────────────────────

    private String generateProcessDefId() { return WfIdGenerator.generate("WP"); }
    private String generateStateDefId()   { return WfIdGenerator.generate("WS"); }
    private String generateTransitionDefId() { return WfIdGenerator.generate("WT"); }
    private String generateTaskTemplateId()  { return WfIdGenerator.generate("TT"); }

    // ── findOrThrow ──────────────────────────────────────────────

    private WfProcessDef findProcessDefOrThrow(String id) {
        return processDefRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "프로세스 정의를 찾을 수 없습니다: " + id));
    }

    private WfStateDef findStateDefOrThrow(String id) {
        return stateDefRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "상태 정의를 찾을 수 없습니다: " + id));
    }

    private WfTransitionDef findTransitionDefOrThrow(String id) {
        return transitionDefRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "전이 정의를 찾을 수 없습니다: " + id));
    }

    private WfTaskTemplate findTaskTemplateOrThrow(String id) {
        return taskTemplateRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task 템플릿을 찾을 수 없습니다: " + id));
    }

    // ── toDto (private) ──────────────────────────────────────────

    private WfProcessDefResponseDto toProcessDefDto(WfProcessDef e) {
        WfProcessDefResponseDto dto = new WfProcessDefResponseDto();
        dto.setProcessDefId(e.getProcessDefId());
        dto.setProcessNm(e.getProcessNm());
        dto.setProcessDesc(e.getProcessDesc());
        dto.setEntityType(e.getEntityType());
        dto.setUseYn(e.getUseYn());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }

    private WfStateDefResponseDto toStateDefDto(WfStateDef e) {
        WfStateDefResponseDto dto = new WfStateDefResponseDto();
        dto.setStateDefId(e.getStateDefId());
        dto.setProcessDefId(e.getProcessDefId());
        dto.setStateNm(e.getStateNm());
        dto.setStateType(e.getStateType());
        dto.setSortOrder(e.getSortOrder());
        dto.setEntityStatusCd(e.getEntityStatusCd());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }

    private WfTransitionDefResponseDto toTransitionDefDto(WfTransitionDef e) {
        WfTransitionDefResponseDto dto = new WfTransitionDefResponseDto();
        dto.setTransitionDefId(e.getTransitionDefId());
        dto.setProcessDefId(e.getProcessDefId());
        dto.setFromStateDefId(e.getFromStateDefId());
        dto.setToStateDefId(e.getToStateDefId());
        dto.setTransitionCode(e.getTransitionCode());
        dto.setEventNm(e.getEventNm());
        dto.setSortOrder(e.getSortOrder());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }

    private WfTaskTemplateResponseDto toTaskTemplateDto(WfTaskTemplate e) {
        WfTaskTemplateResponseDto dto = new WfTaskTemplateResponseDto();
        dto.setTaskTemplateId(e.getTaskTemplateId());
        dto.setStateDefId(e.getStateDefId());
        dto.setTaskNm(e.getTaskNm());
        dto.setTaskDesc(e.getTaskDesc());
        dto.setAssigneeType(e.getAssigneeType());
        dto.setAssigneeValue(e.getAssigneeValue());
        dto.setTaskType(e.getTaskType());
        dto.setPriority(e.getPriority());
        dto.setSlaHours(e.getSlaHours());
        dto.setSortOrder(e.getSortOrder());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}

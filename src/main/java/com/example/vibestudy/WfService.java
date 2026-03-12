package com.example.vibestudy;

import java.util.List;

public interface WfService {

    // ── 프로세스 정의 ──
    List<WfProcessDefResponseDto> findAllProcessDefs();
    WfProcessDefResponseDto findProcessDefById(String processDefId);
    WfProcessDefResponseDto createProcessDef(WfProcessDefRequestDto dto);
    WfProcessDefResponseDto updateProcessDef(String processDefId, WfProcessDefRequestDto dto);
    void deleteProcessDef(String processDefId);

    // ── 상태 정의 ──
    List<WfStateDefResponseDto> findStatesByProcessDefId(String processDefId);
    WfStateDefResponseDto createStateDef(String processDefId, WfStateDefRequestDto dto);
    WfStateDefResponseDto updateStateDef(String stateDefId, WfStateDefRequestDto dto);
    void deleteStateDef(String stateDefId);

    // ── 전이 정의 ──
    List<WfTransitionDefResponseDto> findTransitionsByProcessDefId(String processDefId);
    WfTransitionDefResponseDto createTransitionDef(String processDefId, WfTransitionDefRequestDto dto);
    WfTransitionDefResponseDto updateTransitionDef(String transitionDefId, WfTransitionDefRequestDto dto);
    void deleteTransitionDef(String transitionDefId);

    // ── Task 템플릿 ──
    List<WfTaskTemplateResponseDto> findTaskTemplatesByStateDefId(String stateDefId);
    WfTaskTemplateResponseDto createTaskTemplate(String stateDefId, WfTaskTemplateRequestDto dto);
    WfTaskTemplateResponseDto updateTaskTemplate(String taskTemplateId, WfTaskTemplateRequestDto dto);
    void deleteTaskTemplate(String taskTemplateId);
}

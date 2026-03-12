package com.example.vibestudy;

public interface WfEngineService {
    WfProcessInstResponseDto startProcess(WfStartProcessRequestDto dto);
    WfProcessInstResponseDto transition(String processInstId, WfTransitionRequestDto dto);
    WfProcessInstResponseDto performTransition(String processInstId, String transitionCode, String performedBy, String comment);
    WfProcessInstResponseDto getProcessInstance(String processInstId);
    WfProcessInstResponseDto getProcessInstanceByEntity(String entityType, String entityId);
}

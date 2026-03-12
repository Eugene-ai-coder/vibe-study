package com.example.vibestudy;

import java.util.List;

public interface WfTaskService {
    List<WfTaskInstResponseDto> getMyTasks(String assigneeId, String status);
    List<WfTaskInstResponseDto> getTasksByProcessInst(String processInstId);
    WfTaskInstResponseDto claimTask(String taskInstId, WfClaimTaskRequestDto dto);
    WfTaskInstResponseDto completeTask(String taskInstId, WfCompleteTaskRequestDto dto);
}

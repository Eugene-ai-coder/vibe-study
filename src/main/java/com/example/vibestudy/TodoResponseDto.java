package com.example.vibestudy;

import java.time.LocalDateTime;

public class TodoResponseDto {

    private Long todoId;
    private String entityType;
    private String entityKey1;
    private String entityKey2;
    private String todoTitle;
    private String assigneeId;
    private String todoStatusCd;
    private LocalDateTime effStartDt;
    private LocalDateTime effEndDt;
    private String createdBy;
    private LocalDateTime createdDt;

    // ── Getters / Setters ────────────────────────────────────────

    public Long getTodoId() { return todoId; }
    public void setTodoId(Long todoId) { this.todoId = todoId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityKey1() { return entityKey1; }
    public void setEntityKey1(String entityKey1) { this.entityKey1 = entityKey1; }

    public String getEntityKey2() { return entityKey2; }
    public void setEntityKey2(String entityKey2) { this.entityKey2 = entityKey2; }

    public String getTodoTitle() { return todoTitle; }
    public void setTodoTitle(String todoTitle) { this.todoTitle = todoTitle; }

    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

    public String getTodoStatusCd() { return todoStatusCd; }
    public void setTodoStatusCd(String todoStatusCd) { this.todoStatusCd = todoStatusCd; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
}

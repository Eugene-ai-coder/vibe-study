package com.example.vibestudy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long todoId;

    @Column(name = "entity_type", length = 50, nullable = false)
    private String entityType;

    @Column(name = "entity_key1", length = 50, nullable = false)
    private String entityKey1;

    @Column(name = "entity_key2", length = 50)
    private String entityKey2;

    @Column(name = "todo_title", length = 200, nullable = false)
    private String todoTitle;

    @Column(name = "assignee_id", length = 50, nullable = false)
    private String assigneeId;

    @Column(name = "todo_status_cd", length = 20, nullable = false)
    private String todoStatusCd;

    @Column(name = "eff_start_dt", nullable = false)
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt", nullable = false)
    private LocalDateTime effEndDt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
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

package com.example.vibestudy;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository repository;

    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<TodoResponseDto> findMyTodos(String assigneeId, String todoStatusCd,
                                              String entityType, int page, int size) {
        Specification<Todo> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.equal(root.get("assigneeId"), assigneeId));
            if (todoStatusCd != null && !todoStatusCd.isEmpty()) {
                preds.add(cb.equal(root.get("todoStatusCd"), todoStatusCd));
            }
            if (entityType != null && !entityType.isEmpty()) {
                preds.add(cb.equal(root.get("entityType"), entityType));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.asc("todoStatusCd"), Sort.Order.desc("effStartDt")));
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public void createTodo(String entityType, String key1, String key2,
                           String assigneeId, String title) {
        if (assigneeId == null || assigneeId.isBlank()) return;

        List<Todo> existing = repository
                .findByEntityTypeAndEntityKey1AndEntityKey2AndEffEndDt(
                        entityType, key1, key2, IdGenerator.MAX_DT);
        if (!existing.isEmpty()) return;

        Todo todo = new Todo();
        todo.setEntityType(entityType);
        todo.setEntityKey1(key1);
        todo.setEntityKey2(key2);
        todo.setTodoTitle(title);
        todo.setAssigneeId(assigneeId);
        todo.setTodoStatusCd("OPEN");
        todo.setEffStartDt(LocalDateTime.now());
        todo.setEffEndDt(IdGenerator.MAX_DT);
        todo.setCreatedBy(SecurityUtils.getCurrentUserId());
        todo.setCreatedDt(LocalDateTime.now());
        repository.save(todo);
    }

    @Override
    @Transactional
    public void completeTodo(String entityType, String key1, String key2) {
        List<Todo> openTodos = repository
                .findByEntityTypeAndEntityKey1AndEntityKey2AndTodoStatusCd(
                        entityType, key1, key2, "OPEN");
        LocalDateTime now = LocalDateTime.now();
        for (Todo todo : openTodos) {
            todo.setTodoStatusCd("DONE");
            todo.setEffEndDt(now);
        }
        if (!openTodos.isEmpty()) {
            repository.saveAll(openTodos);
        }
    }

    private TodoResponseDto toDto(Todo e) {
        TodoResponseDto dto = new TodoResponseDto();
        dto.setTodoId(e.getTodoId());
        dto.setEntityType(e.getEntityType());
        dto.setEntityKey1(e.getEntityKey1());
        dto.setEntityKey2(e.getEntityKey2());
        dto.setTodoTitle(e.getTodoTitle());
        dto.setAssigneeId(e.getAssigneeId());
        dto.setTodoStatusCd(e.getTodoStatusCd());
        dto.setEffStartDt(e.getEffStartDt());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        return dto;
    }
}

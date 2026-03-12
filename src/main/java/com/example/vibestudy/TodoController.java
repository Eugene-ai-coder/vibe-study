package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public Page<TodoResponseDto> getMyTodos(
            @RequestParam(required = false) String todoStatusCd,
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String assigneeId = SecurityUtils.getCurrentUserId();
        return todoService.findMyTodos(assigneeId, todoStatusCd, entityType, page, size);
    }
}

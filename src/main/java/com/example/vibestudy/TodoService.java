package com.example.vibestudy;

import org.springframework.data.domain.Page;

public interface TodoService {
    Page<TodoResponseDto> findMyTodos(String assigneeId, String todoStatusCd,
                                       String entityType, int page, int size);
    void createTodo(String entityType, String key1, String key2,
                    String assigneeId, String title);
    void completeTodo(String entityType, String key1, String key2);
}

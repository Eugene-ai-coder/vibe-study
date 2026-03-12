package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long>,
        JpaSpecificationExecutor<Todo> {

    List<Todo> findByEntityTypeAndEntityKey1AndEntityKey2AndEffEndDt(
            String entityType, String entityKey1, String entityKey2, LocalDateTime effEndDt);

    List<Todo> findByEntityTypeAndEntityKey1AndEntityKey2AndTodoStatusCd(
            String entityType, String entityKey1, String entityKey2, String todoStatusCd);
}

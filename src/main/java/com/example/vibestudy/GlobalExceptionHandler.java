package com.example.vibestudy;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .toList();
        Map<String, Object> body = new HashMap<>();
        body.put("errors", errors);
        body.put("message", String.join(", ", errors));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        String msg = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        Map<String, Object> body = new HashMap<>();
        body.put("errors", List.of(msg));
        body.put("message", msg);
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        String msg = "아이디 또는 비밀번호가 일치하지 않습니다.";
        Map<String, Object> body = new HashMap<>();
        body.put("errors", List.of(msg));
        body.put("message", msg);
        return ResponseEntity.status(401).body(body);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabled(DisabledException ex) {
        String msg = "사용이 제한된 계정입니다.";
        Map<String, Object> body = new HashMap<>();
        body.put("errors", List.of(msg));
        body.put("message", msg);
        return ResponseEntity.status(403).body(body);
    }
}

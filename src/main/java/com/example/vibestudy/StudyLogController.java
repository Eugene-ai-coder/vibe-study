package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class StudyLogController {

    private final StudyLogRepository repository;

    public StudyLogController(StudyLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<StudyLog> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<StudyLog> create(@Valid @RequestBody StudyLogRequestDto dto) {
        StudyLog saved = repository.save(toEntity(dto));
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyLog> update(@PathVariable Long id, @Valid @RequestBody StudyLogRequestDto dto) {
        return repository.findById(id).map(log -> {
            log.setContent(dto.getContent());
            log.setDate(dto.getDate());
            return ResponseEntity.ok(repository.save(log));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private StudyLog toEntity(StudyLogRequestDto dto) {
        StudyLog log = new StudyLog();
        log.setContent(dto.getContent());
        log.setDate(dto.getDate());
        return log;
    }
}

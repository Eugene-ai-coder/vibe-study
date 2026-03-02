package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class StudyLogController {

    private final StudyLogService studyLogService;

    public StudyLogController(StudyLogService studyLogService) {
        this.studyLogService = studyLogService;
    }

    @GetMapping
    public List<StudyLog> getAll() {
        return studyLogService.getAll();
    }

    @PostMapping
    public ResponseEntity<StudyLog> create(@Valid @RequestBody StudyLogRequestDto dto) {
        return ResponseEntity.ok(studyLogService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyLog> update(@PathVariable Long id, @Valid @RequestBody StudyLogRequestDto dto) {
        return ResponseEntity.ok(studyLogService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studyLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

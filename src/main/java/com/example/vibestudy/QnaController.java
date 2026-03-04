package com.example.vibestudy;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaService qnaService;

    public QnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping
    public Page<QnaResponseDto> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return qnaService.findAll(keyword, page, size);
    }

    @PostMapping
    public ResponseEntity<QnaResponseDto> create(@Valid @RequestBody QnaRequestDto dto) {
        return ResponseEntity.status(201).body(qnaService.create(dto));
    }

    @GetMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> getOne(@PathVariable String qnaId) {
        return ResponseEntity.ok(qnaService.findById(qnaId));
    }

    @PutMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> update(
            @PathVariable String qnaId,
            @Valid @RequestBody QnaRequestDto dto) {
        return ResponseEntity.ok(qnaService.update(qnaId, dto));
    }

    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> delete(@PathVariable String qnaId) {
        qnaService.delete(qnaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{qnaId}/comments")
    public List<QnaCommentResponseDto> getComments(@PathVariable String qnaId) {
        return qnaService.findComments(qnaId);
    }

    @PostMapping("/{qnaId}/comments")
    public ResponseEntity<QnaCommentResponseDto> createComment(
            @PathVariable String qnaId,
            @Valid @RequestBody QnaCommentRequestDto dto) {
        return ResponseEntity.status(201).body(qnaService.createComment(qnaId, dto));
    }

    @DeleteMapping("/{qnaId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String qnaId,
            @PathVariable String commentId) {
        qnaService.deleteComment(qnaId, commentId);
        return ResponseEntity.noContent().build();
    }
}

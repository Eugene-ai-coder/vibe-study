package com.example.vibestudy;

import org.springframework.data.domain.Page;
import java.util.List;

public interface QnaService {
    Page<QnaResponseDto> findAll(String keyword, int page, int size);
    QnaResponseDto findById(String qnaId);
    QnaResponseDto create(QnaRequestDto dto);
    QnaResponseDto update(String qnaId, QnaRequestDto dto);
    void delete(String qnaId);

    List<QnaCommentResponseDto> findComments(String qnaId);
    QnaCommentResponseDto createComment(String qnaId, QnaCommentRequestDto dto);
    void deleteComment(String qnaId, String commentId);
}

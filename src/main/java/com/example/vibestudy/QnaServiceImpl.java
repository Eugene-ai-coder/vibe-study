package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;
    private final QnaCommentRepository qnaCommentRepository;

    public QnaServiceImpl(QnaRepository qnaRepository, QnaCommentRepository qnaCommentRepository) {
        this.qnaRepository = qnaRepository;
        this.qnaCommentRepository = qnaCommentRepository;
    }

    private String generateQnaId() {
        return "QNA" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }

    private String generateCommentId() {
        return "CMT" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }

    @Override
    public Page<QnaResponseDto> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        if (keyword == null || keyword.isBlank()) {
            return qnaRepository.findAll(pageable).map(this::toDto);
        }
        return qnaRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable).map(this::toDto);
    }

    @Override
    public QnaResponseDto findById(String qnaId) {
        qnaRepository.incrementViewCnt(qnaId);
        return toDto(findOrThrow(qnaId));
    }

    @Override
    public QnaResponseDto create(QnaRequestDto dto) {
        Qna entity = new Qna();
        entity.setQnaId(generateQnaId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setViewCnt(0);
        entity.setAnswerYn("N");
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toDto(qnaRepository.save(entity));
    }

    @Override
    public QnaResponseDto update(String qnaId, QnaRequestDto dto) {
        Qna entity = findOrThrow(qnaId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toDto(qnaRepository.save(entity));
    }

    @Override
    public void delete(String qnaId) {
        findOrThrow(qnaId);
        qnaRepository.deleteById(qnaId);
    }

    @Override
    public List<QnaCommentResponseDto> findComments(String qnaId) {
        return qnaCommentRepository.findByQnaIdOrderByCreatedDt(qnaId)
                .stream().map(this::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public QnaCommentResponseDto createComment(String qnaId, QnaCommentRequestDto dto) {
        if (!qnaRepository.existsById(qnaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Q&A를 찾을 수 없습니다: " + qnaId);
        }
        QnaComment entity = new QnaComment();
        entity.setCommentId(generateCommentId());
        entity.setQnaId(qnaId);
        entity.setParentCommentId(dto.getParentCommentId());
        entity.setContent(dto.getContent());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toCommentDto(qnaCommentRepository.save(entity));
    }

    @Override
    public void deleteComment(String qnaId, String commentId) {
        QnaComment entity = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다: " + commentId));
        if (!entity.getQnaId().equals(qnaId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Q&A ID가 일치하지 않습니다.");
        }
        qnaCommentRepository.deleteById(commentId);
    }

    private Qna findOrThrow(String qnaId) {
        return qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Q&A를 찾을 수 없습니다: " + qnaId));
    }

    private QnaResponseDto toDto(Qna entity) {
        QnaResponseDto dto = new QnaResponseDto();
        dto.setQnaId(entity.getQnaId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setViewCnt(entity.getViewCnt());
        dto.setAnswerYn(entity.getAnswerYn());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDt(entity.getCreatedDt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedDt(entity.getUpdatedDt());
        return dto;
    }

    private QnaCommentResponseDto toCommentDto(QnaComment entity) {
        QnaCommentResponseDto dto = new QnaCommentResponseDto();
        dto.setCommentId(entity.getCommentId());
        dto.setQnaId(entity.getQnaId());
        dto.setParentCommentId(entity.getParentCommentId());
        dto.setContent(entity.getContent());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDt(entity.getCreatedDt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedDt(entity.getUpdatedDt());
        return dto;
    }
}

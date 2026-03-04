package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaComment, String> {
    List<QnaComment> findByQnaIdOrderByCreatedDt(String qnaId);
}

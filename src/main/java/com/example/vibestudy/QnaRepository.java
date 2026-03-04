package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface QnaRepository extends JpaRepository<Qna, String> {
    Page<Qna> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Qna q SET q.viewCnt = q.viewCnt + 1 WHERE q.qnaId = :qnaId")
    void incrementViewCnt(@Param("qnaId") String qnaId);
}

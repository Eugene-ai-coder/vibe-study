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

    @Query("""
        SELECT q FROM Qna q
        ORDER BY
          CASE WHEN q.noticeYn = 'Y'
                AND q.noticeStartDt <= CURRENT_TIMESTAMP
                AND q.noticeEndDt >= CURRENT_TIMESTAMP
               THEN 0 ELSE 1 END,
          q.createdDt DESC
        """)
    Page<Qna> findAllWithNoticeFirst(Pageable pageable);

    @Query("""
        SELECT q FROM Qna q
        WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%
        ORDER BY
          CASE WHEN q.noticeYn = 'Y'
                AND q.noticeStartDt <= CURRENT_TIMESTAMP
                AND q.noticeEndDt >= CURRENT_TIMESTAMP
               THEN 0 ELSE 1 END,
          q.createdDt DESC
        """)
    Page<Qna> findByKeywordWithNoticeFirst(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Qna q SET q.viewCnt = q.viewCnt + 1 WHERE q.qnaId = :qnaId")
    void incrementViewCnt(@Param("qnaId") String qnaId);
}

package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_qna")
public class Qna {

    @Id
    @Column(name = "qna_id", length = 50, nullable = false)
    private String qnaId;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_cnt")
    private Integer viewCnt = 0;

    @Column(name = "answer_yn", length = 1)
    private String answerYn = "N";

    @Column(name = "notice_yn", length = 1)
    private String noticeYn = "N";

    @Column(name = "notice_start_dt")
    private LocalDateTime noticeStartDt;

    @Column(name = "notice_end_dt")
    private LocalDateTime noticeEndDt;

    /* ── System Fields ── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public String getQnaId() { return qnaId; }
    public void setQnaId(String qnaId) { this.qnaId = qnaId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getViewCnt() { return viewCnt; }
    public void setViewCnt(Integer viewCnt) { this.viewCnt = viewCnt; }
    public String getAnswerYn() { return answerYn; }
    public void setAnswerYn(String answerYn) { this.answerYn = answerYn; }
    public String getNoticeYn() { return noticeYn; }
    public void setNoticeYn(String noticeYn) { this.noticeYn = noticeYn; }
    public LocalDateTime getNoticeStartDt() { return noticeStartDt; }
    public void setNoticeStartDt(LocalDateTime noticeStartDt) { this.noticeStartDt = noticeStartDt; }
    public LocalDateTime getNoticeEndDt() { return noticeEndDt; }
    public void setNoticeEndDt(LocalDateTime noticeEndDt) { this.noticeEndDt = noticeEndDt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

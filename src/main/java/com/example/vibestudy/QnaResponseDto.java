package com.example.vibestudy;

import java.time.LocalDateTime;

public class QnaResponseDto {
    private String qnaId;
    private String title;
    private String content;
    private Integer viewCnt;
    private String answerYn;
    private String noticeYn;
    private LocalDateTime noticeStartDt;
    private LocalDateTime noticeEndDt;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
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

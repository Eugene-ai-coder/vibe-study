package com.example.vibestudy;

import java.time.LocalDateTime;

public class QnaRequestDto {
    private String title;
    private String content;
    private String createdBy;
    private String noticeYn;
    private LocalDateTime noticeStartDt;
    private LocalDateTime noticeEndDt;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getNoticeYn() { return noticeYn; }
    public void setNoticeYn(String noticeYn) { this.noticeYn = noticeYn; }
    public LocalDateTime getNoticeStartDt() { return noticeStartDt; }
    public void setNoticeStartDt(LocalDateTime noticeStartDt) { this.noticeStartDt = noticeStartDt; }
    public LocalDateTime getNoticeEndDt() { return noticeEndDt; }
    public void setNoticeEndDt(LocalDateTime noticeEndDt) { this.noticeEndDt = noticeEndDt; }
}

package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class StudyLogRequestDto {

    @NotBlank(message = "학습 내용을 입력해주세요.")
    @Size(min = 2, message = "최소 2자 이상 입력해주세요.")
    private String content;

    @NotNull(message = "날짜를 선택해주세요.")
    private LocalDate date;

    private String createdBy;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

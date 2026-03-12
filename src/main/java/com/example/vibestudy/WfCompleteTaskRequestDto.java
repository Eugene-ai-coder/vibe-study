package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfCompleteTaskRequestDto {

    @NotBlank(message = "완료결과는 필수입니다.")
    private String result;

    private String comment;

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

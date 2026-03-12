package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfTransitionRequestDto {

    @NotBlank(message = "전이코드는 필수입니다.")
    private String transitionCode;

    private String comment;

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getTransitionCode() { return transitionCode; }
    public void setTransitionCode(String transitionCode) { this.transitionCode = transitionCode; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

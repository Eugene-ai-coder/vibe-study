package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class WfClaimTaskRequestDto {

    @NotBlank(message = "생성자ID는 필수입니다.")
    private String createdBy;

    // ── Getters / Setters ────────────────────────────────────────

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

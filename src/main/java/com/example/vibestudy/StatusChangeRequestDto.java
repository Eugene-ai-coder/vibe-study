package com.example.vibestudy;

import jakarta.validation.constraints.NotBlank;

public class StatusChangeRequestDto {

    @NotBlank(message = "상태값은 필수입니다")
    private String status;

    private String createdBy;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

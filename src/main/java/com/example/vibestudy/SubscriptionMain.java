package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_subscription_main")
public class SubscriptionMain {

    @Id
    @Column(name = "subs_main_id", length = 20, nullable = false)
    private String subsMainId;

    @Column(name = "subs_id", length = 50, nullable = false)
    private String subsId;

    @Column(name = "main_subs_yn", length = 1, nullable = false)
    private String mainSubsYn;

    @Column(name = "main_subs_id", length = 50)
    private String mainSubsId;

    @Column(name = "eff_start_dt", nullable = false)
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt", nullable = false)
    private LocalDateTime effEndDt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public String getSubsMainId() { return subsMainId; }
    public void setSubsMainId(String subsMainId) { this.subsMainId = subsMainId; }

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getMainSubsYn() { return mainSubsYn; }
    public void setMainSubsYn(String mainSubsYn) { this.mainSubsYn = mainSubsYn; }

    public String getMainSubsId() { return mainSubsId; }
    public void setMainSubsId(String mainSubsId) { this.mainSubsId = mainSubsId; }

    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }

    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}

package com.example.vibestudy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSessionDto implements Serializable {

    private String userId;
    private String nickname;
    private int accountStatus;
    private List<String> roles = new ArrayList<>();

    public UserSessionDto() {}

    public UserSessionDto(String userId, String nickname, int accountStatus) {
        this.userId = userId;
        this.nickname = nickname;
        this.accountStatus = accountStatus;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getAccountStatus() { return accountStatus; }
    public void setAccountStatus(int accountStatus) { this.accountStatus = accountStatus; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}

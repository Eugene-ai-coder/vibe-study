package com.example.vibestudy;

import java.io.Serializable;

public class UserSessionDto implements Serializable {

    private String userId;
    private String nickname;
    private int accountStatus;

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
}

package com.example.vibestudy;

public class RoleUserDto {

    private String userId;
    private String nickname;
    private int accountStatus;

    public RoleUserDto() {
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getAccountStatus() { return accountStatus; }
    public void setAccountStatus(int accountStatus) { this.accountStatus = accountStatus; }
}

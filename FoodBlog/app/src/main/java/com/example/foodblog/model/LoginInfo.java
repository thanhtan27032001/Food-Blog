package com.example.foodblog.model;

public class LoginInfo {
    private String accountName;
    private String password;

    public LoginInfo(String accountName, String password) {
        this.accountName = accountName;
        this.password = password;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPassword() {
        return password;
    }
}

package com.example.foodblog.model;

import java.io.Serializable;

public class RegisterInfo implements Serializable {
    private String fullName;
    private String email;
    private String accountName;
    private String password;
    private String password2;

    public RegisterInfo() {
    }

    public RegisterInfo(String fullName, String email, String accountName, String password, String password2) {
        this.fullName = fullName;
        this.email = email;
        this.accountName = accountName;
        this.password = password;
        this.password2 = password2;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPassword() {
        return password;
    }
}

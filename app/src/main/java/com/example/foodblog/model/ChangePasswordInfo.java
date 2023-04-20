package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordInfo {
    private String accountName;
    private String newPassword;
    @SerializedName("checkPassword")
    private String confirmPassword;
    private String otp;

    public ChangePasswordInfo(String accountName, String newPassword, String confirmPassword, String otp) {
        this.accountName = accountName;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.otp = otp;
    }
}

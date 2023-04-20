package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

public class SendOtpInfo {
    @SerializedName("accountName")
    private String username;
    private String subject;

    public SendOtpInfo(String username, String subject) {
        this.username = username;
        this.subject = subject;
    }
}

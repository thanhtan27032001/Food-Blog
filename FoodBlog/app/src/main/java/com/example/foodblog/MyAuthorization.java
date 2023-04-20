package com.example.foodblog;

import com.example.foodblog.model.User;

public class MyAuthorization {
    private static MyAuthorization instance;
    private String token;
    private String username;
    private User user;
    public static MyAuthorization getInstance(){
        if (instance == null){
            instance = new MyAuthorization();
        }
        return instance;
    }

    public static void setInstance(MyAuthorization instance) {
        MyAuthorization.instance = instance;
    }

    public String getToken() {
        return "Bearer " + token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

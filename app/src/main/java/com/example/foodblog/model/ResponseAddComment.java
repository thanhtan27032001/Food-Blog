package com.example.foodblog.model;

public class ResponseAddComment extends ResponseObject{
    private Comment data;

    public ResponseAddComment(boolean success, String message, Comment data) {
        super(success, message);
        this.data = data;
    }
}

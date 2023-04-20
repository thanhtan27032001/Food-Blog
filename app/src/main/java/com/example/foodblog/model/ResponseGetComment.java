package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class ResponseCommentData{
    @SerializedName("comment")
    private ArrayList<Comment> comments;
    private int commentCount;
    private Comment myComment;

    public ResponseCommentData() {
    }

    public ResponseCommentData(ArrayList<Comment> comments, int commentCount, Comment myComment) {
        this.comments = comments;
        this.commentCount = commentCount;
        this.myComment = myComment;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public Comment getMyComment() {
        return myComment;
    }
}
public class ResponseGetComment extends ResponseObject{
    private ResponseCommentData data;

    public ResponseGetComment() {
    }

    public ResponseGetComment(boolean success, String message, ResponseCommentData data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<Comment> getComments() {
        return data.getComments();
    }

    public int getCommentCount() {
        return data.getCommentCount();
    }
    public Comment getMyComment(){
        return data.getMyComment();
    }
}

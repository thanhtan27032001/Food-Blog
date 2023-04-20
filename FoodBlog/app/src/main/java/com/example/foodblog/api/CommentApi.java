package com.example.foodblog.api;

import com.example.foodblog.model.Comment;
import com.example.foodblog.model.ResponseAddComment;
import com.example.foodblog.model.ResponseGetComment;
import com.example.foodblog.model.ResponseObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentApi {
    @GET("/api/v1/comment/getCommentOfRecipe/{recipeId}")
    Call<ResponseGetComment> getAllCommentOfRecipe(@Header("Authorization") String token, @Path("recipeId") int recipeId);

    @POST("/api/v1/comment/createComment/{recipeId}")
    Call<ResponseAddComment> addComment(@Header("Authorization") String token, @Path("recipeId") int recipeId, @Body Comment comment);

    @PUT("/api/v1/comment/updateComment/{recipeId}")
    Call<ResponseAddComment> updateComment(@Header("Authorization") String token, @Path("recipeId") int recipeId, @Body Comment comment);

    @DELETE("/api/v1/comment/deleteComment/{recipeId}")
    Call<ResponseObject> deleteComment(@Header("Authorization") String token, @Path("recipeId") int recipeId);
}

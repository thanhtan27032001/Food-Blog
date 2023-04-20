package com.example.foodblog.api;

import com.example.foodblog.model.BookmarkRecipeInfo;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipe;
import com.example.foodblog.model.ResponseRecipeNames;
import com.example.foodblog.model.ResponseRecipes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecipeApi {
    @GET("/api/v1/recipe/getRecipeByIngredient/{slug}")
    Call<ResponseRecipes> getRecipesByIngredient(@Header("Authorization") String token, @Path("slug") String ingredientName);

    @GET("/api/v1/recipe/getRecipe/{recipeId}")
    Call<ResponseRecipe> getRecipeById(@Header("Authorization") String token, @Path("recipeId") int recipeId);

    @GET("/api/v1/recipe/getRecipeFromFollowers")
    Call<ResponseRecipes> getRecipeFromFollower(@Header("Authorization") String token);

    @GET("/api/v1/recipe/getPopularRecipe")
    Call<ResponseRecipes> getRecipePopular(@Header("Authorization") String token);

    @GET("/api/v1/recipe/getAllRecipe")
    Call<ResponseRecipes> getRecipeNewest(@Header("Authorization") String token);

    @Multipart
    @POST("/api/v1/recipe/createRecipe/")
    Call<ResponseObject> addRecipe(
            @Header("Authorization") String token,
            @Part("data") RequestBody recipeDetail,
            @Part MultipartBody.Part recipe,
            @Part MultipartBody.Part[] step);

    @Multipart
    @PUT("/api/v1/recipe/updateRecipe/{recipeId}")
    Call<ResponseObject> updateRecipe(
            @Header("Authorization") String token,
            @Path("recipeId") int recipeId,
            @Part("data") RequestBody recipeDetail,
            @Part MultipartBody.Part recipe,
            @Part MultipartBody.Part[] step);

    @GET("/api/v1/recipe/getRecipeByUserId/")
    Call<ResponseRecipes> getAllMyRecipes(@Header("Authorization") String token);

    @PUT("/api/v1/recipe/updatePrivacyRecipe/{recipeId}")
    Call<ResponseObject> updateRecipeStatus(@Header("Authorization") String token, @Path("recipeId") int recipeId, @Body Recipe recipe);

    @DELETE("/api/v1/recipe/deleteRecipe/{recipeId}")
    Call<ResponseObject> deleteRecipe(@Header("Authorization") String token, @Path("recipeId") int recipeId);

    @POST("/api/v1/favorite/create/{recipeId}")
    Call<ResponseObject> likeRecipe(@Header("Authorization") String token, @Path("recipeId") int recipeId);

    @DELETE("/api/v1/favorite/delete/{recipeId}")
    Call<ResponseObject> unlikeRecipe(@Header("Authorization") String token, @Path("recipeId") int recipeId);

    @GET("/api/v1/recipe/getRecipeFavorite")
    Call<ResponseRecipes> getRecipeFavorite(@Header("Authorization") String token);

    @GET("/api/v1/recipe/search")
    Call<ResponseRecipeNames> searchRecipeNames(@Header("Authorization") String token, @Query("q") String searchValue);

    @GET("/api/v1/recipe/getRecipeByName/{name}")
    Call<ResponseRecipes> getRecipesByName(@Header("Authorization") String token, @Path("name") String name);

    @POST("/api/v1/recipeList/createRecipe/{recipeId}")
    Call<ResponseObject> bookmarkRecipe(
            @Header("Authorization") String token,
            @Path("recipeId") int recipeId,
            @Body BookmarkRecipeInfo info);

    @DELETE("/api/v1/recipeList/deleteRecipe/{recipeListId}/{recipeId}")
    Call<ResponseObject> removeBookmarkRecipe(
            @Header("Authorization") String token,
            @Path("recipeListId") int recipeListId,
            @Path("recipeId") int recipeId
    );
}

package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.adapter_recycler_view.BookmarkAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeSearchAdapter;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.fragment.SearchFragment;
import com.example.foodblog.model.BookmarkRecipeInfo;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeListDetail;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.example.foodblog.model.ResponseRecipes;
import com.example.foodblog.model.SearchHistories;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity implements SearchRecipeInterface{

    SearchHistories searchHistories;
    TextView tvSearchTitle;
    ImageButton btnBack;
    RecyclerView rvRecipeSearchResult;
    RecipeSearchAdapter recipeSearchAdapter;
    ArrayList<Recipe> recipes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchHistories = SearchHistories.getInstance(this);
        setControl();
        setEvent();
        setData();
    }
    private void setData() {
        String searchValue = getIntent().getStringExtra(SearchFragment.TAG_SEARCH_VALUE);
        tvSearchTitle.setText(searchValue);
        // set data rv search recipe
        Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getRecipesByName(MyAuthorization.getInstance().getToken(), searchValue);
        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                if (response.isSuccessful()){
                    recipes.clear();
                    recipes.addAll(response.body().getData());
                    recipeSearchAdapter = new RecipeSearchAdapter(SearchResultActivity.this, recipes);
                    rvRecipeSearchResult.setAdapter(recipeSearchAdapter);
                }
                else if (response.code() == 432){
                    Toast.makeText(SearchResultActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRecipes> call, Throwable t) {
                Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void setControl() {
        tvSearchTitle = findViewById(R.id.tvSearchTitle);
        btnBack = findViewById(R.id.btnBack);
        rvRecipeSearchResult = findViewById(R.id.rvRecipeSearchResult);
        rvRecipeSearchResult.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
    public void viewRecipeDetail(int recipeId){
        searchHistories.addHistory(this, recipeId);
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.TAG_RECIPE, recipeId);startActivity(intent);
    }
    public String getMyText(int resId){
        return getText(resId).toString();
    }

    public void changeRecipeFavorite(Recipe recipe, boolean isFavorite, ImageButton updateLikeStatus, TextView numOfLike){
        Call<ResponseObject> call;
        if (isFavorite){
            call = RecipeApiInstance.getInstance().likeRecipe(MyAuthorization.getInstance().getToken(), recipe.getRecipeId());
        }
        else {
            call = RecipeApiInstance.getInstance().unlikeRecipe(MyAuthorization.getInstance().getToken(), recipe.getRecipeId());
        }
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    Toast.makeText(SearchResultActivity.this, (isFavorite ? getText(R.string.success_like) : getText(R.string.success_unlike)), Toast.LENGTH_SHORT).show();
                    recipe.setFavorite(isFavorite);
                    updateLikeStatus.setImageResource(isFavorite ? R.drawable.baseline_favorite_16 : R.drawable.baseline_favorite_border_16);
                    numOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
                }
                else if (response.code() == 432){
                    Toast.makeText(SearchResultActivity.this, getText(R.string.error_recipe_432), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 444) {
                    Toast.makeText(SearchResultActivity.this, getText(R.string.error_like_recipe_444), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 445) {
                    Toast.makeText(SearchResultActivity.this, R.string.error_unlike_recipe_445, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void showDialogBookmark(int recipeId) {
        ArrayList<RecipeListDetail> recipeListDetails = new ArrayList<>();

        Dialog dialog = new Dialog(SearchResultActivity.this);
        dialog.setContentView(R.layout.dialog_bookmark);
        LinearLayout layout = dialog.findViewById(R.id.layoutDialogBookmark);
        RecyclerView rvRecipeListsDetail = dialog.findViewById(R.id.rvRecipeListDetail);
        TextView tvSubmit = dialog.findViewById(R.id.btnSubmit);
        TextView tvCancel = dialog.findViewById(R.id.btnCancel);

        tvSubmit.setOnClickListener(view -> {
            bookmarkRecipe(recipeId, recipeListDetails);
            dialog.dismiss();
        });
        tvCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        rvRecipeListsDetail.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this, LinearLayoutManager.VERTICAL, false));
        Call<ResponseRecipeListsDetail> call = RecipeListApiInstance.getInstance().getRecipeListsDetail(
                MyAuthorization.getInstance().getToken(),
                recipeId);
        call.enqueue(new Callback<ResponseRecipeListsDetail>() {
            @Override
            public void onResponse(Call<ResponseRecipeListsDetail> call, Response<ResponseRecipeListsDetail> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipeListDetails.addAll(response.body().getData());
                    BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(recipeListDetails);
                    rvRecipeListsDetail.setAdapter(bookmarkAdapter);
                    dialog.show();
                }
                else {
                    Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRecipeListsDetail> call, Throwable t) {
                Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
        try { // Adjust dialog width fit to screen
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
            layout.requestLayout();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bookmarkRecipe(int recipeId, ArrayList<RecipeListDetail> recipeListDetails) {
        BookmarkRecipeInfo info = new BookmarkRecipeInfo(recipeListDetails);
        Call<ResponseObject> call = RecipeApiInstance.getInstance().bookmarkRecipe(
                MyAuthorization.getInstance().getToken(),
                recipeId,
                info);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    Toast.makeText(SearchResultActivity.this, R.string.success_bookmark_recipe, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(SearchResultActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 443){
                    Toast.makeText(SearchResultActivity.this, R.string.error_bookmark_443, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(SearchResultActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
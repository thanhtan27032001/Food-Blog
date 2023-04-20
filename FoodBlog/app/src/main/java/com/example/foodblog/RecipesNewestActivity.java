package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.foodblog.adapter_recycler_view.RecipeHomeAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeNewestAdapter;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.model.BookmarkRecipeInfo;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeListDetail;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.example.foodblog.model.ResponseRecipes;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesNewestActivity extends AppCompatActivity {
    private ArrayList<Recipe> recipesNew = new ArrayList<>();
    private RecipeNewestAdapter recipeNewestAdapter;

    private RecyclerView rvRecipeNew;

    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_recipes);

        setControl();
        setEvent();
        setData();
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void setControl() {
        rvRecipeNew = findViewById(R.id.rvRecipeNew);
        rvRecipeNew.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));

        btnBack = findViewById(R.id.btnBack);
    }

    private void setData() {
        setRvRecipeNewestData();
        btnBack = findViewById(R.id.btnBack);
    }

    private void setRvRecipeNewestData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getRecipeNewest(MyAuthorization.getInstance().getToken());
                call.enqueue(new Callback<ResponseRecipes>() {
                    @Override
                    public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                        if (response.isSuccessful()){
                            ResponseRecipes responseRecipe = response.body();
                            if (responseRecipe != null && responseRecipe.getData().size()>0){
                                recipesNew.clear();
                                recipesNew.addAll(responseRecipe.getData());
                                recipeNewestAdapter = new RecipeNewestAdapter(RecipesNewestActivity.this, recipesNew);
                                rvRecipeNew.setAdapter(recipeNewestAdapter);
                            }
                        }
                        else if (response.code() == 432) {
                            Toast.makeText(RecipesNewestActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RecipesNewestActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRecipes> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }

    public void viewRecipeDetail(int recipeId){
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.TAG_RECIPE, recipeId);
        startActivity(intent);
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
                    Toast.makeText(RecipesNewestActivity.this, (isFavorite ? "Yêu thích" : "Bỏ yêu thích") + " công thức thành công", Toast.LENGTH_SHORT).show();
                    recipe.setFavorite(isFavorite);
                    updateLikeStatus.setImageResource(isFavorite ? R.drawable.baseline_favorite_16 : R.drawable.baseline_favorite_border_16);
                    numOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
                }
                else if (response.code() == 432){
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 444) {
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_like_recipe_444, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 445) {
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_unlike_recipe_445, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipesNewestActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void showDialogBookmark(int recipeId) {
        ArrayList<RecipeListDetail> recipeListDetails = new ArrayList<>();

        Dialog dialog = new Dialog(RecipesNewestActivity.this);
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

        rvRecipeListsDetail.setLayoutManager(new LinearLayoutManager(RecipesNewestActivity.this, LinearLayoutManager.VERTICAL, false));
        Call<ResponseRecipeListsDetail> call = RecipeListApiInstance.getInstance().getRecipeListsDetail(
                MyAuthorization.getInstance().getToken(),
                recipeId);
        call.enqueue(new Callback<ResponseRecipeListsDetail>() {
            @Override
            public void onResponse(Call<ResponseRecipeListsDetail> call, Response<ResponseRecipeListsDetail> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipeListDetails.addAll(response.body().getData());
                    Log.e("test", "size " + response.body().getData().size());
                    BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(recipeListDetails);
                    rvRecipeListsDetail.setAdapter(bookmarkAdapter);
                    dialog.show();
                }
                else {
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipesNewestActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RecipesNewestActivity.this, R.string.success_bookmark_recipe, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 443){
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_bookmark_443, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecipesNewestActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipesNewestActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
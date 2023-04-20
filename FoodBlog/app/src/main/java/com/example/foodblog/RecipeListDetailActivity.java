package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.adapter_recycler_view.RecipeListDetailAdapter;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeList;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListDetailActivity extends AppCompatActivity {

    public static final String TAG_RECIPE_LIST = "TAG_RECIPE_LIST";
    private RecipeList recipeList;

    TextView tvRecipeListName;
    ImageButton btnBack;
    RecyclerView rvRecipe;
    ArrayList<Recipe> recipes = new ArrayList<>();
    RecipeListDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list_detail);
        setControl();
        setEvent();
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setControl() {
        tvRecipeListName = findViewById(R.id.tvRecipeListName);
        btnBack = findViewById(R.id.btnBack);
        rvRecipe = findViewById(R.id.rvRecipe);
        rvRecipe.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        recipeList = (RecipeList) intent.getSerializableExtra(TAG_RECIPE_LIST);
        if (recipeList != null){
            tvRecipeListName.setText(recipeList.getName());
            setRvRecipeData();
        }
    }

    private void setRvRecipeData() {
        Log.e("test", "set data recipeListId " + recipeList.getRecipeListId());
        Call<ResponseRecipes> call = RecipeListApiInstance.getInstance().getRecipeListRecipes(
                MyAuthorization.getInstance().getToken(),
                recipeList.getRecipeListId());
        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipes.addAll(response.body().getData());
                    adapter = new RecipeListDetailAdapter(RecipeListDetailActivity.this, recipes);
                    rvRecipe.setAdapter(adapter);
                }
                else {
                    Toast.makeText(RecipeListDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipeListDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void removeBookmark(int position) {
        Log.e("test", "recipeListId: " + recipeList.getRecipeListId());
        Log.e("test", "recipeId: " + recipes.get(position).getRecipeId());
        Call<ResponseObject> call = RecipeApiInstance.getInstance().removeBookmarkRecipe(
                MyAuthorization.getInstance().getToken(),
                recipeList.getRecipeListId(),
                recipes.get(position).getRecipeId()
        );
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipes.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(RecipeListDetailActivity.this, R.string.success_unbookmark_recipe, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(RecipeListDetailActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecipeListDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipeListDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
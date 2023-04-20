package com.example.foodblog.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.adapter_recycler_view.RecipeFavoriteAdapter;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoriteFragment extends Fragment {

    SwipeRefreshLayout swfFavRecipe;
    RecyclerView rvMyFavRecipes;
    ArrayList<Recipe> recipes = new ArrayList<>();
    RecipeFavoriteAdapter recipeFavoriteAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRvMyFavRecipesData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_my_favorite, container, false);

        swfFavRecipe = contentView.findViewById(R.id.swfFavRecipe);

        rvMyFavRecipes = contentView.findViewById(R.id.rvMyFavRecipe);
        rvMyFavRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
        rvMyFavRecipes.setAdapter(recipeFavoriteAdapter);

        setEvent();
        return contentView;
    }

    private void setEvent() {
        swfFavRecipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRvMyFavRecipesData();
            }
        });
    }

    private void setRvMyFavRecipesData() {
        Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getRecipeFavorite(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                swfFavRecipe.setRefreshing(false);
                if (response.isSuccessful() || response.code() == 200){
                    recipes.clear();
                    recipes.addAll(response.body().getData());
                    recipeFavoriteAdapter = new RecipeFavoriteAdapter(MyFavoriteFragment.this, recipes);
                    rvMyFavRecipes.setAdapter(recipeFavoriteAdapter);
                }
                else {
                    Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                swfFavRecipe.setRefreshing(false);
            }
        });
    }

    public void unlikeRecipe(Recipe recipe) {
        Call<ResponseObject> call = RecipeApiInstance.getInstance().unlikeRecipe(
                MyAuthorization.getInstance().getToken(),
                recipe.getRecipeId()
        );
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipeFavoriteAdapter.notifyItemRemoved(recipes.indexOf(recipe));
                    recipes.remove(recipe);
                    Toast.makeText(getContext(), R.string.success_unlike, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(getContext(), R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 445){
                    Toast.makeText(getContext(), R.string.error_unlike_recipe_445, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
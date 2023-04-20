package com.example.foodblog.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.foodblog.MainActivity;
import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.RecipeDetailActivity;
import com.example.foodblog.RecipesNewestActivity;
import com.example.foodblog.adapter_recycler_view.BookmarkAdapter;
import com.example.foodblog.adapter_recycler_view.IngredientSeasonAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeHomeAdapter;
import com.example.foodblog.api_instance.IngredientApiInstance;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.model.BookmarkRecipeInfo;
import com.example.foodblog.model.Ingredient;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeListDetail;
import com.example.foodblog.model.ResponseIngredients;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.example.foodblog.model.ResponseRecipes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private int recipeIngredientRvStatus = 0; // 0: default; 1: found; 2: not found; 3: error
    private static final int DEFAULT = 0;
    private static final int RECIPE_FOUND = 1;
    private static final int RECIPE_NOT_FOUND = 2;
    private static final int ERROR = 4;

    private MainActivity mainActivity;
    private ArrayList<Ingredient> ingredientList = new ArrayList<>();;
    private ArrayList<Recipe> recipesByIngredient = new ArrayList<>();
    private ArrayList<Recipe> recipesFromFollowing = new ArrayList<>();
    private ArrayList<Recipe> recipesPopular = new ArrayList<>();
    private ArrayList<Recipe> recipesNew = new ArrayList<>();
    private IngredientSeasonAdapter ingredientSeasonAdapter;
    private RecipeHomeAdapter recipeIngredientAdapter, recipeFromFollowerAdapter, recipePopularAdapter, recipeNewestAdapter;
    private RecyclerView rvIngredientSeason, rvRecipeIngredient, rvRecipeFromFollower, rvRecipePopular, rvRecipeNew;
    private TextView tvNoRecipeIngredient, tvSeeAllRecipeNew, tvSearchBar;
    private ProgressBar prbLoadRecipeIngredient;
    private ImageButton btnRefreshRecipeFromFollowing, btnRefreshRecipePopular, btnRefreshRecipeNew;

    private NestedScrollView scrollViewContainer;
    private int scrollX = 0;
    private int scrollY = 0;

    public HomeFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRvIngredientSeasonData();
        setRvRecipeFromFollowing();
        setRvRecipePopularData();
        setRvRecipeNewestData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        scrollViewContainer = view.findViewById(R.id.scrollViewContainer);
        scrollViewContainer.scrollTo(scrollX, scrollY);

        rvIngredientSeason = view.findViewById(R.id.rvIngredientSeason);
        rvIngredientSeason.setAdapter(ingredientSeasonAdapter);
        rvIngredientSeason.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));

        rvRecipeIngredient = view.findViewById(R.id.rvRecipeSeason);
        rvRecipeIngredient.setAdapter(recipeIngredientAdapter);
        rvRecipeIngredient.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tvNoRecipeIngredient = view.findViewById(R.id.tvNoRecipeIngredient);
        prbLoadRecipeIngredient = view.findViewById(R.id.prbLoadRecipeIngredient);
        switch (recipeIngredientRvStatus){
            case DEFAULT:
//                Toast.makeText(getContext(), "default", Toast.LENGTH_SHORT).show();
                break;
            case RECIPE_FOUND:
//                Toast.makeText(getContext(), "found", Toast.LENGTH_SHORT).show();
                rvRecipeIngredient.setVisibility(View.VISIBLE);
                tvNoRecipeIngredient.setVisibility(View.GONE);
                break;
            case RECIPE_NOT_FOUND:
//                Toast.makeText(getContext(), "not found", Toast.LENGTH_SHORT).show();
                tvNoRecipeIngredient.setText(R.string.error_load_recipe_ingredient_428);
                tvNoRecipeIngredient.setVisibility(View.VISIBLE);
                rvRecipeIngredient.setVisibility(View.GONE);
                break;
            case ERROR:
//                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                tvNoRecipeIngredient.setText(R.string.error_500);
                tvNoRecipeIngredient.setVisibility(View.VISIBLE);
                rvRecipeIngredient.setVisibility(View.GONE);
                break;
        }

        rvRecipeFromFollower = view.findViewById(R.id.rvRecipeFromFollowing);
        rvRecipeFromFollower.setAdapter(recipeFromFollowerAdapter);
        rvRecipeFromFollower.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvRecipePopular = view.findViewById(R.id.rvRecipePopular);
        rvRecipePopular.setAdapter(recipePopularAdapter);
        rvRecipePopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvRecipeNew = view.findViewById(R.id.rvRecipeNew);
        rvRecipeNew.setAdapter(recipeNewestAdapter);
        rvRecipeNew.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));

        btnRefreshRecipeFromFollowing = view.findViewById(R.id.btnRefreshRecipeFromFollowing);
        btnRefreshRecipePopular = view.findViewById(R.id.btnRefreshRecipePopular);
        btnRefreshRecipeNew = view.findViewById(R.id.btnRefreshRecipeNew);

        tvSeeAllRecipeNew = view.findViewById(R.id.tvSeeAllRecipeNew);
        tvSearchBar = view.findViewById(R.id.tvSearchBar);
        setEvent();
        return view;
    }

    private void setEvent() {
        btnRefreshRecipeFromFollowing.setOnClickListener(view -> setRvRecipeFromFollowing());
        btnRefreshRecipePopular.setOnClickListener(view -> setRvRecipePopularData());
        btnRefreshRecipeNew.setOnClickListener(view -> setRvRecipeNewestData());
        tvSeeAllRecipeNew.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), RecipesNewestActivity.class));
        });
        tvSearchBar.setOnClickListener(view ->{
            mainActivity.goToSearchFragment();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        scrollX = scrollViewContainer.getScrollX();
        scrollY = scrollViewContainer.getScrollX();
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
                                recipeNewestAdapter = new RecipeHomeAdapter(HomeFragment.this, recipesNew, R.layout.item_recipe_small);
                                rvRecipeNew.setAdapter(recipeNewestAdapter);
                            }
                        }
                        else if (response.code() == 429) {
                            Toast.makeText(getContext(), getText(R.string.error_get_new_recipe_429), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRecipes> call, Throwable t) {
                        Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }
    private void setRvRecipePopularData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getRecipePopular(MyAuthorization.getInstance().getToken());
                call.enqueue(new Callback<ResponseRecipes>() {
                    @Override
                    public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                        if (response.isSuccessful()){
                            ResponseRecipes responseRecipe = response.body();
                            if (responseRecipe != null && responseRecipe.getData().size()>0){
                                recipesPopular.clear();
                                recipesPopular.addAll(responseRecipe.getData());
                                recipePopularAdapter = new RecipeHomeAdapter(HomeFragment.this, recipesPopular, R.layout.item_recipe);
                                rvRecipePopular.setAdapter(recipePopularAdapter);
                            }
                        }
                        else if (response.code() == 432) {
                            Toast.makeText(getContext(), R.string.error_get_popular_recipe_432, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRecipes> call, Throwable t) {
                        Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }
    private void setRvRecipeFromFollowing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getRecipeFromFollower(MyAuthorization.getInstance().getToken());
                call.enqueue(new Callback<ResponseRecipes>() {
                    @Override
                    public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                        if (response.isSuccessful()){
                            ResponseRecipes responseRecipe = response.body();
                            if (responseRecipe != null && responseRecipe.getData().size()>0){
                                recipesFromFollowing.clear();
                                recipesFromFollowing.addAll(responseRecipe.getData());
                                recipeFromFollowerAdapter = new RecipeHomeAdapter(HomeFragment.this, recipesFromFollowing, R.layout.item_recipe);
                                rvRecipeFromFollower.setAdapter(recipeFromFollowerAdapter);
                            }
                        }
                        else if (response.code() == 436) {
                            Toast.makeText(getContext(), getText(R.string.error_get_following_recipe_436), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRecipes> call, Throwable t) {
                        Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }
    public void setRvRecipeIngredientData(String ingredientName) {
        tvNoRecipeIngredient.setVisibility(View.GONE);
        rvRecipeIngredient.setVisibility(View.GONE);
        prbLoadRecipeIngredient.setVisibility(View.VISIBLE);

        Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getRecipesByIngredient(MyAuthorization.getInstance().getToken(), ingredientName);
        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                prbLoadRecipeIngredient.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()){
                    recipeIngredientRvStatus = RECIPE_FOUND;
                    ResponseRecipes responseRecipe = response.body();
                    if (responseRecipe != null && responseRecipe.getData().size()>0){
                        rvRecipeIngredient.setVisibility(View.VISIBLE);
                        tvNoRecipeIngredient.setVisibility(View.GONE);

                        recipesByIngredient.clear();
                        recipesByIngredient.addAll(responseRecipe.getData());
                        recipeIngredientAdapter = new RecipeHomeAdapter(HomeFragment.this, recipesByIngredient, R.layout.item_recipe);
                        rvRecipeIngredient.setAdapter(recipeIngredientAdapter);
                    }
                }
                else if (response.code() == 428) {
                    recipeIngredientRvStatus = RECIPE_NOT_FOUND;
                    tvNoRecipeIngredient.setText(R.string.error_load_recipe_ingredient_428);
                    tvNoRecipeIngredient.setVisibility(View.VISIBLE);
                    Log.e("TEST", "Không tìm thấy công thức theo nguyên liệu");
                }
                else {
                    recipeIngredientRvStatus = ERROR;
                    tvNoRecipeIngredient.setText(R.string.error_500);
                    tvNoRecipeIngredient.setVisibility(View.VISIBLE);
                    Log.e("TEST", "Không thể lấy công thức theo nguyên liệu");
                }
            }

            @Override
            public void onFailure(Call<ResponseRecipes> call, Throwable t) {
                recipeIngredientRvStatus = ERROR;
                tvNoRecipeIngredient.setText(R.string.error_500);
                tvNoRecipeIngredient.setVisibility(View.VISIBLE);
                Log.e("TEST", "ERROR: Không thể lấy công thức theo nguyên liệu");
                Log.e("TEST", t.getMessage());
                t.printStackTrace();
            }
        });
    }
    private void setRvIngredientSeasonData() {
        Call<ResponseIngredients> call = IngredientApiInstance.getInstance().getIngredientBySeason(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseIngredients>() {
            @Override
            public void onResponse(Call<ResponseIngredients> call, Response<ResponseIngredients> response) {
                if (response.isSuccessful()){
                    ResponseIngredients responseIngredients = response.body();
                    ingredientList.addAll(responseIngredients.getData());
                    ingredientSeasonAdapter = new IngredientSeasonAdapter(HomeFragment.this, ingredientList);
                    rvIngredientSeason.setAdapter(ingredientSeasonAdapter);
                    // Lấy danh sách công thức theo nguyên liệu đầu tiên
                    if (responseIngredients.getData().size()>0){
                        setRvRecipeIngredientData(responseIngredients.getData().get(0).getName());
                    }
                }
                else if (response.code() == 427){
                    Toast.makeText(getContext(), getText(R.string.error_get_season_ingredient_427), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ResponseIngredients> call, Throwable t) {
                Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
    public void viewRecipeDetail(int recipeId){
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
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
                    Toast.makeText(getContext(), (isFavorite ? getText(R.string.success_like) : getText(R.string.success_unlike)), Toast.LENGTH_SHORT).show();
                    recipe.setFavorite(isFavorite);
                    updateLikeStatus.setImageResource(isFavorite ? R.drawable.baseline_favorite_16 : R.drawable.baseline_favorite_border_16);
                    numOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
                }
                else if (response.code() == 432){
                    Toast.makeText(getContext(), getText(R.string.error_recipe_432), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 444) {
                    Toast.makeText(getContext(), getText(R.string.error_like_recipe_444), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 445) {
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

    public void showDialogBookmark(int recipeId) {
        ArrayList<RecipeListDetail> recipeListDetails = new ArrayList<>();

        Dialog dialog = new Dialog(getContext());
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

        rvRecipeListsDetail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
            public void onFailure(Call<ResponseRecipeListsDetail> call, Throwable t) {
                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), R.string.success_bookmark_recipe, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(getContext(), R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 443){
                    Toast.makeText(getContext(), R.string.error_bookmark_443, Toast.LENGTH_SHORT).show();
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
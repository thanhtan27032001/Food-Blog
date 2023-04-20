package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.adapter_recycler_view.BookmarkAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeHomeAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeOtherUserAdapter;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.api_instance.UserApiInstance;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeListDetail;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.example.foodblog.model.ResponseRecipes;
import com.example.foodblog.model.ResponseUser;
import com.example.foodblog.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserWallActivity extends AppCompatActivity {
    public static final String TAG_USER_ID = "TAG_USER_ID";
    private int userId;
    private User userInfo;

    SwipeRefreshLayout swfUserWall;
    TextView tvUserName, tvEmail, tvFollow, tvUnfollow, tvNumOfRecipe, tvNumOfFollower, tvNumOfFollowing, tvLocation, tvIntro;
    ImageButton btnBack;
    ImageView imgAvt;
    RecyclerView rvRecipe;
    RecipeOtherUserAdapter recipeAdapter;
    ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wall);
        setControl();
        setEvent();
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        userId = intent.getIntExtra(TAG_USER_ID, -1);
        if (userId != -1){
            setUserData();
        }
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
        swfUserWall.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRvRecipeData();
            }
        });
        tvFollow.setOnClickListener(view -> {
            changeFollowStatus(true);
        });
        tvUnfollow.setOnClickListener(view -> {
            changeFollowStatus(false);
        });
    }

    private void setControl() {
        swfUserWall = findViewById(R.id.swfUserWall);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvFollow = findViewById(R.id.tvFollow);
        tvUnfollow = findViewById(R.id.tvUnfollow);
        tvNumOfRecipe = findViewById(R.id.tvNumOfRecipe);
        tvNumOfFollower = findViewById(R.id.tvNumOfFollower);
        tvNumOfFollowing = findViewById(R.id.tvNumOfFollowing);
        tvLocation = findViewById(R.id.tvLocation);
        tvIntro = findViewById(R.id.tvIntroduce);
        btnBack = findViewById(R.id.btnBack);
        imgAvt = findViewById(R.id.imgAvatar);
        rvRecipe = findViewById(R.id.rvRecipe);

        rvRecipe.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        rvRecipe.setAdapter(recipeAdapter);
    }

    private void setUserData() {
        Call<ResponseUser> call = UserApiInstance.getInstance().getUserInfo(MyAuthorization.getInstance().getToken(), userId);
        call.enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                if (response.isSuccessful() || response.code() == 200){
                    userInfo = response.body().getData();
                    if (userInfo != null){
                        tvUserName.setText(userInfo.getFullName());
                        tvEmail.setText(userInfo.getEmail());
                        tvNumOfRecipe.setText(String.valueOf(userInfo.getNumOfRecipe()));
                        tvNumOfFollower.setText(String.valueOf(userInfo.getNumOfFollower()));
                        tvNumOfFollowing.setText(String.valueOf(userInfo.getNumOfFollowing()));
                        tvLocation.setText(userInfo.getAddress());
                        tvIntro.setText(userInfo.getIntroduce());
                        Picasso.get().load(ApiUrl.IMAGE_URL + userInfo.getAvatar()).into(imgAvt);
                        setFollowAndUnfollowButton();

                        setRvRecipeData();
                    }
                }
                else {
                    Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setFollowAndUnfollowButton(){
        if (userInfo.isFollowed()){
            tvUnfollow.setVisibility(View.VISIBLE);
            tvFollow.setVisibility(View.GONE);
        }
        else {
            tvUnfollow.setVisibility(View.GONE);
            tvFollow.setVisibility(View.VISIBLE);
        }
    }

    private void setRvRecipeData() {
        Call<ResponseRecipes> call = UserApiInstance.getInstance().getAllUserPublicRecipe(MyAuthorization.getInstance().getToken(), userId);
        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                swfUserWall.setRefreshing(false);
                if (response.isSuccessful() || response.code() == 200){
                    recipes.clear();
                    recipes.addAll(response.body().getData());
                    recipeAdapter = new RecipeOtherUserAdapter(UserWallActivity.this, userInfo, recipes, R.layout.item_recipe_small_simple);
                    rvRecipe.setAdapter(recipeAdapter);
                }
                else {
                    Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                swfUserWall.setRefreshing(false);
            }
        });
    }

    public void changeRecipeFavorite(Recipe recipe, boolean isFavorite, ImageButton updateLikeStatus, TextView numOfLike) {
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
                    Toast.makeText(UserWallActivity.this, (isFavorite ? getText(R.string.success_like) : getText(R.string.success_unlike)), Toast.LENGTH_SHORT).show();
                    recipe.setFavorite(isFavorite);
                    updateLikeStatus.setImageResource(isFavorite ? R.drawable.baseline_favorite_16 : R.drawable.baseline_favorite_border_16);
                    numOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
                }
                else if (response.code() == 432){
                    Toast.makeText(UserWallActivity.this, getText(R.string.error_recipe_432), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 444) {
                    Toast.makeText(UserWallActivity.this, getText(R.string.error_like_recipe_444), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 445) {
                    Toast.makeText(UserWallActivity.this, R.string.error_unlike_recipe_445, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void showDialogBookmark(int recipeId) {
        Log.e("test", recipeId + "");
        ArrayList<RecipeListDetail> recipeListDetails = new ArrayList<>();

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_bookmark);
        LinearLayout layout = dialog.findViewById(R.id.layoutDialogBookmark);
        RecyclerView rvRecipeListsDetail = dialog.findViewById(R.id.rvRecipeListDetail);
        TextView tvSubmit = dialog.findViewById(R.id.btnSubmit);
        TextView tvCancel = dialog.findViewById(R.id.btnCancel);

        tvSubmit.setOnClickListener(view -> {

        });
        tvCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        rvRecipeListsDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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

    public void viewRecipeDetail(int recipeId) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.TAG_RECIPE, recipeId);
        startActivity(intent);
    }

    public void changeFollowStatus(boolean isMakeFollowed){
        Call<ResponseObject> call;
        if (isMakeFollowed){
            call = UserApiInstance.getInstance().followOtherUser(MyAuthorization.getInstance().getToken(), userId);
        }
        else {
            call = UserApiInstance.getInstance().unfollowOtherUser(MyAuthorization.getInstance().getToken(), userId);
        }
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    userInfo.setFollowed(isMakeFollowed);
                    tvNumOfFollower.setText(String.valueOf(userInfo.getNumOfFollower()));
                    if (isMakeFollowed){
                        setFollowAndUnfollowButton();
                        Toast.makeText(UserWallActivity.this, R.string.success_follow, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        setFollowAndUnfollowButton();
                        Toast.makeText(UserWallActivity.this, R.string.success_unfollow, Toast.LENGTH_SHORT).show();
                    }
                }
                else if (response.code() == 435) {
                    Toast.makeText(UserWallActivity.this, R.string.error_unfollow_435, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UserWallActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                t.printStackTrace();
            }
        });
    }
}
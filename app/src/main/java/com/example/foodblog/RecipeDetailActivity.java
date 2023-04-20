package com.example.foodblog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.adapter_recycler_view.BookmarkAdapter;
import com.example.foodblog.adapter_recycler_view.IngredientsDetailAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeCommentAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeStepAdapter;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api_instance.CommentApiInstance;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.model.BookmarkRecipeInfo;
import com.example.foodblog.model.Comment;
import com.example.foodblog.model.IngredientDetail;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeListDetail;
import com.example.foodblog.model.RecipeStep;
import com.example.foodblog.model.ResponseAddComment;
import com.example.foodblog.model.ResponseGetComment;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipe;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String TAG_RECIPE = "TAG_RECIPE";
    private static final int MAX_DESCRIPTION_LENGTH = 300;
    private boolean isCollapsed = true;
    private Recipe recipe;
    private String myComment;
    ImageView imgRecipeImage, imgAvt, imgMyAvt;
    ImageButton btnMore, btnBack, btnLike, btnBookmark;
    TextView tvRecipeName, tvAuthorUserName, tvDatePublic, tvDescription, tvExpandCollapse, tvAmount,
            tvPrepareTime, tvCookingTime, tvNumOfLike, tvNumOfComment, tvMyName, tvMyCommentDate, tvMyComment;
    RecyclerView rvIngredientDetail, rvCookingStep, rvComment;
    Button btnAddComment;
    LinearLayout layoutMyComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setControl();
        setEvent();
        setData();
//        this.registerForContextMenu(imgMore);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_comment, menu);
    }
    private void setEvent() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
        btnAddComment.setOnClickListener(view -> {
            showCommentDialog(null, true);
        });
        tvExpandCollapse.setOnClickListener(view -> {
            if (isCollapsed){
                tvDescription.setText(recipe.getDescription());
                tvExpandCollapse.setText(R.string.tv_collapse);
            }
            else {
                tvDescription.setText(recipe.getDescription().substring(0, MAX_DESCRIPTION_LENGTH));
                tvExpandCollapse.setText(R.string.tv_expand);
            }
            isCollapsed = !isCollapsed;;
        });
        btnMore.setOnClickListener(view -> {
            showCommentPopupMenu();
        });
        btnLike.setOnClickListener(view -> {
            changeRecipeFavorite(!recipe.isFavorite());
        });
        imgAvt.setOnClickListener(view -> {
            viewAuthorWall();
        });
        tvAuthorUserName.setOnClickListener(view -> {
            viewAuthorWall();
        });
        btnBookmark.setOnClickListener(view -> {
            showDialogBookmark(recipe.getRecipeId());
        });
    }
    private void setControl() {
        tvRecipeName = findViewById(R.id.tvRecipeName);
        tvAuthorUserName = findViewById(R.id.tvRecipeUserName);
        tvDatePublic = findViewById(R.id.tvRecipeCreateDate);
        tvDescription = findViewById(R.id.tvRecipeDescription);
        tvExpandCollapse = findViewById(R.id.tvExpandCollapse);
        tvAmount = findViewById(R.id.tvAmount);
        tvPrepareTime = findViewById(R.id.tvPrepareTime);
        tvCookingTime = findViewById(R.id.tvCookingTime);
        tvNumOfLike = findViewById(R.id.tvNumOfLike);
        tvNumOfComment = findViewById(R.id.tvNumOfComment);
        tvMyName = findViewById(R.id.tvUserName);
        tvMyCommentDate = findViewById(R.id.tvCommentDate);
        tvMyComment = findViewById(R.id.tvComment);

        layoutMyComment = findViewById(R.id.layoutMyComment);

        imgRecipeImage = findViewById(R.id.imgRecipeImage);
        imgAvt = findViewById(R.id.imgAvatar);
        imgMyAvt = findViewById(R.id.imgMyAvatar);

        btnAddComment = findViewById(R.id.btnAddComment);
        btnBack = findViewById(R.id.imgBack);
        btnLike = findViewById(R.id.btnLike);
        btnBookmark = findViewById(R.id.btnBookmark);
        btnMore = findViewById(R.id.btnMore);


        rvIngredientDetail = findViewById(R.id.rvIngredientDetail);
        rvIngredientDetail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        rvCookingStep = findViewById(R.id.rvCookingStep);
        rvCookingStep.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        rvComment = findViewById(R.id.rvRecipeComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void showCommentDialog(String comment, boolean isAddComment) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_comment);
        LinearLayout layout = dialog.findViewById(R.id.layoutDialogAddComment);
        TextView tvRecipeName = dialog.findViewById(R.id.tvRecipeName);
        TextView tvUserName = dialog.findViewById(R.id.tvUserName);
        ImageView imgAvatar = dialog.findViewById(R.id.imgAvatar);
        EditText edtComment = dialog.findViewById(R.id.edtComment);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        tvRecipeName.setText(recipe.getRecipeName());
        tvUserName.setText(MyAuthorization.getInstance().getUser().getFullName());
        Picasso.get().load(ApiUrl.IMAGE_URL + MyAuthorization.getInstance().getUser().getAvatar()).into(imgAvatar);
        try { // Adjust dialog width fit to screen
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
            layout.requestLayout();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (isAddComment){
            btnSubmit.setOnClickListener(view -> {
                if (!edtComment.getText().toString().equals("")){
                    Call<ResponseAddComment> call = CommentApiInstance.getInstance().addComment(MyAuthorization.getInstance().getToken(), recipe.getRecipeId(), new Comment(edtComment.getText().toString()));
                    call.enqueue(new Callback<ResponseAddComment>() {
                        @Override
                        public void onResponse(Call<ResponseAddComment> call, Response<ResponseAddComment> response) {
                            if (response.isSuccessful()){
                                Toast.makeText(RecipeDetailActivity.this, R.string.success_add_comment, Toast.LENGTH_SHORT).show();
                                setCommentData(recipe.getRecipeId());
                                dialog.dismiss();
                            }
                            else if (response.code() == 432){
                                Toast.makeText(RecipeDetailActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.code() == 437){
                                Toast.makeText(RecipeDetailActivity.this, R.string.error_add_comment_437, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                                try {
                                    Toast.makeText(RecipeDetailActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseAddComment> call, Throwable t) {
                            Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
                }

            });
        }
        else {
            edtComment.setText(comment);
            btnSubmit.setOnClickListener(view -> {
                Call<ResponseAddComment> call = CommentApiInstance.getInstance().updateComment(MyAuthorization.getInstance().getToken(), recipe.getRecipeId(), new Comment(edtComment.getText().toString()));
                call.enqueue(new Callback<ResponseAddComment>() {
                    @Override
                    public void onResponse(Call<ResponseAddComment> call, Response<ResponseAddComment> response) {
                        if (response.isSuccessful()){
                            setCommentData(recipe.getRecipeId());
                            Toast.makeText(RecipeDetailActivity.this, R.string.success_edit_comment, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else if (response.code() == 434){
                            Toast.makeText(RecipeDetailActivity.this, R.string.error_edit_comment_434, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error", response.errorBody().string());
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAddComment> call, Throwable t) {
                        Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            });
        }

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void showCommentPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(this, btnMore);
        popupMenu.getMenuInflater().inflate(R.menu.menu_comment, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.optionUpdateComment:
                        showCommentDialog(myComment, false);
                        break;
                    case R.id.optionDeleteComment:
                        showDeleteCommentDialog();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void showDeleteCommentDialog(){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(getText(R.string.title_delete_comment))
                .setMessage(getText(R.string.message_delete_comment))
                .setPositiveButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<ResponseObject> call = CommentApiInstance.getInstance().deleteComment(MyAuthorization.getInstance().getToken(), recipe.getRecipeId());
                        call.enqueue(new Callback<ResponseObject>() {
                            @Override
                            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                                if (response.isSuccessful()){
                                    Toast.makeText(RecipeDetailActivity.this, R.string.success_delete_comment, Toast.LENGTH_SHORT).show();
                                    setCommentData(recipe.getRecipeId());
                                }
                                else if (response.code() == 434) {
                                    Toast.makeText(RecipeDetailActivity.this, R.string.error_delete_comment_434, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });
                    }
                })
                .setNegativeButton(getText(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        alertDialog.show();
    }
    private void setRecipeData(int recipeId){
        // Data recipe
        Call<ResponseRecipe> call = RecipeApiInstance.getInstance().getRecipeById(MyAuthorization.getInstance().getToken(), recipeId);
        call.enqueue(new Callback<ResponseRecipe>() {
            @Override
            public void onResponse(Call<ResponseRecipe> call, Response<ResponseRecipe> response) {
                if (response.isSuccessful() || response.code() == 200){
                    ResponseRecipe responseRecipe = response.body();
                    Recipe recipe = responseRecipe.getData();
                    RecipeDetailActivity.this.recipe = recipe;

                    tvRecipeName.setText(recipe.getRecipeName());
                    Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getImage()).into(imgRecipeImage);
                    Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getUser().getAvatar()).into(imgAvt);
                    tvAuthorUserName.setText(recipe.getUser().getFullName());
                    tvDatePublic.setText(" ・ ".concat(recipe.getDate(
                            getText(R.string.recipe_get_date_minute_ago),
                            getText(R.string.recipe_get_date_minutes_ago),
                            getText(R.string.recipe_get_date_hour_ago),
                            getText(R.string.recipe_get_date_hours_ago),
                            getText(R.string.recipe_get_date_yesterday))
                    ));

                    if (recipe.isFavorite()){
                        btnLike.setImageResource(R.drawable.baseline_favorite_24);
                        btnLike.setColorFilter(getColor(R.color.img_favorite_tint));
                    }
                    else {
                        btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
                        btnLike.setColorFilter(getColor(R.color.white));
                    }

                    if (recipe.getDescription() != null){

                        tvDescription.setText(recipe.getDescription().length() <= MAX_DESCRIPTION_LENGTH
                                ? recipe.getDescription()
                                : recipe.getDescription().substring(0, MAX_DESCRIPTION_LENGTH));

                        if (recipe.getDescription().length() <= MAX_DESCRIPTION_LENGTH){
                            tvExpandCollapse.setVisibility(View.GONE);
                        }
                        else {
                            tvExpandCollapse.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        tvDescription.setText(R.string.no_description);
                        tvExpandCollapse.setVisibility(View.GONE);
                    }

                    tvAmount.setText(recipe.getAmount() + " " + getText(R.string.unit_amount));
                    tvPrepareTime.setText(recipe.getPreparationTime() + " " + (recipe.getPreparationTime() > 1 ? getText(R.string.unit_minutes) : getText(R.string.unit_minute)));
                    tvCookingTime.setText(recipe.getCookingTime() + " " + (recipe.getCookingTime() > 1 ? getText(R.string.unit_minutes) : getText(R.string.unit_minute)));

                    ArrayList<IngredientDetail> ingredientDetails = responseRecipe.getData().getIngredients();
                    rvIngredientDetail.setAdapter(new IngredientsDetailAdapter(RecipeDetailActivity.this, ingredientDetails));

                    ArrayList<RecipeStep> recipeSteps = responseRecipe.getData().getSteps();
                    rvCookingStep.setAdapter(new RecipeStepAdapter(RecipeDetailActivity.this, recipeSteps));

                    tvNumOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
                }
                else if (response.code() == 432) {
                    Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_get_recipe_by_id_432), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRecipe> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setCommentData(int recipeId){
        // Data comment
        Call<ResponseGetComment> callComment = CommentApiInstance.getInstance().getAllCommentOfRecipe(MyAuthorization.getInstance().getToken(), recipeId);
        callComment.enqueue(new Callback<ResponseGetComment>() {
            @Override
            public void onResponse(Call<ResponseGetComment> call, Response<ResponseGetComment> response) {
                if (response.isSuccessful() || response.code() == 200){
                    // My comment
                    if (response.body().getMyComment() != null){
                        myComment = response.body().getMyComment().getComment();
                        layoutMyComment.setVisibility(View.VISIBLE);
                        btnAddComment.setVisibility(View.GONE);
                        Picasso.get().load(ApiUrl.IMAGE_URL + MyAuthorization.getInstance().getUser().getAvatar()).into(imgMyAvt);
                        tvMyName.setText(MyAuthorization.getInstance().getUser().getFullName());
                        tvMyCommentDate.setText(" ・ ".concat(response.body().getMyComment().getDate(
                                getText(R.string.recipe_get_date_minute_ago),
                                getText(R.string.recipe_get_date_minutes_ago),
                                getText(R.string.recipe_get_date_hour_ago),
                                getText(R.string.recipe_get_date_hours_ago),
                                getText(R.string.recipe_get_date_yesterday))
                        ));
                        tvMyComment.setText(response.body().getMyComment().getComment());
                    }
                    else {
                        layoutMyComment.setVisibility(View.GONE);
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                    // All comments
                    tvNumOfComment.setText(String.valueOf(response.body().getCommentCount()));
                    ArrayList<Comment> comments = response.body().getComments();
                    rvComment.setAdapter(new RecipeCommentAdapter(RecipeDetailActivity.this, comments));
                }
                else if (response.code() == 438){
                    tvNumOfComment.setText(String.valueOf(0));
                    layoutMyComment.setVisibility(View.GONE);
                    btnAddComment.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetComment> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RecipeDetailActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setData() {
        Intent intent = getIntent();
        int recipeId = intent.getIntExtra(TAG_RECIPE, -1);
        Log.e("RECIPE ID", recipeId + "");
        if (recipeId > -1){
            setRecipeData(recipeId);
            setCommentData(recipeId);
        }
        else {
            finish();
        }
    }

    public void changeRecipeFavorite(boolean isFavorite){
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
                    Toast.makeText(RecipeDetailActivity.this, (isFavorite ? "Yêu thích" : "Bỏ yêu thích") + " công thức thành công", Toast.LENGTH_SHORT).show();
                    recipe.setFavorite(isFavorite);
                    tvNumOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
                    if (isFavorite){
                        btnLike.setImageResource(R.drawable.baseline_favorite_24);
                        btnLike.setColorFilter(getColor(R.color.img_favorite_tint));
                    }
                    else {
                        btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
                        btnLike.setColorFilter(getColor(R.color.white));
                    }
                }
                else if (response.code() == 432){
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 444) {
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_like_recipe_444, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 445) {
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_unlike_recipe_445, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void viewAuthorWall(){
        Intent intent = new Intent(this, UserWallActivity.class);
        intent.putExtra(UserWallActivity.TAG_USER_ID, recipe.getUser().getUserId());
        startActivity(intent);
    }

    public void showDialogBookmark(int recipeId) {
        ArrayList<RecipeListDetail> recipeListDetails = new ArrayList<>();

        Dialog dialog = new Dialog(this);
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
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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

    private void bookmarkRecipe(int recipeId, ArrayList<RecipeListDetail> recipeListDetails) {
        BookmarkRecipeInfo info = new BookmarkRecipeInfo(recipeListDetails);
        Call<ResponseObject> call = RecipeApiInstance.getInstance().bookmarkRecipe(
                MyAuthorization.getInstance().getToken(),
                recipeId,
                info);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    Toast.makeText(RecipeDetailActivity.this, R.string.success_bookmark_recipe, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 443){
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_bookmark_443, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RecipeDetailActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
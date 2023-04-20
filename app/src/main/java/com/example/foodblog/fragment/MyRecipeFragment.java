package com.example.foodblog.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodblog.AddRecipeActivity;
import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.RecipeDetailActivity;
import com.example.foodblog.UpdateRecipeActivity;
import com.example.foodblog.adapter_recycler_view.MyRecipeAdapter;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecipeFragment extends Fragment {

    ImageButton btnAddRecipe;
    SwipeRefreshLayout swfMyRecipe;
    RecyclerView rvMyRecipes;
    ArrayList<Recipe> myRecipes = new ArrayList<>();
    MyRecipeAdapter myRecipeAdapter;
    public MyRecipeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataRv();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipe, container, false);

        btnAddRecipe = view.findViewById(R.id.btnSaveRecipe);

        swfMyRecipe = view.findViewById(R.id.swfMyRecipe);

        rvMyRecipes = view.findViewById(R.id.rvMyRecipes);
        rvMyRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        rvMyRecipes.setAdapter(myRecipeAdapter);

        setEvent();
        return view;
    }

    private void setEvent(){
        btnAddRecipe.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AddRecipeActivity.class));
        });
        swfMyRecipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDataRv();
            }
        });
    }

    private void setDataRv() {
        Call<ResponseRecipes> call = RecipeApiInstance.getInstance().getAllMyRecipes(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseRecipes>() {
            @Override
            public void onResponse(Call<ResponseRecipes> call, Response<ResponseRecipes> response) {
                swfMyRecipe.setRefreshing(false);
                if (response.isSuccessful() || response.code() == 200){
                    myRecipes.addAll(response.body().getData());
                    myRecipeAdapter = new MyRecipeAdapter(MyRecipeFragment.this, myRecipes);
                    rvMyRecipes.setAdapter(myRecipeAdapter);
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
                swfMyRecipe.setRefreshing(false);
            }
        });
//        myRecipes = new ArrayList<>();
//        myRecipeAdapter = new MyRecipeAdapter(MyRecipeFragment.this, myRecipes);
    }

    public void viewRecipeDetail(int recipeId){
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.TAG_RECIPE, recipeId);
        getContext().startActivity(intent);
    }

    public void changeRecipeStatus(Recipe recipe, boolean isPrivate) {
        String newStatus = isPrivate ? Recipe.STATUS_PRIVATE : Recipe.STATUS_PUBLIC;
        Recipe temp = new Recipe();
        temp.setStatus(newStatus);
        Call<ResponseObject> call = RecipeApiInstance.getInstance().updateRecipeStatus(MyAuthorization.getInstance().getToken(), recipe.getRecipeId(), temp);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipe.setStatus(newStatus);
                    myRecipeAdapter.notifyItemChanged(myRecipes.indexOf(recipe));
                    Toast.makeText(getContext(), "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 432){
                    Toast.makeText(getContext(), R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
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

    public void deleteRecipe(Recipe recipe){
        Call<ResponseObject> call = RecipeApiInstance.getInstance().deleteRecipe(MyAuthorization.getInstance().getToken(), recipe.getRecipeId());
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    Toast.makeText(getContext(), "Xóa công thức thành công", Toast.LENGTH_SHORT).show();
                    myRecipeAdapter.notifyItemRemoved(myRecipes.indexOf(recipe));
                    myRecipes.remove(recipe);
                }
                else if (response.code() == 432){
                    Toast.makeText(getContext(), R.string.error_recipe_432, Toast.LENGTH_SHORT).show();
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

    public void showPopupMore(View constrainView, Recipe recipe){
        PopupMenu popupMenu = new PopupMenu(getContext(), constrainView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_recipe_manager, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                popupMenu.dismiss();
                switch (menuItem.getItemId()){
                    case R.id.optionUpdateRecipe:
                        updateRecipe(recipe);
                        break;
                    case R.id.optionUpdateRecipeStatus:
                        showDialogUpdateRecipeStatus(recipe);
                        popupMenu.dismiss();
                        break;
                    case R.id.optionDeleteRecipe:
                        showDialogDeleteRecipe(recipe);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void updateRecipe(Recipe recipe) {
        Intent intent = new Intent(getContext(), UpdateRecipeActivity.class);
        intent.putExtra(UpdateRecipeActivity.TAG_RECIPE_ID, recipe.getRecipeId());
        startActivity(intent);
    }

    private void showDialogUpdateRecipeStatus(Recipe recipe) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_recipe_status);
        //
        LinearLayout layout = dialog.findViewById(R.id.layoutChangeRecipeStatus);
        RadioButton radPrivate = dialog.findViewById(R.id.radPrivate);
        RadioButton radPublic = dialog.findViewById(R.id.radPublic);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        //
        try { // Adjust dialog width fit to screen
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
            layout.requestLayout();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (recipe.getStatus().equals(Recipe.STATUS_PRIVATE)){
            radPrivate.setChecked(true);
        }
        else {
            radPublic.setChecked(true);
        }
        btnSubmit.setOnClickListener(view -> {
            if (radPrivate.isChecked() && recipe.getStatus().equals(Recipe.STATUS_PRIVATE)){
                // do nothing
                Toast.makeText(getContext(), R.string.error_change_recipe_status_private, Toast.LENGTH_SHORT).show();
            }
            else if(!radPrivate.isChecked() && recipe.getStatus().equals(Recipe.STATUS_PUBLIC)){
                Toast.makeText(getContext(), R.string.error_change_recipe_status_public, Toast.LENGTH_SHORT).show();
            }
            else {
                changeRecipeStatus(recipe, radPrivate.isChecked());
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showDialogDeleteRecipe(Recipe recipe) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        alertDialog = builder
                .setTitle(getText(R.string.title_delete_recipe))
                .setMessage(getText(R.string.message_delete_recipe) + " \"" + recipe.getRecipeName() + "\"?")
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteRecipe(recipe);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        alertDialog.show();
    }
}
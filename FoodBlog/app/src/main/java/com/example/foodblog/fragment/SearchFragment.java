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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.RecipeDetailActivity;
import com.example.foodblog.SearchRecipeInterface;
import com.example.foodblog.SearchResultActivity;
import com.example.foodblog.adapter_recycler_view.BookmarkAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeNameSearchAdapter;
import com.example.foodblog.adapter_recycler_view.RecipeSearchAdapter;
import com.example.foodblog.api_instance.RecipeApiInstance;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.model.BookmarkRecipeInfo;
import com.example.foodblog.model.Recipe;
import com.example.foodblog.model.RecipeListDetail;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipe;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.example.foodblog.model.ResponseRecipeNames;
import com.example.foodblog.model.SearchHistories;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SearchRecipeInterface {
//    private static final String TAG_SEARCH_HISTORIES = "TAG_SEARCH_HISTORIES";
    public static final String TAG_SEARCH_VALUE = "TAG_SEARCH_VALUE";
//    private SearchHistories searchHistories;
//    private SearchHistoryAdapter searchHistoryAdapter;

    private LinearLayout layoutMain, layoutSearch;
    private TextView tvSearchBar;
    private SearchView searchViewRecipe;
    private ImageButton btnBack;
//    private ListView rvSearchHistory;
    private RecyclerView rvRecipeNameSearch, rvRecipeSearchHistory;
    private RecipeNameSearchAdapter recipeSearchAdapter;
    private RecipeSearchAdapter recipeSearchHistoryAdapter;
    ArrayList<Recipe> recipeNamesSearch = new ArrayList<>();
    ArrayList<Recipe> recipeSearchHistories = new ArrayList<>();
    ArrayList<Integer> recipeIdSearchHistories = new ArrayList<>();
    public SearchFragment() {
    }

//    private void showRecipeResult(String searchValue){
//        Intent intent = new Intent(getContext(), SearchResultActivity.class);
//        intent.putExtra(TAG_SEARCH_VALUE, searchValue);
//        startActivity(intent);
//    }
//    public void setHistoryValue(String value){
//        searchViewRecipe.setQuery(value, false);
//    }
    private void setEvent(){
        tvSearchBar.setOnClickListener(view -> {
            layoutSearch.setVisibility(View.VISIBLE);
            searchViewRecipe.requestFocus();
        });
        btnBack.setOnClickListener(view -> {
            searchViewRecipe.setQuery("", false);
            layoutSearch.setVisibility(View.GONE);
        });
        searchViewRecipe.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipeByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                searchHistoryAdapter.getFilter().filter(newText);
                searchRecipeName(newText);
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        searchHistories = new SearchHistories();
//        searchHistories.loadHistories(getContext());
//        searchHistoryAdapter = new SearchHistoryAdapter(getContext(), R.layout.item_search_history, searchHistories.getHistories(), SearchFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        layoutMain = view.findViewById(R.id.layoutMain);
        layoutSearch = view.findViewById(R.id.layoutSearch);

        tvSearchBar = view.findViewById(R.id.tvSearchBar);

//        rvSearchHistory = view.findViewById(R.id.rvSearchHistory);
//        rvSearchHistory.setAdapter(searchHistoryAdapter);

        searchViewRecipe = view.findViewById(R.id.searchViewRecipe);

        btnBack = view.findViewById(R.id.btnBack);

        rvRecipeNameSearch = view.findViewById(R.id.rvRecipeNameSearch);
        rvRecipeNameSearch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvRecipeNameSearch.setAdapter(recipeSearchAdapter);

        rvRecipeSearchHistory = view.findViewById(R.id.rvSearchHistories);
        rvRecipeSearchHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setEvent();
        setData();
        return view;
    }

    private void setData() {
        setRvSearchHistoriesData();
    }

    private void setRvSearchHistoriesData() {
        SearchHistories searchHistories = SearchHistories.getInstance(getContext());

        recipeIdSearchHistories = searchHistories.getRecipeIdHistories();

        recipeSearchHistories.clear();
        recipeSearchHistoryAdapter = new RecipeSearchAdapter(SearchFragment.this, recipeSearchHistories);
        rvRecipeSearchHistory.setAdapter(recipeSearchHistoryAdapter);

        for (int recipeId: recipeIdSearchHistories){
            Log.e("test", "recipeId " + recipeId);
            Call<ResponseRecipe> call = RecipeApiInstance.getInstance().getRecipeById(MyAuthorization.getInstance().getToken(), recipeId);
            call.enqueue(new Callback<ResponseRecipe>() {
                @Override
                public void onResponse(Call<ResponseRecipe> call, Response<ResponseRecipe> response) {
                    if (response.isSuccessful() || response.code() == 200){
                        recipeSearchHistories.add(response.body().getData());
                        recipeSearchHistoryAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ResponseRecipe> call, Throwable t) {

                }
            });
        }
    }

    private void searchRecipeName(String searchValue) {
        if (searchValue.equals("")){
            recipeNamesSearch.clear();
            recipeSearchAdapter = new RecipeNameSearchAdapter(SearchFragment.this, recipeNamesSearch);
            rvRecipeNameSearch.setAdapter(recipeSearchAdapter);
        }
        else {
            Call<ResponseRecipeNames> call = RecipeApiInstance.getInstance().searchRecipeNames(MyAuthorization.getInstance().getToken(), searchValue);
            call.enqueue(new Callback<ResponseRecipeNames>() {
                @Override
                public void onResponse(Call<ResponseRecipeNames> call, Response<ResponseRecipeNames> response) {
                    if (response.isSuccessful() || response.code() == 200){
                        recipeNamesSearch.clear();
                        recipeNamesSearch.addAll(response.body().getData());
                        recipeSearchAdapter = new RecipeNameSearchAdapter(SearchFragment.this, recipeNamesSearch);
                        rvRecipeNameSearch.setAdapter(recipeSearchAdapter);
                    }
                    else if (response.code() == 432){
                        recipeNamesSearch.clear();
                        recipeSearchAdapter = new RecipeNameSearchAdapter(SearchFragment.this, recipeNamesSearch);
                        rvRecipeNameSearch.setAdapter(recipeSearchAdapter);
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
                public void onFailure(Call<ResponseRecipeNames> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
    }

    public void searchRecipeByName(String recipeName){
        Intent intent = new Intent(getContext(), SearchResultActivity.class);
        intent.putExtra(TAG_SEARCH_VALUE, recipeName);
        startActivity(intent);
    }

    @Override
    public void viewRecipeDetail(int recipeId) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.TAG_RECIPE, recipeId);startActivity(intent);
    }

    @Override
    public String getMyText(int resId) {
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
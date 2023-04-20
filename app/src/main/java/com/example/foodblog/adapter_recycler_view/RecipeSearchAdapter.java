package com.example.foodblog.adapter_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.SearchRecipeInterface;
import com.example.foodblog.SearchResultActivity;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeSearchAdapter extends RecyclerView.Adapter<RecipeSearchAdapter.MyViewHolder> {

    private SearchRecipeInterface context;
    private ArrayList<Recipe> recipes;

    public RecipeSearchAdapter(SearchRecipeInterface context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeSearchAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeSearchAdapter.MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getImage()).into(holder.imgPreview);
        holder.tvTitle.setText(recipe.getRecipeName());

        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getUser().getAvatar()).into(holder.imgAvatar);

        if (recipe.getUser().getFullName().length() <= 16){
            holder.tvUserName.setText(recipe.getUser().getFullName());
        }
        else {
            String name = recipe.getUser().getFullName().substring(0, 16).trim() + "...";
            holder.tvUserName.setText(name);
        }
        holder.tvDate.setText(recipe.getDate(
                context.getMyText(R.string.recipe_get_date_minute_ago),
                context.getMyText(R.string.recipe_get_date_minutes_ago),
                context.getMyText(R.string.recipe_get_date_hour_ago),
                context.getMyText(R.string.recipe_get_date_hours_ago),
                context.getMyText(R.string.recipe_get_date_yesterday)
        ));
        holder.tvDescription.setText(recipe.getDescription());
        holder.tvNumOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
        holder.btnLike.setImageResource(recipe.isFavorite() ? R.drawable.baseline_favorite_16 : R.drawable.baseline_favorite_border_16);

        holder.layoutRecipe.setOnClickListener(view -> {
            context.viewRecipeDetail(recipe.getRecipeId());
        });
        holder.btnLike.setOnClickListener(view -> {
            context.changeRecipeFavorite(recipe, !recipe.isFavorite(), holder.btnLike, holder.tvNumOfLike);
        });
        holder.btnBookmark.setOnClickListener(view -> {
            context.showDialogBookmark(recipe.getRecipeId());
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView layoutRecipe;
        ImageView imgPreview, imgAvatar;
        TextView tvTitle, tvUserName, tvDate, tvDescription, tvNumOfLike;
        ImageButton btnLike, btnBookmark;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRecipe = itemView.findViewById(R.id.layoutRecipe);
            imgPreview = itemView.findViewById(R.id.imgRecipePreview);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvUserName = itemView.findViewById(R.id.tvRecipeUserName);
            tvDate = itemView.findViewById(R.id.tvRecipeCreateDate);
            tvDescription = itemView.findViewById(R.id.tvRecipeDescription);
            tvNumOfLike = itemView.findViewById(R.id.tvNumOfLike);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
        }
    }
}

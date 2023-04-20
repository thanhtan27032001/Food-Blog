package com.example.foodblog.adapter_recycler_view;

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
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeHomeAdapter extends RecyclerView.Adapter<RecipeHomeAdapter.MyViewHolder> {
    private HomeFragment context;
    private ArrayList<Recipe> recipes;
    private int layoutResource;

    public RecipeHomeAdapter(HomeFragment context, ArrayList<Recipe> recipes, int layoutResource) {
        this.context = context;
        this.recipes = recipes;
        this.layoutResource = layoutResource;
    }

    @NonNull
    @Override
    public RecipeHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHomeAdapter.MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getImage()).into(holder.imgPreview);
        holder.tvTitle.setText(recipe.getRecipeName());

        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getUser().getAvatar()).into(holder.imgAvt);

        if (recipe.getUser().getFullName().length() <= 16){
            holder.tvUserName.setText(recipe.getUser().getFullName());
        }
        else {
            String name = recipe.getUser().getFullName().substring(0, 16).trim() + "...";
            holder.tvUserName.setText(name);
        }
        holder.tvCreateDate.setText(recipe.getDate(
                context.getText(R.string.recipe_get_date_minute_ago),
                context.getText(R.string.recipe_get_date_minutes_ago),
                context.getText(R.string.recipe_get_date_hour_ago),
                context.getText(R.string.recipe_get_date_hours_ago),
                context.getText(R.string.recipe_get_date_yesterday)
        ));
        holder.tvNumOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
        holder.btnLike.setImageResource(recipe.isFavorite() ? R.drawable.baseline_favorite_16 : R.drawable.baseline_favorite_border_16);
        holder.btnLike.setOnClickListener(view -> {
            context.changeRecipeFavorite(recipe, !recipe.isFavorite(), holder.btnLike, holder.tvNumOfLike);
        });
        holder.btnBookmark.setOnClickListener(view -> {
            context.showDialogBookmark(recipe.getRecipeId());
        });
        holder.layoutRecipe.setOnClickListener(view -> {
            context.viewRecipeDetail(recipe.getRecipeId());
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(recipes.size(), 20);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView layoutRecipe;
        TextView tvTitle, tvUserName, tvCreateDate, tvNumOfLike;
        ImageView imgPreview, imgAvt;
        ImageButton btnLike, btnBookmark;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRecipe = itemView.findViewById(R.id.layoutRecipePreview);
            imgPreview = itemView.findViewById(R.id.imgRecipePreview);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvUserName = itemView.findViewById(R.id.tvRecipeUserName);
            tvCreateDate = itemView.findViewById(R.id.tvRecipeCreateDate);
            tvNumOfLike = itemView.findViewById(R.id.tvNumOfLike);
            imgAvt = itemView.findViewById(R.id.imgAvatar);
            btnLike = itemView.findViewById(R.id.imgLike);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);

        }
    }
}

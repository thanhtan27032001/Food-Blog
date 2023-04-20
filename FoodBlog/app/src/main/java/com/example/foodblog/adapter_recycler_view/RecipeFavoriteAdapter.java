package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.MyFormat;
import com.example.foodblog.R;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.fragment.MyFavoriteFragment;
import com.example.foodblog.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeFavoriteAdapter extends RecyclerView.Adapter<RecipeFavoriteAdapter.MyViewHolder> {

    private MyFavoriteFragment context;
    private ArrayList<Recipe> recipes;

    public RecipeFavoriteAdapter(MyFavoriteFragment context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeFavoriteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_favorited, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFavoriteAdapter.MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getImage()).into(holder.imgRecipePreview);
        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getUser().getAvatar()).into(holder.imgAvatar);
        holder.tvRecipeTitle.setText(recipe.getRecipeName());
        holder.tvRecipeUserName.setText(recipe.getUser().getFullName());
        holder.tvRecipeCreateDate.setText(recipe.getDate(
                context.getText(R.string.recipe_get_date_minute_ago),
                context.getText(R.string.recipe_get_date_minutes_ago),
                context.getText(R.string.recipe_get_date_hour_ago),
                context.getText(R.string.recipe_get_date_hours_ago),
                context.getText(R.string.recipe_get_date_yesterday)
        ));
        holder.layoutRecipePreview.setOnClickListener(view -> {
            context.viewRecipeDetail(recipe.getRecipeId());
        });
        holder.btnUnlike.setOnClickListener(view -> {
            context.unlikeRecipe(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView layoutRecipePreview;
        ImageView imgRecipePreview, imgAvatar;
        TextView tvRecipeTitle, tvRecipeUserName, tvRecipeCreateDate;
        ImageButton btnUnlike;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRecipePreview = itemView.findViewById(R.id.layoutRecipePreview);
            imgRecipePreview = itemView.findViewById(R.id.imgRecipePreview);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeUserName = itemView.findViewById(R.id.tvRecipeUserName);
            tvRecipeCreateDate = itemView.findViewById(R.id.tvRecipeCreateDate);
            btnUnlike = itemView.findViewById(R.id.btnUnlike);
        }
    }
}

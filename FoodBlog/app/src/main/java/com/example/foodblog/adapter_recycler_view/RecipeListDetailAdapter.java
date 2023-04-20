package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.RecipeListDetailActivity;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.fragment.MyFavoriteFragment;
import com.example.foodblog.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeListDetailAdapter extends RecyclerView.Adapter<RecipeListDetailAdapter.MyViewHolder> {

    private RecipeListDetailActivity context;
    private ArrayList<Recipe> recipes;

    public RecipeListDetailAdapter(RecipeListDetailActivity context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeListDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListDetailAdapter.MyViewHolder holder, int position) {
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
        holder.btnRemoveBookmark.setOnClickListener(view -> {
            context.removeBookmark(position);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipePreview, imgAvatar;
        TextView tvRecipeTitle, tvRecipeUserName, tvRecipeCreateDate;
        ImageButton btnRemoveBookmark;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipePreview = itemView.findViewById(R.id.imgRecipePreview);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeUserName = itemView.findViewById(R.id.tvRecipeUserName);
            tvRecipeCreateDate = itemView.findViewById(R.id.tvRecipeCreateDate);
            btnRemoveBookmark = itemView.findViewById(R.id.btnRemoveBookmark);
        }
    }
}

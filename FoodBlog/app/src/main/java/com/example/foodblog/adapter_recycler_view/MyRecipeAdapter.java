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
import com.example.foodblog.fragment.MyRecipeFragment;
import com.example.foodblog.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyRecipeAdapter extends RecyclerView.Adapter<MyRecipeAdapter.MyViewHolder> {

    private MyRecipeFragment context;
    private ArrayList<Recipe> recipes;

    public MyRecipeAdapter(MyRecipeFragment context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public MyRecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecipeAdapter.MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Picasso.get().load(ApiUrl.IMAGE_URL + recipe.getImage()).into(holder.imgPreview);
        holder.tvTitle.setText(recipe.getRecipeName());

        holder.tvCreateDate.setText(recipe.getDate(
                context.getText(R.string.recipe_get_date_minute_ago),
                context.getText(R.string.recipe_get_date_minutes_ago),
                context.getText(R.string.recipe_get_date_hour_ago),
                context.getText(R.string.recipe_get_date_hours_ago),
                context.getText(R.string.recipe_get_date_yesterday)
        ));
        holder.tvNumOfLike.setText(String.valueOf(recipe.getNumberOfLikes()));
        holder.imgPrivatePublic.setImageResource(recipe.getStatus().equals(Recipe.STATUS_PRIVATE) ? R.drawable.ic_lock : R.drawable.baseline_public_24);
        holder.layoutRecipe.setOnClickListener(view -> {
            context.viewRecipeDetail(recipe.getRecipeId());
        });
        holder.btnMore.setOnClickListener(view -> {
            context.showPopupMore(holder.btnMore, recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView layoutRecipe;
        TextView tvTitle, tvCreateDate, tvNumOfLike;
        ImageView imgPreview, imgPrivatePublic;
        ImageButton btnMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRecipe = itemView.findViewById(R.id.layoutRecipePreview);
            imgPreview = itemView.findViewById(R.id.imgRecipePreview);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvCreateDate = itemView.findViewById(R.id.tvRecipeCreateDate);
            tvNumOfLike = itemView.findViewById(R.id.tvNumOfLike);
            imgPrivatePublic = itemView.findViewById(R.id.imgPrivatePublic);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}

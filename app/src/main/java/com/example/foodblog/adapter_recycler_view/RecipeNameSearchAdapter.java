package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.fragment.SearchFragment;
import com.example.foodblog.model.Recipe;

import java.util.ArrayList;

public class RecipeNameSearchAdapter extends RecyclerView.Adapter<RecipeNameSearchAdapter.MyViewHolder> {

    private SearchFragment context;
    private ArrayList<Recipe> recipes;

    public RecipeNameSearchAdapter(SearchFragment context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeNameSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_name_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeNameSearchAdapter.MyViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvRecipeName.setText(recipe.getRecipeName());
        holder.tvRecipeName.setOnClickListener(view -> {
            context.searchRecipeByName(recipe.getRecipeName());
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
        }
    }
}

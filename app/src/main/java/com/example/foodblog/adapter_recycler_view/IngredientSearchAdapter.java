package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.RecipeManagerInterface;
import com.example.foodblog.model.Ingredient;

import java.util.ArrayList;

public class IngredientSearchAdapter extends RecyclerView.Adapter<IngredientSearchAdapter.MyViewHolder> {

    private RecipeManagerInterface context;
    private ArrayList<Ingredient> ingredients;

    public IngredientSearchAdapter(RecipeManagerInterface context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientSearchAdapter.MyViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.tvIngredientName.setText(ingredient.getName());
        holder.tvIngredientName.setOnClickListener(view -> {
            context.setSelectIngredient(position);
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
        }
    }
}

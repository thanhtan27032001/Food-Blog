package com.example.foodblog.adapter_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.model.Ingredient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class IngredientSeasonAdapter extends RecyclerView.Adapter<IngredientSeasonAdapter.MyViewHolder> {
    private HomeFragment context;
    private ArrayList<Ingredient> ingredients;
    private int selectedPosition = 0;

    public IngredientSeasonAdapter(HomeFragment context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientSeasonAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientSeasonAdapter.MyViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.tvIngredientName.setText(ingredient.getName());
        holder.cardIngredient.setOnClickListener(view -> {
            if (position != selectedPosition){
                // Cập nhật recyclerview công thức theo nguyên liệu
                context.setRvRecipeIngredientData(ingredient.getName());
                // Cập nhật giao diện
                notifyItemChanged(selectedPosition);
                notifyItemChanged(position);
                selectedPosition = holder.getLayoutPosition();
            }
        });
        holder.imgIngredient.setVisibility(View.VISIBLE);
        holder.imgIngredientChecked.setVisibility(View.INVISIBLE);

        if (position == selectedPosition){
            holder.imgIngredient.setVisibility(View.INVISIBLE);
            holder.imgIngredientChecked.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgIngredient.setVisibility(View.VISIBLE);
            holder.imgIngredientChecked.setVisibility(View.INVISIBLE);
            Picasso.get().load(ApiUrl.IMAGE_URL + ingredient.getImage()).into(holder.imgIngredient);
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardIngredient;
        ImageView imgIngredient;
        ImageView imgIngredientChecked;
        TextView tvIngredientName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIngredient = itemView.findViewById(R.id.cardIngredient);
            imgIngredient = itemView.findViewById(R.id.imgIngredient);
            imgIngredientChecked = itemView.findViewById(R.id.imgIngredientChecked);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
        }
    }
}

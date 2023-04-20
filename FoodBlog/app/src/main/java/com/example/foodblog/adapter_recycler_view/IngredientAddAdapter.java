package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.RecipeManagerInterface;
import com.example.foodblog.model.IngredientDetail;

import java.util.ArrayList;

public class IngredientAddAdapter extends RecyclerView.Adapter<IngredientAddAdapter.MyViewHolder> {

    RecipeManagerInterface context;
    ArrayList<IngredientDetail> ingredients;

    public IngredientAddAdapter(RecipeManagerInterface context, ArrayList<IngredientDetail> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientAddAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredien_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAddAdapter.MyViewHolder holder, int position) {
        IngredientDetail ingredientDetail = ingredients.get(position);
        holder.edtIngredient.setText(ingredientDetail.getName());
        holder.edtIngredientAmount.setText(ingredientDetail.getAmount());
        holder.edtIngredient.setOnClickListener(view -> context.showFrameEditIngredient(position));
        holder.edtIngredientAmount.setOnClickListener(view -> context.showFrameEditIngredient(position));
        holder.btnDeleteIngredient.setOnClickListener(view -> {
            if (ingredients.size() > 1){
                deleteIngredient(position);
            }
            else {
                context.toastDeleteIngredient();
            }
        });
    }

    private void deleteIngredient(int position){
        ingredients.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView edtIngredient, edtIngredientAmount;
        ImageButton btnDeleteIngredient;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            edtIngredient = itemView.findViewById(R.id.edtIngredient);
            edtIngredientAmount = itemView.findViewById(R.id.edtIngredientAmount);
            btnDeleteIngredient = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}

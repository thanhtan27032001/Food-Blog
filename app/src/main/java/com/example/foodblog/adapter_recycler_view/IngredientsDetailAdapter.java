package com.example.foodblog.adapter_recycler_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.model.IngredientDetail;

import java.util.ArrayList;

public class IngredientsDetailAdapter extends RecyclerView.Adapter<IngredientsDetailAdapter.MyViewHolder> {

    Context context;
    ArrayList<IngredientDetail> ingredientDetails;

    public IngredientsDetailAdapter(Context context, ArrayList<IngredientDetail> ingredientDetails) {
        this.context = context;
        this.ingredientDetails = ingredientDetails;
    }

    @NonNull
    @Override
    public IngredientsDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsDetailAdapter.MyViewHolder holder, int position) {
        if (position % 2 != 0){
            holder.tvDetail.setBackgroundColor(Color.WHITE);
        }
        else {
            holder.tvDetail.setBackground(AppCompatResources.getDrawable(context, R.drawable.background_ingredient_detail_item));
        }
        holder.tvDetail.setText(ingredientDetails.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return ingredientDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvDetail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDetail = itemView.findViewById(R.id.tvIngredientDetail);
        }
    }
}

package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.model.RecipeListDetail;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> {
    private ArrayList<RecipeListDetail> recipeListDetails;

    public BookmarkAdapter(ArrayList<RecipeListDetail> recipeListDetails) {
        this.recipeListDetails = recipeListDetails;
    }

    @NonNull
    @Override
    public BookmarkAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.MyViewHolder holder, int position) {
        RecipeListDetail recipeListDetail = recipeListDetails.get(position);
        holder.cbRecipeList.setText(recipeListDetail.getName());
        holder.cbRecipeList.setChecked(recipeListDetail.isBookmarked());
        holder.cbRecipeList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                recipeListDetail.setBookmarked(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeListDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbRecipeList;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cbRecipeList = itemView.findViewById(R.id.cbRecipeList);
        }
    }
}

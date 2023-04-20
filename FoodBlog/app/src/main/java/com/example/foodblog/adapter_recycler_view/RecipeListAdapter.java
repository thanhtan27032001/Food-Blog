package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.fragment.AccountFragment;
import com.example.foodblog.fragment.MyLibraryFragment;
import com.example.foodblog.model.RecipeList;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.MyViewHolder> {
    private MyLibraryFragment context;
    private ArrayList<RecipeList> recipeLists;

    public RecipeListAdapter(MyLibraryFragment context, ArrayList<RecipeList> recipeLists) {
        this.context = context;
        this.recipeLists = recipeLists;
    }

    @NonNull
    @Override
    public RecipeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.MyViewHolder holder, int position) {
        RecipeList recipeList = recipeLists.get(position);
        holder.tvRecipeListName.setText(recipeList.getName());
        Picasso.get().load(ApiUrl.IMAGE_URL + recipeList.getImage()).into(holder.imgRecipeList);

        holder.imgRecipeList.setOnClickListener(view -> {
            context.viewRecipeListDetail(recipeList);
        });

        holder.btnMore.setOnClickListener(view -> {
            context.showMenuRecipeListManager(recipeList, holder.btnMore);
        });
    }

    @Override
    public int getItemCount() {
        return recipeLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgRecipeList;
        ImageButton btnMore;
        TextView tvRecipeListName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipeList = itemView.findViewById(R.id.imgRecipeList);
            tvRecipeListName = itemView.findViewById(R.id.tvRecipeListName);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}

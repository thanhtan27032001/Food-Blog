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
import com.example.foodblog.model.RecipeStep;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.MyViewHolder> {

    Context context;
    ArrayList<RecipeStep> recipeSteps;

    public RecipeStepAdapter(Context context, ArrayList<RecipeStep> recipeSteps) {
        this.context = context;
        this.recipeSteps = recipeSteps;
    }

    @NonNull
    @Override
    public RecipeStepAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepAdapter.MyViewHolder holder, int position) {
        RecipeStep recipeStep = recipeSteps.get(position);
        holder.tvId.setText(context.getText(R.string.step).toString().concat(" ").concat(String.valueOf(position+1)));
        holder.tvDescription.setText(recipeStep.getDescription());
        if (recipeStep.getImage() != null && !recipeStep.getImage().equals("")){
            Picasso.get().load(ApiUrl.IMAGE_URL + recipeStep.getImage()).into(holder.imgDemo);
        }
        else {
            holder.cvImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvId, tvDescription;
        CardView cvImage;
        ImageView imgDemo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvStepIndex);
            tvDescription = itemView.findViewById(R.id.tvStepDescription);
            cvImage = itemView.findViewById(R.id.cvStepImage);
            imgDemo = itemView.findViewById(R.id.imgStep);
        }
    }
}

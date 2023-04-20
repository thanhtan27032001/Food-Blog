package com.example.foodblog.adapter_recycler_view;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.RecipeManagerInterface;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.model.RecipeStep;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StepAddAdapter extends RecyclerView.Adapter<StepAddAdapter.MyViewHolder> {

    private RecipeManagerInterface context;
    private ArrayList<RecipeStep> recipeSteps;

    public StepAddAdapter(RecipeManagerInterface context, ArrayList<RecipeStep> recipeSteps) {
        this.context = context;
        this.recipeSteps = recipeSteps;
    }

    @NonNull
    @Override
    public StepAddAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StepAddAdapter.MyViewHolder holder, int position) {
        RecipeStep recipeStep = recipeSteps.get(position);
        holder.tvStepIndex.setText(context.getMyText(R.string.step).concat(" " + recipeStep.getStepIndex()));
        holder.edtStepDescription.setText(recipeStep.getDescription());
        if (recipeStep.getImageUri() != null){
            holder.imgStep.setImageURI(recipeStep.getImageUri());
        }
        else if (recipeStep.getImage() != null && !recipeStep.getImage().equals("")){
            Picasso.get().load(ApiUrl.IMAGE_URL + recipeStep.getImage()).into(holder.imgStep);
        }
        holder.edtStepDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recipeStep.setDescription(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        holder.imgStep.setOnClickListener(view -> {
            context.showDialogChooseImage(position);
        });
        holder.btnDelete.setOnClickListener(view -> {
            if (recipeSteps.size() > 1){
                if (position > 0){
                    context.focusRvStepsEdittext(0);
                }
                else {
                    context.focusRvStepsEdittext(1);
                }
                deleteStep(recipeStep);
            }
            else {
                context.toastDeleteStep();
            }
        });
    }

    private void deleteStep(RecipeStep recipeStep){
        recipeSteps.remove(recipeStep);
        context.setStepIndex(1);
        for (RecipeStep step: recipeSteps){
            step.setStepIndex(context.getStepIndex());
            context.setStepIndex(context.getStepIndex() + 1);
        }
        notifyDataSetChanged();
        for (RecipeStep recipeStep1: recipeSteps){
            Log.e("test", recipeStep1.getStepIndex() + " - " + recipeStep1.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvStepIndex;
        ImageButton btnDelete;
        EditText edtStepDescription;
        ImageView imgStep;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStepIndex = itemView.findViewById(R.id.tvStepIndex);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            edtStepDescription = itemView.findViewById(R.id.edtStepDescription);
            imgStep = itemView.findViewById(R.id.imgStep);
        }
    }
}

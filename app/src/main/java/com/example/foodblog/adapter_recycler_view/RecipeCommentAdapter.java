package com.example.foodblog.adapter_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.model.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeCommentAdapter extends RecyclerView.Adapter<RecipeCommentAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Comment> comments;

    public RecipeCommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public RecipeCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCommentAdapter.MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        Picasso.get().load(ApiUrl.IMAGE_URL + comment.getUser().getAvatar()).into(holder.imgAvt);
        holder.tvUserName.setText(comment.getUser().getFullName());
        holder.tvCommentDate.setText("・".concat(comment.getDate(
                context.getText(R.string.recipe_get_date_minute_ago),
                context.getText(R.string.recipe_get_date_minutes_ago),
                context.getText(R.string.recipe_get_date_hour_ago),
                context.getText(R.string.recipe_get_date_hours_ago),
                context.getText(R.string.recipe_get_date_yesterday))));
        holder.tvComment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        TextView tvUserName, tvCommentDate, tvComment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.imgAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentDate = itemView.findViewById(R.id.tvCommentDate);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}

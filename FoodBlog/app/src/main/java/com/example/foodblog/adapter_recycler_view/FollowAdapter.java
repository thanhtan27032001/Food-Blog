package com.example.foodblog.adapter_recycler_view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api_instance.UserApiInstance;
import com.example.foodblog.fragment.FollowFragment;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private FollowFragment context;
    private ArrayList<User> users;

    public FollowAdapter(FollowFragment context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FollowAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.MyViewHolder holder, int position) {
        User user = users.get(position);
        Picasso.get().load(ApiUrl.IMAGE_URL + user.getAvatar()).into(holder.imgAvt);
        holder.tvUserName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        if (user.isFollowed()){
            holder.tvUnfollow.setVisibility(View.VISIBLE);
            holder.tvFollow.setVisibility(View.GONE);
        }
        else {
            holder.tvUnfollow.setVisibility(View.GONE);
            holder.tvFollow.setVisibility(View.VISIBLE);
        }
        holder.tvFollow.setOnClickListener(view -> {
//            context.changeFollowStatus(user, true, holder.tvFollow, holder.tvUnfollow);
            changeFollowStatus(user, true);
        });
        holder.tvUnfollow.setOnClickListener(view -> {
//            context.changeFollowStatus(user, false, holder.tvFollow, holder.tvUnfollow);
            changeFollowStatus(user, false);
        });
        holder.imgAvt.setOnClickListener(view -> {
            context.viewUserWall(user.getUserId());
        });
        holder.layoutUserInfo.setOnClickListener(view -> {
            context.viewUserWall(user.getUserId());
        });
    }

    public void changeFollowStatus(User user, boolean isMakeFollowed){
        Call<ResponseObject> call;
        if (isMakeFollowed){
            call = UserApiInstance.getInstance().followOtherUser(MyAuthorization.getInstance().getToken(), user.getUserId());
        }
        else {
            call = UserApiInstance.getInstance().unfollowOtherUser(MyAuthorization.getInstance().getToken(), user.getUserId());
        }
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    user.setFollowed(isMakeFollowed);
                    notifyItemChanged(users.indexOf(user));
                    if (isMakeFollowed){
//                        tvFollow.setVisibility(View.GONE);
//                        tvUnfollow.setVisibility(View.VISIBLE);
//                        followingFragment.setData();
                        Toast.makeText(context.getContext(), context.getText(R.string.success_follow), Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        tvFollow.setVisibility(View.VISIBLE);
//                        tvUnfollow.setVisibility(View.GONE);
                        Toast.makeText(context.getContext(), context.getText(R.string.success_unfollow), Toast.LENGTH_SHORT).show();
                    }
                }
                else if (response.code() == 435) {
                    Toast.makeText(context.getContext(), context.getText(R.string.error_unfollow_435), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 446) {
                    Toast.makeText(context.getContext(), context.getText(R.string.error_follow_446), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context.getContext(), context.getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(context.getContext(), context.getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvEmail, tvFollow, tvUnfollow;
        ImageView imgAvt;
        LinearLayout layoutUserInfo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvFollow = itemView.findViewById(R.id.tvFollow);
            tvUnfollow = itemView.findViewById(R.id.tvUnfollow);
            imgAvt = itemView.findViewById(R.id.imgAvatar);
            layoutUserInfo = itemView.findViewById(R.id.layoutUserInfo);
        }
    }
}

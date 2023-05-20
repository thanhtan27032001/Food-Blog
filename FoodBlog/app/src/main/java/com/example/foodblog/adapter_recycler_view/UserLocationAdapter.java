package com.example.foodblog.adapter_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.LocationActivity;
import com.example.foodblog.MyFormat;
import com.example.foodblog.R;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.model.UserLocation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserLocationAdapter extends RecyclerView.Adapter<UserLocationAdapter.MyViewHolder> {

    private LocationActivity context;
    private ArrayList<UserLocation> userLocations;

    public UserLocationAdapter(LocationActivity context, ArrayList<UserLocation> userLocations) {
        this.context = context;
        this.userLocations = userLocations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_location, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserLocation userLocation = userLocations.get(position);
        holder.tvUserName.setText(userLocation.getFullName());
        holder.tvEmail.setText(userLocation.getEmail());
        holder.tvLocation.setText(userLocation.getCurrentLocation());
        holder.tvLocationLastUpdate.setText(context.getText(R.string.tv_location_last_update).toString().concat(" ".concat(
            MyFormat.getPublicDate(
                userLocation.getLocationLastUpdated(),
                context.getText(R.string.recipe_get_date_minute_ago),
                context.getText(R.string.recipe_get_date_minutes_ago),
                context.getText(R.string.recipe_get_date_hour_ago),
                context.getText(R.string.recipe_get_date_hours_ago),
                context.getText(R.string.recipe_get_date_yesterday))
            ))
        );
        Picasso.get().load(ApiUrl.IMAGE_URL + userLocation.getAvatar()).into(holder.imgAvatar);
        holder.imgViewLocation.setOnClickListener(view -> {
            context.openGoogleMap(userLocation);
        });
    }

    @Override
    public int getItemCount() {
        return userLocations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgViewLocation;
        TextView tvUserName, tvEmail, tvLocation, tvLocationLastUpdate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgViewLocation = itemView.findViewById(R.id.imgViewLocation);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvLocationLastUpdate = itemView.findViewById(R.id.tvLocationLastUpdate);
        }
    }
}

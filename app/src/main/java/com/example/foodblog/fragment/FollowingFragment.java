package com.example.foodblog.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.adapter_recycler_view.FollowAdapter;
import com.example.foodblog.api_instance.UserApiInstance;
import com.example.foodblog.model.ResponseUsers;
import com.example.foodblog.model.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends Fragment {
    FollowFragment context;
    SwipeRefreshLayout swfFollowing;
    RecyclerView rvFollowing;
    ArrayList<User> followings = new ArrayList<>();
    FollowAdapter followAdapter;
    public FollowingFragment(FollowFragment context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_following, container, false);

        swfFollowing = contentView.findViewById(R.id.swfFollowing);

        rvFollowing = contentView.findViewById(R.id.rvFollowing);
        rvFollowing.setAdapter(followAdapter);
        rvFollowing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setEvent();
        setData();
        return contentView;
    }

    private void setEvent() {
        swfFollowing.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setData();
            }
        });
    }

    public void setData() {
        Call<ResponseUsers> call = UserApiInstance.getInstance().getAllFollowings(
                MyAuthorization.getInstance().getToken(),
                MyAuthorization.getInstance().getUser().getUserId());
        call.enqueue(new Callback<ResponseUsers>() {
            @Override
            public void onResponse(Call<ResponseUsers> call, Response<ResponseUsers> response) {
                swfFollowing.setRefreshing(false);
                if (response.isSuccessful() || response.code() == 200){
                    followings.clear();
                    followings.addAll(response.body().getUsers());
                    followAdapter = new FollowAdapter(context, followings);
                    rvFollowing.setAdapter(followAdapter);
                }
                else if (response.code() == 441){
                    Toast.makeText(getContext(), getText(R.string.error_following_441), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUsers> call, Throwable t) {
                Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                swfFollowing.setRefreshing(false);
            }
        });
    }
}
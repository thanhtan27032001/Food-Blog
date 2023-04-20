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

public class FollowerFragment extends Fragment {

    FollowFragment context;
    SwipeRefreshLayout swfFollower;
    RecyclerView rvFollowers;
    ArrayList<User> followers = new ArrayList<>();
    FollowAdapter followAdapter;

    public FollowerFragment(FollowFragment context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_follower, container, false);

        swfFollower = contentView.findViewById(R.id.swfFollower);

        rvFollowers = contentView.findViewById(R.id.rvFollowers);
        rvFollowers.setAdapter(followAdapter);
        rvFollowers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setEvent();
        setData();
        return contentView;
    }

    private void setEvent() {
        swfFollower.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setData();
            }
        });
    }

    private void setData() {
        Call<ResponseUsers> call = UserApiInstance.getInstance().getAllFollowers(
                MyAuthorization.getInstance().getToken(),
                MyAuthorization.getInstance().getUser().getUserId());
        call.enqueue(new Callback<ResponseUsers>() {
            @Override
            public void onResponse(Call<ResponseUsers> call, Response<ResponseUsers> response) {
                swfFollower.setRefreshing(false);
                if (response.isSuccessful() || response.code() == 200){
                    followers.clear();
                    followers.addAll(response.body().getUsers());
                    followAdapter = new FollowAdapter(context, followers);
                    rvFollowers.setAdapter(followAdapter);
                }
                else if (response.code() == 442) {
                    Toast.makeText(getContext(), getText(R.string.error_follower_442), Toast.LENGTH_SHORT).show();
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
                swfFollower.setRefreshing(false);
            }
        });
    }
}
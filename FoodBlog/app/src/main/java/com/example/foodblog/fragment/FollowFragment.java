package com.example.foodblog.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.UserWallActivity;
import com.example.foodblog.adapter_recycler_view.FollowAdapter;
import com.example.foodblog.api_instance.UserApiInstance;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseUsersSearch;
import com.example.foodblog.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowFragment extends Fragment {

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    if (followingFragment == null){
                        followingFragment = new FollowingFragment(FollowFragment.this);
                    }
                    return followingFragment;
                case 1:
                    return new FollowerFragment(FollowFragment.this);
            }
            if (followingFragment == null){
                followingFragment = new FollowingFragment(FollowFragment.this);
            }
            return followingFragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    FollowingFragment followingFragment;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabLayoutMediator tabLayoutMediator;
    private FragmentStateAdapter pagerAdapter;


    private LinearLayout layoutMain, layoutSearch;
    private TextView tvSearchBar;
    private SearchView searchViewRecipe;
    private ImageButton btnBack;
    private RecyclerView rvUser;
    private FollowAdapter userAdapter;
    private ArrayList<User> users = new ArrayList<>();

    public FollowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_follow, container, false);

        pagerAdapter = new ScreenSlidePagerAdapter(getActivity());
        viewPager2 = contentView.findViewById(R.id.pagerContent);
        tabLayout = contentView.findViewById(R.id.tabLayoutContent);

        viewPager2.setAdapter(pagerAdapter);
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText(getText(R.string.follow_tab_following));
                        break;
                    case 1:
                        tab.setText(getText(R.string.follow_tab_follower));
                }
            }
        });
        tabLayoutMediator.attach();

        layoutMain = contentView.findViewById(R.id.layoutMain);
        layoutSearch = contentView.findViewById(R.id.layoutSearch);
        tvSearchBar = contentView.findViewById(R.id.tvSearchBar);
        searchViewRecipe = contentView.findViewById(R.id.searchViewRecipe);
        btnBack = contentView.findViewById(R.id.btnBack);

        rvUser = contentView.findViewById(R.id.rvUser);
        rvUser.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        setEvent();

        return contentView;
    }

    private void setEvent() {
        tvSearchBar.setOnClickListener(view -> {
            layoutMain.setVisibility(View.GONE);
            layoutSearch.setVisibility(View.VISIBLE);
            searchViewRecipe.requestFocus();
        });
        btnBack.setOnClickListener(view -> {
            searchViewRecipe.setQuery("", false);
            layoutMain.setVisibility(View.VISIBLE);
            layoutSearch.setVisibility(View.GONE);
        });
        searchViewRecipe.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.contains("@")){
                    searchUserByEmail(newText);
                }
                else {
                    searchUserByName(newText);
                }
                return false;
            }
        });
    }

    public void changeFollowStatus(User user, ArrayList<User> users, boolean isMakeFollowed, FollowAdapter adapter){
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
                    adapter.notifyItemChanged(users.indexOf(user));
//                    if (isMakeFollowed){
//                        tvFollow.setVisibility(View.GONE);
//                        tvUnfollow.setVisibility(View.VISIBLE);
//                        followingFragment.setData();
//                        Toast.makeText(getContext(), getText(R.string.success_follow), Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        tvFollow.setVisibility(View.VISIBLE);
//                        tvUnfollow.setVisibility(View.GONE);
//                        Toast.makeText(getContext(), getText(R.string.success_unfollow), Toast.LENGTH_SHORT).show();
//                    }
                }
                else if (response.code() == 435) {
                    Toast.makeText(getContext(), getText(R.string.error_unfollow_435), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 446) {
                    Toast.makeText(getContext(), getText(R.string.error_follow_446), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void viewUserWall(int userId){
        Intent intent = new Intent(getContext(), UserWallActivity.class);
        intent.putExtra(UserWallActivity.TAG_USER_ID, userId);
        startActivity(intent);
    }

    private void searchUserByName(String name) {
        Call<ResponseUsersSearch> call = UserApiInstance.getInstance().searchUserByName(
                MyAuthorization.getInstance().getToken(),
                name
        );
        call.enqueue(new Callback<ResponseUsersSearch>() {
            @Override
            public void onResponse(Call<ResponseUsersSearch> call, Response<ResponseUsersSearch> response) {
                if (response.isSuccessful() || response.code() == 200){
                    users.clear();
                    users.addAll(response.body().getData());
                    userAdapter = new FollowAdapter(FollowFragment.this, users);
                    rvUser.setAdapter(userAdapter);
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
            public void onFailure(Call<ResponseUsersSearch> call, Throwable t) {
                Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void searchUserByEmail(String email) {
        Call<ResponseUsersSearch> call = UserApiInstance.getInstance().searchUserByEmail(
                MyAuthorization.getInstance().getToken(),
                email
        );
        call.enqueue(new Callback<ResponseUsersSearch>() {
            @Override
            public void onResponse(Call<ResponseUsersSearch> call, Response<ResponseUsersSearch> response) {
                if (response.isSuccessful() || response.code() == 200){
                    users.clear();
                    users.addAll(response.body().getData());
                    userAdapter = new FollowAdapter(FollowFragment.this, users);
                    rvUser.setAdapter(userAdapter);
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
            public void onFailure(Call<ResponseUsersSearch> call, Throwable t) {
                Toast.makeText(getContext(), getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
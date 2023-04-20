package com.example.foodblog.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodblog.ChangePasswordActivity;
import com.example.foodblog.LoginActivity;
import com.example.foodblog.MyAuthorization;
import com.example.foodblog.R;
import com.example.foodblog.UpdateUserActivity;
import com.example.foodblog.UserWallActivity;
import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.model.SearchHistories;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {
    public static final int CODE_UPDATE_DATA = 2703;
    private static final int NUM_PAGES = 2;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabLayoutMediator tabLayoutMediator;
    ImageButton btnMore;
    ImageView imgAvatar;
    TextView tvUserName, tvEmail;
    private FragmentStateAdapter pagerAdapter;

    public AccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pagerAdapter = new ScreenSlidePagerAdapter(getActivity());
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        viewPager2 = view.findViewById(R.id.pagerContent);
        tabLayout = view.findViewById(R.id.tabLayoutContent);

        viewPager2.setAdapter(pagerAdapter);
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText(getText(R.string.account_tab_0));
                        break;
                    case 1:
                        tab.setText(R.string.account_tab_1);
                }
            }
        });
        tabLayoutMediator.attach();

        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnMore = view.findViewById(R.id.btnMore);

        setEvent();
        setData();
        return view;
    }

    private void setData() {
        Picasso.get().load(ApiUrl.IMAGE_URL + MyAuthorization.getInstance().getUser().getAvatar()).into(imgAvatar);
        tvUserName.setText(MyAuthorization.getInstance().getUser().getFullName());
        tvEmail.setText(MyAuthorization.getInstance().getUser().getEmail());
    }

    private void setEvent() {
        btnMore.setOnClickListener(view -> {
            showMenuAccountManager();
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new MyLibraryFragment();
                case 1:
                    return new MyFavoriteFragment();
            }
            return new MyLibraryFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    private void showMenuAccountManager() {
        PopupMenu popupMenu = new PopupMenu(getContext(), btnMore);
        popupMenu.getMenuInflater().inflate(R.menu.menu_account_manager, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.optionViewMyWall:
                        Intent intent = new Intent(getContext(), UserWallActivity.class);
                        intent.putExtra(UserWallActivity.TAG_USER_ID, MyAuthorization.getInstance().getUser().getUserId());
                        startActivity(intent);
                        break;
                    case R.id.optionUpdateUser:
                        startActivityForResult(new Intent(getContext(), UpdateUserActivity.class), CODE_UPDATE_DATA);
                        break;
                    case R.id.optionUpdatePassword:
                        startActivity(new Intent(getContext(), ChangePasswordActivity.class));
                        break;
                    case R.id.optionLogout:
                        logout();
                        break;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void logout() {
        // clear login info
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit()
                .remove(LoginActivity.TAG_REMEMBER)
                .remove(LoginActivity.TAG_USERNAME)
                .remove(LoginActivity.TAG_PASSWORD)
                .apply();
        // clear search histories
        SearchHistories.getInstance(getContext()).clearHistory(getContext());
        //
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODE_UPDATE_DATA){
            if (resultCode == UpdateUserActivity.RESULT_OK){
                setData();
            }
        }
    }
}
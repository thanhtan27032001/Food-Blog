package com.example.foodblog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.foodblog.fragment.AccountFragment;
import com.example.foodblog.fragment.MyRecipeFragment;
import com.example.foodblog.fragment.FollowFragment;
import com.example.foodblog.fragment.HomeFragment;
import com.example.foodblog.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_FRAGMENT_HOME = "home";
    private static final String TAG_FRAGMENT_SEARCH = "search";
    private static final String TAG_FRAGMENT_ADD = "add";
    private static final String TAG_FRAGMENT_FAV = "favorite";
    private static final String TAG_FRAGMENT_ACC = "account";

    private MyRecipeFragment myRecipeFragment;
    private AccountFragment accountFragment;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();
    }

    private void setEvent() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_HOME);
                    if (homeFragment == null){
                        homeFragment = new HomeFragment(MainActivity.this);
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, homeFragment, TAG_FRAGMENT_HOME)
                            .addToBackStack(TAG_FRAGMENT_HOME)
                            .commit();
                    break;
                case R.id.search:
                    SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SEARCH);
                    if (searchFragment == null){
                        searchFragment = new SearchFragment();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, searchFragment, TAG_FRAGMENT_SEARCH)
                            .addToBackStack(TAG_FRAGMENT_SEARCH)
                            .commit();
                    break;
                case R.id.manage:
                    if (myRecipeFragment == null){
                        myRecipeFragment = new MyRecipeFragment();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, myRecipeFragment)
                            .commit();
//                    MyRecipeFragment myRecipeFragment = (MyRecipeFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_ADD);
//                    if (myRecipeFragment == null){
//                        myRecipeFragment = new MyRecipeFragment();
//                    }
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.fragmentContainer, myRecipeFragment, TAG_FRAGMENT_ADD)
//                            .commit();
                    break;
                case R.id.favorite:
                    FollowFragment followFragment = (FollowFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_FAV);
                    if (followFragment == null){
                        followFragment = new FollowFragment();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, followFragment, TAG_FRAGMENT_FAV)
                            .commit();
                    break;
                case R.id.account:
                    if (accountFragment == null){
                        accountFragment = new AccountFragment();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, accountFragment)
                            .commit();
//                    AccountFragment accountFragment = (AccountFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_ACC);
//                    if (accountFragment == null){
//                        accountFragment = new AccountFragment();
//                    }
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.fragmentContainer, accountFragment, TAG_FRAGMENT_ACC)
//                            .commit();
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void setControl() {
        // Setup bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
    }

    public void goToSearchFragment(){
        bottomNavigationView.setSelectedItemId(R.id.search);
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getText(R.string.title_close_app))
                .setMessage(getText(R.string.message_close_app))
                .setPositiveButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(getText(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        alertDialog.show();
    }
}
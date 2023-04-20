package com.example.foodblog.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.MyFileReader;
import com.example.foodblog.MyPermission;
import com.example.foodblog.R;
import com.example.foodblog.RecipeListDetailActivity;
import com.example.foodblog.adapter_recycler_view.RecipeListAdapter;
import com.example.foodblog.api_instance.RecipeListApiInstance;
import com.example.foodblog.model.RecipeList;
import com.example.foodblog.model.RecipeListInfo;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipeList;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLibraryFragment extends Fragment {
    private static final int REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY = 1;
    //    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

    File recipeListImageFile;
    Uri recipeListImageUri;
    SwipeRefreshLayout swfRecipeList;
    ImageButton btnAddRecipeList;
    ImageView imgChooseImage;
    RecyclerView rvRecipeList;
    ArrayList<RecipeList> recipeLists;
    RecipeListAdapter recipeListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRecipeListData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_library, container, false);

        swfRecipeList = view.findViewById(R.id.swfRecipeList);

        btnAddRecipeList = view.findViewById(R.id.btnAddRecipeList);

        rvRecipeList = view.findViewById(R.id.rvRecipeList);
        rvRecipeList.setAdapter(recipeListAdapter);
        rvRecipeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setEvent();
        return view;
    }
    private void setRecipeListData(){
        Call<ResponseRecipeList> call = RecipeListApiInstance.getInstance().getRecipeList(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseRecipeList>() {
            @Override
            public void onResponse(Call<ResponseRecipeList> call, Response<ResponseRecipeList> response) {
                swfRecipeList.setRefreshing(false);
                if (response.isSuccessful() || response.code() == 200){
                    if (response.body() != null){
                        recipeLists = response.body().getData();
                        recipeListAdapter = new RecipeListAdapter(MyLibraryFragment.this, recipeLists);
                        rvRecipeList.setAdapter(recipeListAdapter);
                    }
                    else {
                        Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                        try {
                            Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRecipeList> call, Throwable t) {
                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                swfRecipeList.setRefreshing(false);
            }
        });
    }
    private void setEvent() {
        btnAddRecipeList.setOnClickListener(view -> {
            showDialogAddRecipeList();
        });
        swfRecipeList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecipeListData();
            }
        });
    }

    private void showDialogAddRecipeList() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_recipe_list);
        EditText edtRecipeListName = dialog.findViewById(R.id.edtRecipeListName);
        imgChooseImage = dialog.findViewById(R.id.imgChooseImage);
        imgChooseImage.setOnClickListener(view -> {
            if (MyPermission.checkReadFilePermission(getActivity())){
                chooseImage();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyPermission.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        });
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(view -> {
            if (recipeListImageFile != null){
                addRecipeList(edtRecipeListName.getText().toString());
                dialog.dismiss();
            }
            else {
                Toast.makeText(getContext(), R.string.error_file_not_found, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void addRecipeList(String recipeListName){
        RequestBody requestBody = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(recipeListImageUri)), recipeListImageFile);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("recipeList", recipeListImageFile.getName(), requestBody);

        RequestBody name = RequestBody.create(okhttp3.MultipartBody.FORM, recipeListName);

//                RecipeList recipeList = new RecipeList(edtRecipeListName.getText().toString(), multipartBody);


        Call<ResponseObject> call = RecipeListApiInstance.getInstance().
                addRecipeList(MyAuthorization.getInstance().getToken(), name, multipartBody);
//                Call<ResponseObject> call = RecipeListApiInstance.getInstance().
//                        addRecipeList(Authorization.getInstance().getToken(), recipeList);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    setRecipeListData();
                    Toast.makeText(getContext(), R.string.success_add_recipe_list, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 418){
                    Toast.makeText(getContext(), R.string.error_missing_field, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 440){
                    Toast.makeText(getContext(), R.string.error_file_not_found, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
//                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MyPermission.REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImage();
                }
                else {
                    Toast.makeText(getContext(), R.string.warning_grant_permission_access_storage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY:
                if (resultCode == getActivity().RESULT_OK){
                    Uri selectedImageUri = data.getData();
                    recipeListImageUri = selectedImageUri;
                    recipeListImageFile = new File(MyFileReader.getRealPathFromURI(getContext(), selectedImageUri));
                    imgChooseImage.setImageURI(selectedImageUri);
                }
                break;
        }
    }
    public void viewRecipeListDetail(RecipeList recipeList) {
        Intent intent = new Intent(getContext(), RecipeListDetailActivity.class);
        intent.putExtra(RecipeListDetailActivity.TAG_RECIPE_LIST, recipeList);
        startActivity(intent);
    }

    public void showMenuRecipeListManager(RecipeList recipeList, ImageButton btnRecipeListOpt) {
        PopupMenu popupMenu = new PopupMenu(getContext(), btnRecipeListOpt);
        popupMenu.getMenuInflater().inflate(R.menu.menu_recipe_list_manager, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.optionChangeRecipeListName:
                        showDialogChangeRecipeListName(recipeList);
                        break;
                    case R.id.optionDeleteRecipeList:
                        deleteRecipeList(recipeList);
                        break;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void deleteRecipeList(RecipeList recipeList) {
        Log.e("test", recipeLists.indexOf(recipeList) + "");
        Call<ResponseObject> call = RecipeListApiInstance.getInstance().deleteRecipeList(
                MyAuthorization.getInstance().getToken(),
                recipeList.getRecipeListId()
        );
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipeListAdapter.notifyItemRemoved(recipeLists.indexOf(recipeList));
                    recipeLists.remove(recipeList);
                    Toast.makeText(getContext(), R.string.success_delete_recipe_list, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 433){
                    Toast.makeText(getContext(), R.string.error_delete_recipe_list_433, Toast.LENGTH_SHORT).show();
                }
                else {
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
                t.printStackTrace();
            }
        });
    }

    private void showDialogChangeRecipeListName(RecipeList recipeList) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_recipe_list_name);
        EditText edtRecipeListName = dialog.findViewById(R.id.edtRecipeListName);
        TextView btnSubmit = dialog.findViewById(R.id.btnSubmit);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);

        btnSubmit.setOnClickListener(view -> {
            if (!edtRecipeListName.getText().toString().equals("")){
                changeRecipeListName(recipeList, edtRecipeListName.getText().toString());
                dialog.dismiss();
            }
            else {
                edtRecipeListName.setError(getText(R.string.error_miss_recipe_list_name));
            }
        });
        btnCancel.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void changeRecipeListName(RecipeList recipeList, String newName) {
        RecipeListInfo info = new RecipeListInfo(newName);
        Call<ResponseObject> call = RecipeListApiInstance.getInstance().changeRecipeListName(
                MyAuthorization.getInstance().getToken(),
                recipeList.getRecipeListId(),
                info
        );
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    recipeList.setName(newName);
                    recipeListAdapter.notifyItemChanged(recipeLists.indexOf(recipeList));
                }
                else if (response.code() == 433){
                    Toast.makeText(getContext(), R.string.error_delete_recipe_list_433, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 440){
                    Toast.makeText(getContext(), R.string.error_file_not_found, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
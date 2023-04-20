package com.example.foodblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api_instance.UserApiInstance;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateUserActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY = 1;

    private Uri avatarUri;
    private File avatarFile;
    private TextView tvUserName, tvEmail;
    private ImageButton btnBack, btnSave;
    private EditText edtUserName, edtEmail, edtBirthdate, edtLocation, edtIntro;
    private ImageView imgAvt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        setControl();
        setEvent();
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> finish());
        btnSave.setOnClickListener(view -> {
            if (isInfoValidated()){
                updateMyInfo();
            }
        });
        imgAvt.setOnClickListener(view -> {
            if (MyPermission.checkReadFilePermission(this)){
                chooseImage();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyPermission.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    private void setControl() {
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        edtUserName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtBirthdate = findViewById(R.id.edtBirthdate);
        edtLocation = findViewById(R.id.edtLocation);
        edtIntro = findViewById(R.id.edtIntro);
        imgAvt = findViewById(R.id.imgAvatar);
        setData();
    }

    private void setData() {
        User me = MyAuthorization.getInstance().getUser();
        tvUserName.setText(me.getFullName());
        tvEmail.setText(me.getEmail());
        edtUserName.setText(me.getFullName());
        edtEmail.setText(me.getEmail());
        edtBirthdate.setText(me.getDateOfBirth());
        edtLocation.setText(me.getAddress());
        edtIntro.setText(me.getIntroduce());
        Picasso.get().load(ApiUrl.IMAGE_URL + me.getAvatar()).into(imgAvt);
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
                    Toast.makeText(this, R.string.warning_grant_permission_access_storage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY:
                if (resultCode == RESULT_OK){
                    Uri selectedImageUri = data.getData();
                    avatarUri = selectedImageUri;
                    avatarFile = new File(MyFileReader.getRealPathFromURI(this, selectedImageUri));
                    imgAvt.setImageURI(selectedImageUri);
                }
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY);
    }

    private boolean isInfoValidated() {
        if (edtUserName.getText().toString().equals("")){
            edtUserName.setError(getText(R.string.error_miss_name));
            edtUserName.requestFocus();
            return false;
        }
        if (edtEmail.getText().toString().equals("")){
            edtEmail.setError(getText(R.string.error_miss_email));
            edtEmail.requestFocus();
            return false;
        }
        try {
            String birthdate = edtBirthdate.getText().toString();
            Date.parse(birthdate);
        }
        catch (Exception e){
            edtBirthdate.setError(getText(R.string.error_birthdate_wrong_format));
            return false;
        }
        if (edtLocation.getText().toString().equals("")){
            edtLocation.setError(getText(R.string.error_miss_location));
            edtLocation.requestFocus();
            return false;
        }
        return true;
    }

    private void updateMyInfo() {
        try {
            RequestBody name = RequestBody.create(okhttp3.MultipartBody.FORM, edtUserName.getText().toString());
            RequestBody birthDate = RequestBody.create(okhttp3.MultipartBody.FORM, edtBirthdate.getText().toString());
            RequestBody location = RequestBody.create(okhttp3.MultipartBody.FORM, edtLocation.getText().toString());
            RequestBody email = RequestBody.create(okhttp3.MultipartBody.FORM, edtEmail.getText().toString());
            RequestBody intro = RequestBody.create(okhttp3.MultipartBody.FORM, edtIntro.getText().toString());

            MultipartBody.Part avatar = null;
            if (avatarUri != null){
                RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(avatarUri)), avatarFile);
                avatar = MultipartBody.Part.createFormData("user", avatarFile.getName(), requestBody);
            }

            Call<ResponseObject> call = UserApiInstance.getInstance().updateMyInfo(
                    MyAuthorization.getInstance().getToken(),
                    name,
                    birthDate,
                    location,
                    email,
                    intro,
                    avatar
            );
            call.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                    if (response.isSuccessful() || response.code() == 200){
                        Toast.makeText(UpdateUserActivity.this, R.string.success_update_user, Toast.LENGTH_SHORT).show();
                        MyAuthorization.getInstance().getUser().setFullName(edtUserName.getText().toString());
                        MyAuthorization.getInstance().getUser().setEmail(edtEmail.getText().toString());
                        MyAuthorization.getInstance().getUser().setDateOfBirth(edtBirthdate.getText().toString());
                        MyAuthorization.getInstance().getUser().setAddress(edtLocation.getText().toString());
                        MyAuthorization.getInstance().getUser().setIntroduce(edtIntro.getText().toString());
                        setResult(RESULT_OK);
                        UpdateUserActivity.this.finish();
                    }
                    else if (response.code() == 418){
                        Toast.makeText(UpdateUserActivity.this, R.string.error_missing_field, Toast.LENGTH_SHORT).show();
                    }
                    else if (response.code() == 422){
                        Toast.makeText(UpdateUserActivity.this, R.string.error_email_existed, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(UpdateUserActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UpdateUserActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
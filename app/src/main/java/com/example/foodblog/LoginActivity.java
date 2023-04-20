package com.example.foodblog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.api_instance.AuthorizationApiInstance;
import com.example.foodblog.model.LoginInfo;
import com.example.foodblog.model.ResponseLogin;
import com.example.foodblog.model.ResponseUser;
import com.example.foodblog.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static int REQUEST_REGISTER_CODE = 2703;
    private static final String TAG_REMEMBER = "TAG_REMEMBER";
    private static final String TAG_USERNAME = "TAG_USERNAME";
    private static final String TAG_PASSWORD = "TAG_PASSWORD";
    private SharedPreferences sharedPreferences;
    private EditText edtUsername, edtPassword;
    private ImageView imgShowPassword;
    private Button btnLogin;
    private CheckBox cbRemember;
    private LinearLayout btnRegister, layoutProgressLogin;
    private TextView tvForgotPassword;

    private boolean isPasswordShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setControl();
        setEvent();
    }
    private void setEvent() {
        imgShowPassword.setOnClickListener(view -> {
            isPasswordShowed = !isPasswordShowed;
            int selectionIndex = edtPassword.getSelectionEnd();
            if (isPasswordShowed){
                imgShowPassword.setImageResource(R.drawable.baseline_visibility_24);
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                imgShowPassword.setImageResource(R.drawable.baseline_visibility_off_24);
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            edtPassword.setSelection(selectionIndex);
        });
        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            LoginInfo loginInfo = new LoginInfo(username, password);
            login(loginInfo);
        });
        btnRegister.setOnClickListener(view -> {
            startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REQUEST_REGISTER_CODE);
        });
        tvForgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void setControl() {
        edtUsername = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        imgShowPassword = findViewById(R.id.imgShowPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        layoutProgressLogin = findViewById(R.id.layoutProgressLogin);
        cbRemember = findViewById(R.id.cbRemember);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        setData();
    }

    private void setData() {
        boolean remember = sharedPreferences.getBoolean(TAG_REMEMBER, false);
        if (remember){
            String username = sharedPreferences.getString(TAG_USERNAME, "");
            String password = sharedPreferences.getString(TAG_PASSWORD, "");
            if (!username.equals("") && !password.equals("")){
                cbRemember.setChecked(true);
                edtUsername.setText(username);
                edtPassword.setText(password);
                LoginInfo loginInfo = new LoginInfo(username, password);
                login(loginInfo);
            }
        }
    }

    private void rememberLogin(LoginInfo loginInfo){
        sharedPreferences
                .edit()
                .putBoolean(TAG_REMEMBER, true)
                .putString(TAG_USERNAME, loginInfo.getAccountName())
                .putString(TAG_PASSWORD, loginInfo.getPassword())
                .apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER_CODE){
            if (resultCode == RESULT_OK){
                String username = data.getStringExtra(RegisterActivity.EXTRA_USERNAME);
                String password = data.getStringExtra(RegisterActivity.EXTRA_PASSWORD);
                if (username != null && password != null){
                    edtUsername.setText(username);
                    edtPassword.setText(password);
                }
            }
        }
    }

    private void login(LoginInfo loginInfo){
        layoutProgressLogin.setVisibility(View.VISIBLE);
        Call<ResponseLogin> call = AuthorizationApiInstance.getInstance().login(loginInfo);
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful() || response.code() == 200){
                    ResponseLogin responseLogin = response.body();
                    MyAuthorization.getInstance().setToken(responseLogin.getData());
                    Call<ResponseUser> callUser = AuthorizationApiInstance.getInstance().getUserInfo(MyAuthorization.getInstance().getToken());
                    callUser.enqueue(new Callback<ResponseUser>() {
                        @Override
                        public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                            if (response.isSuccessful()){
                                User user = response.body().getData();
                                MyAuthorization.getInstance().setUser(user);
                                MyAuthorization.getInstance().setUsername(loginInfo.getAccountName());
                                rememberLogin(loginInfo);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(LoginActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                                try {
                                    Toast.makeText(LoginActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e){

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseUser> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
                }
                else if (response.code() == 424 || response.code() == 425){
                    Toast.makeText(LoginActivity.this, getText(R.string.error_login_424_425), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                }
                layoutProgressLogin.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                layoutProgressLogin.setVisibility(View.GONE);
            }
        });
    }
}
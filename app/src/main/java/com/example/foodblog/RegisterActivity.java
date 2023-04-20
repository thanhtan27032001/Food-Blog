package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.foodblog.api_instance.AuthorizationApiInstance;
import com.example.foodblog.model.RegisterInfo;
import com.example.foodblog.model.ResponseObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtUserName, edtPassword, edtConfirmPassword;
    private ImageView imgShowPassword, imgShowConfirmPassword;
    private Button btnRegister;
    private LinearLayout btnLogin;

    private boolean isPasswordShowed = false;
    private boolean isConfirmPasswordShowed = false;
    private RegisterInfo registerInfo;
    public static String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static String EXTRA_PASSWORD = "EXTRA_PASSWORD";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setControl();
        setEvent();
    }

    private void register(){
        Call<ResponseObject> call = AuthorizationApiInstance.getInstance().register(registerInfo);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 201){
                    Toast.makeText(RegisterActivity.this, R.string.success_register, Toast.LENGTH_SHORT).show();
                    // return data
                    Intent data = new Intent();
                    data.putExtra(EXTRA_USERNAME, registerInfo.getAccountName());
                    data.putExtra(EXTRA_PASSWORD, registerInfo.getPassword());
                    setResult(RESULT_OK, data);
                    finish();
                }
                else if (response.code() == 418){
                    Toast.makeText(RegisterActivity.this, getText(R.string.error_register_418), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 419){
                    Toast.makeText(RegisterActivity.this, getText(R.string.error_register_419), Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 420){
                    edtPassword.setError(getText(R.string.error_register_420));
                    edtPassword.requestFocus();
                }
                else if (response.code() == 421){
                    edtEmail.setError(getText(R.string.error_register_421));
                    edtEmail.requestFocus();
                }
                else if (response.code() == 422){
                    edtEmail.setError(getText(R.string.error_register_422));
                    edtEmail.requestFocus();
                }
                else if (response.code() == 423){
                    Toast.makeText(RegisterActivity.this, getText(R.string.error_register_423), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterActivity.this, getText(R.string.error_500), Toast.LENGTH_SHORT).show();
                Log.e("ERROR LOGIN", t.getMessage());
            }
        });
    }
    private boolean isValidated(String fullName, String email, String username, String password, String confirmPassword){
        if (fullName.equals("")){
            edtFullName.setError(getText(R.string.error_miss_name));
            edtFullName.requestFocus();
            return false;
        }
        if (email.equals("")){
            edtEmail.setError(getText(R.string.error_miss_email));
            edtEmail.requestFocus();
            return false;
        }
        if (username.equals("")){
            edtUserName.setError(getText(R.string.error_miss_username));
            edtUserName.requestFocus();
            return false;
        }
        if (password.equals("")){
            edtPassword.requestFocus();
            edtPassword.setError(getText(R.string.error_miss_username));
            return false;
        }
        if (!confirmPassword.equals(password)){
            edtConfirmPassword.requestFocus();
            edtConfirmPassword.setError(getText(R.string.error_confirm_password_incorrect));
            return false;
        }
        return true;
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
        imgShowConfirmPassword.setOnClickListener(view -> {
            isConfirmPasswordShowed = !isConfirmPasswordShowed;
            int selectionIndex = edtConfirmPassword.getSelectionEnd();
            if (isConfirmPasswordShowed){
                imgShowConfirmPassword.setImageResource(R.drawable.baseline_visibility_24);
                edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                imgShowConfirmPassword.setImageResource(R.drawable.baseline_visibility_off_24);
                edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            edtConfirmPassword.setSelection(selectionIndex);
        });
        btnRegister.setOnClickListener(view -> {
            String fullName = edtFullName.getText().toString();
            String email = edtEmail.getText().toString();
            String username = edtUserName.getText().toString();
            String password = edtPassword.getText().toString();
            String confirmPassword = edtConfirmPassword.getText().toString();
            if(isValidated(fullName, email, username, password, confirmPassword)){
                registerInfo = new RegisterInfo(fullName, email, username, password, confirmPassword);
                register();
            }
        });
        btnLogin.setOnClickListener(view -> finish());
    }

    private void setControl() {
        edtFullName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUserName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        imgShowPassword = findViewById(R.id.imgShowPassword);
        imgShowConfirmPassword = findViewById(R.id.imgShowConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
    }
}
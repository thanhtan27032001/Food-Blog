package com.example.foodblog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodblog.api_instance.AuthorizationApiInstance;
import com.example.foodblog.model.ChangePasswordInfo;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseOtp;
import com.example.foodblog.model.SendOtpInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtUsername, edtNewPassword, edtConfirmPassword;
    TextView tvSendOtp, tvCountDown;
    Button btnChangePassword;
    ImageButton btnBack;
    long otpAvailableTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setControl();
        setEvent();
    }

    private void setControl() {
        tvSendOtp = findViewById(R.id.tvSendOtp);
        edtUsername = findViewById(R.id.edtUsername);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnBack = findViewById(R.id.btnBack);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> finish());
        btnChangePassword.setOnClickListener(view -> {
            if (isInfoValidated()){
                showDialogOtp();
            }
        });
    }

    private void showDialogOtp() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_otp);
        EditText edtOtp1 = dialog.findViewById(R.id.edtOtp1);
        EditText edtOtp2 = dialog.findViewById(R.id.edtOtp2);
        EditText edtOtp3 = dialog.findViewById(R.id.edtOtp3);
        EditText edtOtp4 = dialog.findViewById(R.id.edtOtp4);
        EditText edtOtp5 = dialog.findViewById(R.id.edtOtp5);
        EditText edtOtp6 = dialog.findViewById(R.id.edtOtp6);
        TextView tvSendOtp = dialog.findViewById(R.id.tvSendOtp);
        tvCountDown = dialog.findViewById(R.id.tvOtpCountDown);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        LinearLayout layout = dialog.findViewById(R.id.layout);

        try { // Adjust dialog width fit to screen
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
            layout.requestLayout();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        tvSendOtp.setOnClickListener(view -> {
            if (otpAvailableTime > 0){
                Toast.makeText(this, R.string.warning_otp_available, Toast.LENGTH_SHORT).show();
            }
            else {
                sendOtp();
            }
        });
        btnSubmit.setOnClickListener(view -> {
            String otp = edtOtp1.getText().toString() +
                    edtOtp2.getText().toString() +
                    edtOtp3.getText().toString() +
                    edtOtp4.getText().toString() +
                    edtOtp5.getText().toString() +
                    edtOtp6.getText().toString();
            if (isOtpValidated(otp)){
                if (otpAvailableTime > 0){
                    changePassword(otp);
                }
                else {
                    Toast.makeText(this, R.string.error_otp_451, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, R.string.error_otp_invalid, Toast.LENGTH_SHORT).show();
            }
        });
        edtOtp1.setSelectAllOnFocus(true);
        edtOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int temp = Integer.parseInt(charSequence.toString());
                    edtOtp2.requestFocus();
                    edtOtp2.selectAll();
                }
                catch (Exception e){
                    Log.i("log", "not number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edtOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int temp = Integer.parseInt(charSequence.toString());
                    edtOtp3.requestFocus();
                    edtOtp3.selectAll();
                }
                catch (Exception e){
                    Log.i("log", "not number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edtOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int temp = Integer.parseInt(charSequence.toString());
                    edtOtp4.requestFocus();
                    edtOtp4.selectAll();
                }
                catch (Exception e){
                    Log.i("log", "not number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int temp = Integer.parseInt(charSequence.toString());
                    edtOtp5.requestFocus();
                    edtOtp5.selectAll();
                }
                catch (Exception e){
                    Log.i("log", "not number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edtOtp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int temp = Integer.parseInt(charSequence.toString());
                    edtOtp6.requestFocus();
                    edtOtp6.selectAll();
                }
                catch (Exception e){
                    Log.i("log", "not number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edtOtp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int temp = Integer.parseInt(charSequence.toString());
                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtOtp6.getWindowToken(), 0);
                }
                catch (Exception e){
                    Log.i("log", "not number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private boolean isOtpValidated(String otp) {
        return otp.length() == 6;
    }

    private void changePassword(String otp) {
        Log.e("test", edtUsername.getText().toString());
        Call<ResponseObject> call = AuthorizationApiInstance.getInstance().changePassword(new ChangePasswordInfo(
                edtUsername.getText().toString(),
                edtNewPassword.getText().toString(),
                edtConfirmPassword.getText().toString(),
                otp)
        );
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() || response.code() == 200){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.success_change_password, Toast.LENGTH_SHORT).show();
                    ForgotPasswordActivity.this.finish();
                }
                else if (response.code() == 420){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_register_420, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 424){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_otp_424, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 429){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_otp_449, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 450){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_otp_450, Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 451){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_otp_451, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.code() + " " + response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private boolean isInfoValidated() {
        if (edtUsername.getText().toString().equals("")){
            edtUsername.setError(getText(R.string.error_miss_username_forgot_password));
            edtUsername.requestFocus();
            return false;
        }
        if (edtNewPassword.getText().toString().equals("")){
            edtNewPassword.setError(getText(R.string.error_miss_new_password));
            edtNewPassword.requestFocus();
            return false;
        }
        if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())){
            edtConfirmPassword.setError(getText(R.string.error_confirm_password_incorrect));
            edtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void sendOtp() {
        Toast.makeText(this, getText(R.string.noti_sending_otp) + "...", Toast.LENGTH_SHORT).show();
        Call<ResponseOtp> call = AuthorizationApiInstance.getInstance().sendOtp(new SendOtpInfo(
                edtUsername.getText().toString(),
                getText(R.string.otp_subject).toString()
        ));
        call.enqueue(new Callback<ResponseOtp>() {
            @Override
            public void onResponse(Call<ResponseOtp> call, Response<ResponseOtp> response) {
                if (response.isSuccessful() || response.code() == 200){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.success_send_otp, Toast.LENGTH_SHORT).show();
                    String time = response.body().getData();
                    countDownOtp(time);
                }
                else if (response.code() == 424){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_otp_424, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOtp> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void countDownOtp(String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date dateStart = new Date();
            Date dateEnd = simpleDateFormat.parse(time);
            otpAvailableTime = dateEnd.getTime() - dateStart.getTime(); // milliseconds
            Log.e("test", "start: " + simpleDateFormat.format(dateStart));
            Log.e("test", "end: " + time);
            Log.e("test", "duration: " + otpAvailableTime);
            CountDownTimer countDownTimer = new CountDownTimer(otpAvailableTime, 1000) {
                @Override
                public void onTick(long l) {
                    otpAvailableTime = l/1000;
                    tvCountDown.setText(String.valueOf(otpAvailableTime));
                }

                @Override
                public void onFinish() {

                }
            };
            countDownTimer.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
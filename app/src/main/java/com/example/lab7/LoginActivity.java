package com.example.lab7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buoi1demo.R;
import com.example.lab7.model.LoginData;
import com.example.lab7.model.Response;
import com.example.lab7.model.User;
import com.example.lab7.service.HttpRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton btnLogin, btnRegister;
    private TextInputEditText edtUsername, edtPassword;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_lab7);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        httpRequest = new HttpRequest();

        btnLogin.setOnClickListener(v -> {
            User user = new User();
            String _user = edtUsername.getText().toString().trim();
            String _pass = edtPassword.getText().toString().trim();

            if (_user.isEmpty() || _pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setUsername(_user);
            user.setPassword(_pass);
            httpRequest.callAPI().login(user).enqueue(responseUser);
        });
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    Callback<Response<LoginData>> responseUser = new Callback<Response<LoginData>>() {
        @Override
        public void onResponse(Call<Response<LoginData>> call, retrofit2.Response<Response<LoginData>> response) {
            Response<LoginData> responseBody = response.body();

            if (response.isSuccessful() && responseBody != null) {
                User loggedInUser = responseBody.getData() == null ? null : responseBody.getData().getUser();

                if (responseBody.getStatus() == 200 && loggedInUser != null) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", responseBody.getToken());
                    editor.putString("refreshToken", responseBody.getRefreshToken());
                    editor.putString("id", loggedInUser.getId());
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, Lab7Activity.class));
                } else {
                    Toast.makeText(LoginActivity.this, responseBody.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<LoginData>> call, Throwable throwable) {
            Log.d(">>> Login", "onFailure: " + throwable.getMessage());
            Toast.makeText(LoginActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void initUi() {
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }
}

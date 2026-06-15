package com.example.lab7;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.buoi1demo.R;
import com.example.lab7.model.Response;
import com.example.lab7.model.User;
import com.example.lab7.service.HttpRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtPassword, edtEmail, edtName, edtAge, edtAddress;
    private Button btnRegister;
    private ImageView imgAvatar;

    private File fileAvatar;
    private HttpRequest httpRequest;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            fileAvatar = createFileFromUri(uri, "avatar");

                            Glide.with(RegisterActivity.this)
                                    .load(uri)
                                    .circleCrop()
                                    .centerCrop()
                                    .skipMemoryCache(true)
                                    .into(imgAvatar);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_lab7);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        btnRegister = findViewById(R.id.btnRegister);
        imgAvatar = findViewById(R.id.avatar);

        httpRequest = new HttpRequest();

        imgAvatar.setOnClickListener(v -> chooseImage());

        btnRegister.setOnClickListener(v -> register());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    private File createFileFromUri(Uri path, String name) {
        File file = new File(getCacheDir(), name + ".png");

        try {
            InputStream in = RegisterActivity.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void register() {
        String usernameValue = edtUsername.getText().toString().trim();
        String passwordValue = edtPassword.getText().toString().trim();
        String emailValue = edtEmail.getText().toString().trim();
        String nameValue = edtName.getText().toString().trim();

        if (usernameValue.isEmpty() || passwordValue.isEmpty() || emailValue.isEmpty() || nameValue.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileAvatar == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh avatar", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody username = RequestBody.create(MediaType.parse("multipart/form-data"), usernameValue);
        RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"), passwordValue);
        RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"), emailValue);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), nameValue);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileAvatar);

        MultipartBody.Part avatar = MultipartBody.Part.createFormData(
                "avatar",
                fileAvatar.getName(),
                requestFile
        );

        httpRequest.callAPI().register(username, password, email, name, avatar)
                .enqueue(new Callback<Response<User>>() {
                    @Override
                    public void onResponse(Call<Response<User>> call,
                                           retrofit2.Response<Response<User>> response) {
                        Response<User> responseBody = response.body();

                        if (response.isSuccessful() && responseBody != null) {
                            Toast.makeText(RegisterActivity.this,
                                    responseBody.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            if (responseBody.getStatus() == 200) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<User>> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

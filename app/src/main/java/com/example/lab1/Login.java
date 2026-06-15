package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buoi1demo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private MaterialButton btnLogin, btnRegister, btnLoginPhone, btnForgotPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Vui long nhap day du thong tin", Toast.LENGTH_SHORT).show();
            }
            if (password.length() < 6) {
                Toast.makeText(Login.this, "Mat khau phai co it nhat 6 ky tu", Toast.LENGTH_SHORT).show();
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ///  truyền thông tin taif khoản vừa đăng nhập
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(Login.this, "Dang nhap thanh cong " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, HomePage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnLoginPhone.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, LoginWithPhone.class));
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Vui long khong duoc bo trong", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(Login.this, "Dang ky thanh cong", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Dang ky that bai", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, ForgotPassword.class));
        });
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginPhone = findViewById(R.id.btnLoginPhone);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
    }
}
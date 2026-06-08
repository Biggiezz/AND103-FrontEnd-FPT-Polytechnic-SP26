package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
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

public class ForgotPassword extends AppCompatActivity {
    private static final String TAG = "ForgotPassword";

    private TextInputEditText edtEmail;
    private MaterialButton btnResetPassword, btnLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        mAuth = FirebaseAuth.getInstance();
        btnResetPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền Email", Toast.LENGTH_LONG).show();
                return;
            }

            btnResetPassword.setEnabled(false);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                btnResetPassword.setEnabled(true);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Đã gửi email", Toast.LENGTH_LONG).show();
                } else {
                    Exception exception = task.getException();
                    String message = exception == null ? "Không gửi được email đặt lại mật khẩu" : exception.getMessage();
//                    Log.e(TAG, "sendPasswordResetEmail failed", exception);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this, Login.class));
        });
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edtEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

}

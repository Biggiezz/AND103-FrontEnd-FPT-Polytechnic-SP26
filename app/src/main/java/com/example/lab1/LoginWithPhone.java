package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buoi1demo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginWithPhone extends AppCompatActivity {

    private TextInputEditText edtPhone, edtOtp;
    private MaterialButton btnGetOtp, btnLogin;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_with_phone);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                String smsCode = credential.getSmsCode();

                if (smsCode != null) {
                    edtOtp.setText(smsCode);
                }

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginWithPhone.this, "Gửi OTP thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(
                    @NonNull String verificationId,
                    @NonNull PhoneAuthProvider.ForceResendingToken token
            ) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;

                Toast.makeText(LoginWithPhone.this, "Gửi OTP thành công", Toast.LENGTH_SHORT).show();
            }
        };

        btnGetOtp.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đổi 0943718195 thành +84943718195
            if (phone.startsWith("0")) {
                phone = "+84" + phone.substring(1);
            }

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(mCallbacks)
                            .build();

            PhoneAuthProvider.verifyPhoneNumber(options);

            Toast.makeText(this, "Đang gửi OTP tới: " + phone, Toast.LENGTH_SHORT).show();
        });
        btnLogin.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();

            if (otp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mVerificationId == null) {
                Toast.makeText(this, "Bạn chưa lấy mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(mVerificationId, otp);

            signInWithPhoneAuthCredential(credential);
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginWithPhone.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginWithPhone.this, HomePage.class));
                        finish();
                    } else {
                        Toast.makeText(LoginWithPhone.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginWithPhone.this, "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initUi() {
        edtPhone = findViewById(R.id.edtPhone);
        edtOtp = findViewById(R.id.edtOtp);
        btnGetOtp = findViewById(R.id.btnGetOTP);
        btnLogin = findViewById(R.id.btnLogin);
    }
}
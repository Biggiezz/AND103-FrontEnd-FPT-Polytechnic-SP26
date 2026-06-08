package com.example.demoRetrofit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buoi1demo.R;
import com.example.demoRetrofit.adapter.DemoRetrofitAdapter;
import com.example.demoRetrofit.handle.Item_User_Handle;
import com.example.demoRetrofit.model.Response;
import com.example.demoRetrofit.model.User;
import com.example.demoRetrofit.services.HttpRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class DemoRetrofit extends AppCompatActivity {

    private HttpRequest httpRetrofit;
    private RecyclerView recyclerDemoRetrofit;
    private Button btnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demo_retrofit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();
        httpRetrofit = new HttpRequest();
        loadUsers();
        btnAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void initUi() {
        recyclerDemoRetrofit = findViewById(R.id.recycleDemoRetrofit);
        btnAddUser = findViewById(R.id.btnAddUser);
    }

    private void loadUsers() {
        httpRetrofit.callAPI().getListUser().enqueue(getListUserAPI);
    }

    private void showAddUserDialog() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_update_user, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        TextInputEditText edtUsername = view.findViewById(R.id.edt_username);
        TextInputEditText edtAge = view.findViewById(R.id.edt_age);
        TextInputEditText edtAddress = view.findViewById(R.id.edt_adress);
        TextInputEditText edtImage = view.findViewById(R.id.edtImage);

        ImageView imgPreview = view.findViewById(R.id.imgUser);

        MaterialButton btnUpdate = view.findViewById(R.id.btn_update);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        btnUpdate.setText("Thêm");

        setupImagePreview(edtImage, imgPreview);

        btnUpdate.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String age = edtAge.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String image = edtImage.getText().toString().trim();

            if (username.isEmpty() || age.isEmpty() || address.isEmpty() || image.isEmpty()) {
                Toast.makeText(DemoRetrofit.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int ageInt;
            try {
                ageInt = Integer.parseInt(age);
            } catch (NumberFormatException e) {
                Toast.makeText(DemoRetrofit.this, "Tuổi phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(username, ageInt, address, image);

            httpRetrofit.callAPI()
                    .addUser(user)
                    .enqueue(addUserAPI);

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupImagePreview(TextInputEditText edtImage, ImageView imgPreview) {
        edtImage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String imageLink = s.toString();

                if (imageLink.startsWith("http://") || imageLink.startsWith("https://")) {
                    Glide.with(DemoRetrofit.this)
                            .load(imageLink)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_delete)
                            .into(imgPreview);
                }
            }
        });
    }

    private final Callback<Response<User>> addUserAPI = new Callback<Response<User>>() {
        @Override
        public void onResponse(
                Call<Response<User>> call,
                retrofit2.Response<Response<User>> response
        ) {
            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(DemoRetrofit.this, "Thêm user thất bại", Toast.LENGTH_SHORT).show();
                return;
            }

            Response<User> apiResponse = response.body();

            if (apiResponse.getStatus() == 200) {
                loadUsers();
                Toast.makeText(DemoRetrofit.this, "Thêm user thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                        DemoRetrofit.this,
                        apiResponse.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable throwable) {
            Log.e("AddUserAPI", "Error: " + throwable.getMessage());
            Toast.makeText(DemoRetrofit.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
        }
    };

    private final Callback<Response<User>> updateUserAPI = new Callback<Response<User>>() {
        @Override
        public void onResponse(
                Call<Response<User>> call,
                retrofit2.Response<Response<User>> response
        ) {
            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(
                        DemoRetrofit.this,
                        "Cập nhật user thất bại",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Response<User> apiResponse = response.body();

            if (apiResponse.getStatus() == 200) {
                loadUsers();

                Toast.makeText(
                        DemoRetrofit.this,
                        "Cập nhật user thành công",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        DemoRetrofit.this,
                        apiResponse.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable throwable) {
            Log.e("UpdateUserAPI", "Error: " + throwable.getMessage());

            Toast.makeText(
                    DemoRetrofit.this,
                    "Không kết nối được server",
                    Toast.LENGTH_SHORT
            ).show();
        }
    };

    private final Callback<Response<User>> deleteUserAPI = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(DemoRetrofit.this, "Xóa user thất bại", Toast.LENGTH_SHORT).show();
                return;
            }

            Response<User> apiResponse = response.body();

            if (apiResponse.getStatus() == 200) {
                loadUsers();

                Toast.makeText(DemoRetrofit.this, "Xóa user thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DemoRetrofit.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable throwable) {
            Log.e("DeleteUserAPI", "Error: " + throwable.getMessage());
            Toast.makeText(DemoRetrofit.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
        }
    };

    private final Callback<Response<ArrayList<User>>> getListUserAPI = new Callback<Response<ArrayList<User>>>() {
                @Override
                public void onResponse(Call<Response<ArrayList<User>>> call, retrofit2.Response<Response<ArrayList<User>>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(DemoRetrofit.this, "Không lấy được danh sách user", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Response<ArrayList<User>> apiResponse = response.body();

                    if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                        getData(apiResponse.getData());
                    } else {
                        Toast.makeText(DemoRetrofit.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<ArrayList<User>>> call, Throwable throwable) {
                    Log.e("GetListUserAPI", "Error: " + throwable.getMessage());
                    Toast.makeText(DemoRetrofit.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
                }
            };

    private void getData(ArrayList<User> list) {
        DemoRetrofitAdapter adapter = new DemoRetrofitAdapter(list, this, new Item_User_Handle() {
            @Override
            public void onEdit(int position) {
                showUpdateUserDialog(list.get(position));
            }

            @Override
            public void onDelete(int position) {
                User user = list.get(position);
                httpRetrofit.callAPI().deleteUser(user.getId()).enqueue(deleteUserAPI);
            }

            @Override
            public void onDetail(int position) {

            }
        });

        recyclerDemoRetrofit.setLayoutManager(new LinearLayoutManager(this));
        recyclerDemoRetrofit.setAdapter(adapter);
    }

    private void showUpdateUserDialog(User user) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_update_user, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        EditText edtUsername = view.findViewById(R.id.edt_username);
        EditText edtAge = view.findViewById(R.id.edt_age);
        EditText edtAddress = view.findViewById(R.id.edt_adress);
        TextInputEditText edtImage = view.findViewById(R.id.edtImage);

        ImageView imgPreview = view.findViewById(R.id.imgUser);

        MaterialButton btnUpdate = view.findViewById(R.id.btn_update);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        btnUpdate.setText("Cập nhật");

        edtUsername.setText(user.getUsername());
        edtAge.setText(String.valueOf(user.getAge()));
        edtAddress.setText(user.getAdress());
        edtImage.setText(user.getImage());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            Glide.with(this)
                    .load(user.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgPreview);
        }

        setupImagePreview(edtImage, imgPreview);

        btnUpdate.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String age = edtAge.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String image = edtImage.getText().toString().trim();

            if (username.isEmpty() || age.isEmpty() || address.isEmpty() || image.isEmpty()) {
                Toast.makeText(DemoRetrofit.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int ageInt;

            try {
                ageInt = Integer.parseInt(age);
            } catch (NumberFormatException e) {
                Toast.makeText(DemoRetrofit.this, "Tuổi phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, ageInt, address, image);

            httpRetrofit.callAPI()
                    .updateUser(user.getId(), newUser)
                    .enqueue(updateUserAPI);

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
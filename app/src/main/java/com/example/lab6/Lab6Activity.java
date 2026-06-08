package com.example.lab6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import com.example.buoi1demo.R;
import com.bumptech.glide.Glide;
import com.example.lab6.adapter.FruitAdapter;
import com.example.lab6.model.Fruit;
import com.example.lab6.model.Response;
import com.example.lab6.service.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Lab6Activity extends AppCompatActivity {


    private HttpRequest httpRequest;
    private FruitAdapter adapter;
    private String token;
    private FloatingActionButton fabAddFruit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab6);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();
        httpRequest = new HttpRequest();
        SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            return;
        }

        fabAddFruit.setOnClickListener(v -> startActivity(new Intent(Lab6Activity.this, AddFruitActivity.class)));
    }

    private void initUi() {
        RecyclerView recycleFruits = findViewById(R.id.recycle_fruits);
        fabAddFruit = findViewById(R.id.fabAddFruit);

        adapter = new FruitAdapter(new ArrayList<>(), this, new FruitAdapter.OnFruitClickListener() {
            @Override
            public void onUpdate(Fruit fruit) {
                showUpdateFruitDialog(fruit);
            }

            @Override
            public void onDelete(Fruit fruit) {
                confirmDeleteFruit(fruit);
            }
        });
        recycleFruits.setLayoutManager(new LinearLayoutManager(this));
        recycleFruits.setAdapter(adapter);
    }

    private void fetchFruitList() {
        httpRequest.callAPI()
                .getListFruit("Bearer " + token)
                .enqueue(getListFruitResponse);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (httpRequest != null && token != null && !token.isEmpty()) {
            fetchFruitList();
        }
    }

    private final Callback<Response<ArrayList<Fruit>>> getListFruitResponse =
            new Callback<Response<ArrayList<Fruit>>>() {
                @Override
                public void onResponse(Call<Response<ArrayList<Fruit>>> call,
                                       retrofit2.Response<Response<ArrayList<Fruit>>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(Lab6Activity.this, "Không lấy được danh sách: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        Log.e(">>> GetListFruit", "HTTP error: " + response.code());
                        return;
                    }


                    if (response.body().getStatus() == 200) {
                        ArrayList<Fruit> ds = response.body().getData();
                        getData(ds);
                        int count = ds == null ? 0 : ds.size();
                        Toast.makeText(Lab6Activity.this, response.body().getMessage() + ": " + count + " fruit", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Lab6Activity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable throwable) {
                    Log.d(">>> GetListFruit", "onFailure: " + throwable.getMessage());
                    Toast.makeText(Lab6Activity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private void getData(ArrayList<Fruit> ds) {
        adapter.setData(ds == null ? new ArrayList<>() : ds);
    }

    private void showUpdateFruitDialog(Fruit fruit) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_fruit, null);

        ImageView imgFruit = view.findViewById(R.id.imgFruit);
        TextInputEditText edtName = view.findViewById(R.id.edtFruitName);
        TextInputEditText edtQuantity = view.findViewById(R.id.edtQuantity);
        TextInputEditText edtPrice = view.findViewById(R.id.edtPrice);
        TextInputEditText edtDescription = view.findViewById(R.id.edtDescription);
        Button btnUpdate = view.findViewById(R.id.btn_add);

        if (fruit.getImage() != null && !fruit.getImage().isEmpty()) {
            Glide.with(this)
                    .load(fruit.getImage().get(0))
                    .into(imgFruit);
        }
        edtName.setText(fruit.getName());
        edtQuantity.setText(String.valueOf(fruit.getQuantity()));
        edtPrice.setText(String.valueOf(fruit.getPrice()));
        edtDescription.setText(fruit.getDescription());
        btnUpdate.setText("Update");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cập nhật " + fruit.getName())
                .setView(view)
                .create();

        btnUpdate.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            String newQuantity = edtQuantity.getText().toString().trim();
            String newPrice = edtPrice.getText().toString().trim();
            String newDescription = edtDescription.getText().toString().trim();

            if (newName.isEmpty() || newQuantity.isEmpty() || newPrice.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(Lab6Activity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            int price;

            try {
                quantity = Integer.parseInt(newQuantity);
                price = Integer.parseInt(newPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(Lab6Activity.this, "Số lượng và giá phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            Fruit updateFruit = new Fruit();
            updateFruit.setName(newName);
            updateFruit.setQuantity(quantity);
            updateFruit.setPrice(price);
            updateFruit.setDescription(newDescription);

            httpRequest.callAPI()
                    .updateFruit(fruit.get_id(), updateFruit)
                    .enqueue(new Callback<Response<Fruit>>() {
                        @Override
                        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                            if (!response.isSuccessful() || response.body() == null) {
                                Toast.makeText(Lab6Activity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(Lab6Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus() == 200) {
                                dialog.dismiss();
                                fetchFruitList();
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<Fruit>> call, Throwable throwable) {
                            Toast.makeText(Lab6Activity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    private void confirmDeleteFruit(Fruit fruit) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa fruit")
                .setMessage("Bạn có muốn xóa fruit này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> deleteFruit(fruit))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteFruit(Fruit fruit) {
        httpRequest.callAPI()
                .deleteFruit(fruit.get_id())
                .enqueue(new Callback<Response<Fruit>>() {
                    @Override
                    public void onResponse(Call<Response<Fruit>> call,
                                           retrofit2.Response<Response<Fruit>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(Lab6Activity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(Lab6Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        if (response.body().getStatus() == 200) {
                            fetchFruitList();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<Fruit>> call, Throwable throwable) {
                        Toast.makeText(Lab6Activity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

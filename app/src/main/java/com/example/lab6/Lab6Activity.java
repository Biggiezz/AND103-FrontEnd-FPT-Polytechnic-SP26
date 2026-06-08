package com.example.lab6;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buoi1demo.R;
import com.example.lab6.adapter.FruitAdapter;
import com.example.lab6.model.Fruit;
import com.example.lab6.model.Response;
import com.example.lab6.service.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Lab6Activity extends AppCompatActivity {


    private HttpRequest httpRequest;
    private RecyclerView recycleFruits;
    private FruitAdapter adapter;
    private SharedPreferences sharedPreferences;
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
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            return;
        }

        fabAddFruit.setOnClickListener(v -> {
            startActivity(new Intent(Lab6Activity.this, AddFruitActivity.class));
        });
    }

    private void initUi() {
        recycleFruits = findViewById(R.id.recycle_fruits);
        fabAddFruit = findViewById(R.id.fabAddFruit);

        adapter = new FruitAdapter(new ArrayList<>(), this);
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
}

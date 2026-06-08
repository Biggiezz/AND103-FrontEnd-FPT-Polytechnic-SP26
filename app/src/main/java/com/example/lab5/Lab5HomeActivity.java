package com.example.lab5;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buoi1demo.R;
import com.example.lab5.adapter.Recycle_Item_Distributor;
import com.example.lab5.model.Distributor;
import com.example.lab5.model.Response;
import com.example.lab5.service.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Lab5HomeActivity extends AppCompatActivity {

    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private Recycle_Item_Distributor adapter;
    private EditText searchDistributor;
    private FloatingActionButton fabAddDistributor;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab5_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        httpRequest = new HttpRequest();
        httpRequest.callAPI()
                .getListDistributor()
                .enqueue(getListDistributor);

        searchDistributor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = s.toString().trim();

                Log.d("SEARCH_KEY", "Dang nhap: " + key);

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    if (key.isEmpty()) {
                        Log.d("SEARCH_API", "Lay lai danh sach ban dau");

                        httpRequest.callAPI()
                                .getListDistributor()
                                .enqueue(getListDistributor);
                    } else {
                        Log.d("SEARCH_API", "Tim kiem voi key: " + key);

                        httpRequest.callAPI()
                                .searchDistributor(key)
                                .enqueue(getListDistributor);
                    }
                };

                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        fabAddDistributor.setOnClickListener(v -> {
            showAddDialog();
        });
    }

    private void initUi() {
        recyclerView = findViewById(R.id.recycleViewLab5);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchDistributor = findViewById(R.id.searchDistributor);
        fabAddDistributor = findViewById(R.id.fabAddDistributor);
    }


    private void getData(ArrayList<Distributor> ds) {
        adapter = new Recycle_Item_Distributor(this, ds);
        recyclerView.setAdapter(adapter);
    }

    private void showAddDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_distributor, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        EditText edt_dialog_name = view.findViewById(R.id.edt_dialog_name);
        Button btn_add = view.findViewById(R.id.btn_add);

        btn_add.setOnClickListener(v -> {
            String name = edt_dialog_name.getText().toString();
            Distributor distributor = new Distributor();
            distributor.setName(name);
            httpRequest.callAPI()
                    .addDistributor(distributor)
                    .enqueue(responseDistributorAPI);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    Callback<Response<ArrayList<Distributor>>> getListDistributor = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful() && response.body() != null) {
                Response<ArrayList<Distributor>> body = response.body();
                if (body.getStatus() == 200) {
                    ArrayList<Distributor> ds = body.getData();
                    getData(ds);
                    Toast.makeText(Lab5HomeActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Lab5HomeActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Lab5HomeActivity.this, "Khong lay duoc danh sach", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable throwable) {
            Log.d(">>> GetListDistributor", "onFailure: " + throwable.getMessage());
            Toast.makeText(Lab5HomeActivity.this, "Loi ket noi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    Callback<Response<Distributor>> responseDistributorAPI = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                httpRequest.callAPI()
                        .getListDistributor()
                        .enqueue(getListDistributor);
                Toast.makeText(Lab5HomeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable throwable) {
            Log.d(">>> AddDistributor", "onFailure: " + throwable.getMessage());
        }
    };

}

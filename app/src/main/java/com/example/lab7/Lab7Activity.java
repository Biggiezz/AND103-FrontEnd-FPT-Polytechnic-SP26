package com.example.lab7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buoi1demo.R;
import com.example.lab7.adapter.FruitAdapter;
import com.example.lab7.model.Fruit;
import com.example.lab7.model.Page;
import com.example.lab7.model.Response;
import com.example.lab7.service.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Lab7Activity extends AppCompatActivity {
    private ProgressBar loadMore;
    private RecyclerView recyclerView;
    private ArrayList<Fruit> list = new ArrayList<>();
    private int page = 1;
    private int totalPage = 0;
    private NestedScrollView nestedScrollView;
    private FloatingActionButton fabAddFruit;
    private HttpRequest httpRequest;
    private FruitAdapter adapter;
    private String token;
    private boolean isLoading = false;
    private EditText searchFruit, filterPrice;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable filterRunnable;
    private String keyword = "";
    private String price = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab7);

        initUi();
        httpRequest = new HttpRequest();
        SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            return;
        }
        TextWatcher filterTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = searchFruit.getText().toString().trim();
                price = filterPrice.getText().toString().trim();
                if (filterRunnable != null) {
                    handler.removeCallbacks(filterRunnable);
                }
                filterRunnable = () -> {
                    page = 1;
                    totalPage = 0;
                    fetchFruitPage(page);
                };
                handler.postDelayed(filterRunnable, 500);
            }
        };
        searchFruit.addTextChangedListener(filterTextWatcher);
        filterPrice.addTextChangedListener(filterTextWatcher);

        fabAddFruit.setOnClickListener(v -> startActivity(new Intent(Lab7Activity.this, AddFruitActivity.class)));

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (isLoading || page >= totalPage) return;
                    if (loadMore.getVisibility() == View.GONE) {
                        loadMore.setVisibility(View.VISIBLE);
                        fetchFruitPage(page + 1);
                    }

                }
            }
        });
    }

    private void initUi() {
        loadMore = findViewById(R.id.loadMore);
        searchFruit = findViewById(R.id.searchFruit);
        filterPrice = findViewById(R.id.filterPrice);
        nestedScrollView = findViewById(R.id.nestScrollView);
        recyclerView = findViewById(R.id.recycle_fruits);
        fabAddFruit = findViewById(R.id.fabAddFruit);

        adapter = new FruitAdapter(list, this, new FruitAdapter.OnFruitClickListener() {
            @Override
            public void onDetail(Fruit fruit) {
                Intent intent = new Intent(Lab7Activity.this, FruitDetailActivity.class);
                intent.putExtra("id", fruit.get_id());
                startActivity(intent);
            }

            @Override
            public void onUpdate(Fruit fruit) {
                showUpdateFruitDialog(fruit);
            }

            @Override
            public void onDelete(Fruit fruit) {
                confirmDeleteFruit(fruit);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    Callback<Response<Page<ArrayList<Fruit>>>> getListFruitResponse = new Callback<Response<Page<ArrayList<Fruit>>>>() {
        @Override
        public void onResponse(Call<Response<Page<ArrayList<Fruit>>>> call, retrofit2.Response<Response<Page<ArrayList<Fruit>>>> response) {
            isLoading = false;
            loadMore.setVisibility(View.GONE);

            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(Lab7Activity.this, "Không lấy được danh sách: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                Log.e(">>> getListFruit", "HTTP error: " + response.code());
                return;
            }

            Response<Page<ArrayList<Fruit>>> body = response.body();
            if (body.getStatus() == 200 && body.getData() != null) {
                page = body.getData().getCurrentPage();
                totalPage = body.getData().getTotalPage();
                ArrayList<Fruit> ds = body.getData().getData();
                getData(ds);
            } else {
                Toast.makeText(Lab7Activity.this, body.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Page<ArrayList<Fruit>>>> call, Throwable throwable) {
            isLoading = false;
            loadMore.setVisibility(View.GONE);
            Log.e(">>> getListFruit", "onFailure" + throwable.getMessage());
            Toast.makeText(Lab7Activity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

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
                    .load(getImageUrl(fruit.getImage().get(0)))
                    .into(imgFruit);
        }

        edtName.setText(fruit.getName());
        edtQuantity.setText(String.valueOf(fruit.getQuantity()));
        edtPrice.setText(String.valueOf(fruit.getPrice()));
        edtDescription.setText(fruit.getDescription());
        btnUpdate.setText("Cập nhật");

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
                Toast.makeText(Lab7Activity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            int price;

            try {
                quantity = Integer.parseInt(newQuantity);
                price = Integer.parseInt(newPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(Lab7Activity.this, "Số lượng và giá phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            Fruit updateFruit = new Fruit();
            updateFruit.setName(newName);
            updateFruit.setQuantity(quantity);
            updateFruit.setPrice(price);
            updateFruit.setStatus(fruit.getStatus());
            updateFruit.setImage(fruit.getImage());
            updateFruit.setDescription(newDescription);

            httpRequest.callAPI()
                    .updateFruit(fruit.get_id(), updateFruit)
                    .enqueue(new Callback<Response<Fruit>>() {
                        @Override
                        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                            if (!response.isSuccessful() || response.body() == null) {
                                Toast.makeText(Lab7Activity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(Lab7Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus() == 200) {
                                dialog.dismiss();
                                page = 1;
                                fetchFruitPage(page);
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<Fruit>> call, Throwable throwable) {
                            Toast.makeText(Lab7Activity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Lab7Activity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(Lab7Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        if (response.body().getStatus() == 200) {
                            page = 1;
                            fetchFruitPage(page);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<Fruit>> call, Throwable throwable) {
                        Toast.makeText(Lab7Activity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getImageUrl(String image) {
        if (image == null || image.trim().isEmpty()) {
            return null;
        }
        image = image.replace("\\", "/");
        if (image.startsWith("http://localhost:3000/")) {
            return image.replace("http://localhost:3000/", "http://10.0.2.2:3000/");
        }
        if (image.startsWith("http://127.0.0.1:3000/")) {
            return image.replace("http://127.0.0.1:3000/", "http://10.0.2.2:3000/");
        }
        return image;
    }

    Callback<Response<ArrayList<Fruit>>> getListFruit = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful() && response.body() != null) {
                Response<ArrayList<Fruit>> body = response.body();
                if (body.getStatus() == 200) {
                    ArrayList<Fruit> ds = body.getData();
                    getData(ds);
                    Toast.makeText(Lab7Activity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Lab7Activity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Lab7Activity.this, "Khong lay duoc danh sach", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable throwable) {
            Log.d(">>> GetListFruit", "onFailure: " + throwable.getMessage());
            Toast.makeText(Lab7Activity.this, "Loi ket noi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void getData(ArrayList<Fruit> ds) {
        if (ds == null) {
            ds = new ArrayList<>();
        }

        if (page == 1) {
            list.clear();
        }

        if (page > 1) {
            list.addAll(ds);
            adapter.notifyDataSetChanged();
            return;
        }

        list.addAll(ds);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (httpRequest != null && token != null && !token.isEmpty()) {
            page = 1;
            fetchFruitPage(page);
        }
    }

    private void fetchFruitPage(int nextPage) {
        isLoading = true;
        httpRequest.callAPI()
                .getPageFruit("Bearer " + token, nextPage, keyword, price, 1)
                .enqueue(getListFruitResponse);
    }
}

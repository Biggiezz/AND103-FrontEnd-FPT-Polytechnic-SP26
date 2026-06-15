package com.example.lab7;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.buoi1demo.R;
import com.example.lab7.model.Fruit;
import com.example.lab7.model.Response;
import com.example.lab7.service.HttpRequest;

import retrofit2.Call;
import retrofit2.Callback;

public class FruitDetailActivity extends AppCompatActivity {
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:3000/";

    private ImageView imgFruitDetail;
    private TextView tvFruitName, tvFruitPrice, tvFruitQuantity, tvFruitStatus, tvFruitDescription;
    private HttpRequest httpRequest;
    private String token;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail_lab7);

        initUi();
        httpRequest = new HttpRequest();

        SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        id = getIntent().getStringExtra("id");

        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy id fruit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getFruitDetail();
    }

    private void initUi() {
        imgFruitDetail = findViewById(R.id.imgFruitDetail);
        tvFruitName = findViewById(R.id.tvFruitName);
        tvFruitPrice = findViewById(R.id.tvFruitPrice);
        tvFruitQuantity = findViewById(R.id.tvFruitQuantity);
        tvFruitStatus = findViewById(R.id.tvFruitStatus);
        tvFruitDescription = findViewById(R.id.tvFruitDescription);
    }

    private void getFruitDetail() {
        httpRequest.callAPI()
                .getFruitById("Bearer " + token, id)
                .enqueue(new Callback<Response<Fruit>>() {
                    @Override
                    public void onResponse(Call<Response<Fruit>> call,
                                           retrofit2.Response<Response<Fruit>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(FruitDetailActivity.this, "Không lấy được chi tiết fruit", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Response<Fruit> body = response.body();
                        if (body.getStatus() == 200 && body.getData() != null) {
                            showFruit(body.getData());
                        } else {
                            Toast.makeText(FruitDetailActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<Fruit>> call, Throwable throwable) {
                        Toast.makeText(FruitDetailActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showFruit(Fruit fruit) {
        tvFruitName.setText(fruit.getName());
        tvFruitPrice.setText("Giá: " + fruit.getPrice());
        tvFruitQuantity.setText("Số lượng: " + fruit.getQuantity());
        tvFruitStatus.setText("Trạng thái: " + fruit.getStatus());
        tvFruitDescription.setText("Mô tả: " + fruit.getDescription());

        if (fruit.getImage() != null && !fruit.getImage().isEmpty()) {
            Glide.with(this)
                    .load(getImageUrl(fruit.getImage().get(0)))
                    .into(imgFruitDetail);
        }
    }

    private String getImageUrl(String image) {
        if (image == null || image.trim().isEmpty()) {
            return null;
        }

        image = image.replace("\\", "/");

        if (image.startsWith("http://") || image.startsWith("https://")) {
            return image
                    .replace("http://localhost:3000/", BASE_IMAGE_URL)
                    .replace("http://127.0.0.1:3000/", BASE_IMAGE_URL)
                    .replace("/upload/", "/uploads/");
        }

        if (image.startsWith("/")) {
            image = image.substring(1);
        }

        return (BASE_IMAGE_URL + image)
                .replace("/upload/", "/uploads/");
    }
}

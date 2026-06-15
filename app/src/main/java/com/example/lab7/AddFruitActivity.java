package com.example.lab7;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buoi1demo.R;
import com.example.lab7.adapter.ImageAdapter;
import com.example.lab7.adapter.SpinnerDistributorAdapter;
import com.example.lab7.model.Distributor;
import com.example.lab7.model.Fruit;
import com.example.lab7.model.Response;
import com.example.lab7.service.HttpRequest;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AddFruitActivity extends AppCompatActivity {

    private EditText edtName, edtQuantity, edtPrice, edtStatus, edtDescription;
    private MaterialButton btnAdd;
    private Spinner spinner;
    private ImageView imgFruit;
    private RecyclerView rcvImages;

    private final ArrayList<File> images = new ArrayList<>();

    private ImageAdapter imageAdapter;
    private HttpRequest httpRequest;

    private String idDistributor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_fruit_lab7);

        initUi();
        httpRequest = new HttpRequest();
        setupImageRecyclerView();
        loadDistributor();
        imgFruit.setOnClickListener(v -> chooseImage());
        btnAdd.setOnClickListener(v -> addFruit());
        updateImageView();
    }

    private void initUi() {
        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtPrice = findViewById(R.id.edtPrice);
        edtStatus = findViewById(R.id.edtStatus);
        edtDescription = findViewById(R.id.edtDescription);

        btnAdd = findViewById(R.id.addFruit);
        spinner = findViewById(R.id.spinnerFruit);

        imgFruit = findViewById(R.id.imgFruit);
        rcvImages = findViewById(R.id.rcvImages);
    }

    private void setupImageRecyclerView() {
        imageAdapter = new ImageAdapter(this, images);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rcvImages.setLayoutManager(layoutManager);
        rcvImages.setAdapter(imageAdapter);
    }

    private void loadDistributor() {
        Log.d("API", "Call getListDistributor");
        httpRequest.callAPI().getListDistributor().enqueue(getListDistributor);
    }

    private final Callback<Response<ArrayList<Distributor>>> getListDistributor = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(AddFruitActivity.this, "Không lấy được danh sách distributor", Toast.LENGTH_LONG).show();
                return;
            }
            if (response.body().getStatus() == 200 && response.body().getData() != null) {
                setupSpinnerDistributor(response.body().getData());

            } else {
                Toast.makeText(AddFruitActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable throwable) {
            Log.d("AddFruitActivity", "loadDistributor error: " + throwable.getMessage());
            Toast.makeText(AddFruitActivity.this, "Lỗi kết nối API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void setupSpinnerDistributor(ArrayList<Distributor> distributors) {
        SpinnerDistributorAdapter adapter = new SpinnerDistributorAdapter(distributors, this);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                idDistributor = distributors.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addFruit() {
        String name = edtName.getText().toString().trim();
        String quantity = edtQuantity.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String status = edtStatus.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty() || quantity.isEmpty() ||
                price.isEmpty() || status.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idDistributor == null || idDistributor.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn nhà phân phối", Toast.LENGTH_SHORT).show();
            return;
        }

        if (images.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, RequestBody> body = new HashMap<>();
        body.put("name", getRequestBody(name));
        body.put("quantity", getRequestBody(quantity));
        body.put("price", getRequestBody(price));
        body.put("status", getRequestBody(status));
        body.put("description", getRequestBody(description));
        body.put("id_distributor", getRequestBody(idDistributor));

        ArrayList<MultipartBody.Part> imageParts = getImageParts();

        httpRequest.callAPI().addFruit(body, imageParts).enqueue(responseAddFruit);
    }


    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    private ArrayList<MultipartBody.Part> getImageParts() {
        ArrayList<MultipartBody.Part> imageParts = new ArrayList<>();

        for (File file : images) {
            imageParts.add(
                    MultipartBody.Part.createFormData(
                            "image",
                            file.getName(),
                            RequestBody.create(
                                    MediaType.parse("image/*"),
                                    file
                            )
                    )
            );
        }
        return imageParts;
    }

    Callback<Response<Fruit>> responseAddFruit = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(AddFruitActivity.this, "Thêm fruit thất bại", Toast.LENGTH_LONG).show();
                return;
            }
            if (response.body().getStatus() == 200) {
                Toast.makeText(AddFruitActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(AddFruitActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable throwable) {
            Log.d("AddFruitActivity", "addFruit error: " + throwable.getMessage());
            Toast.makeText(AddFruitActivity.this, "Thêm không thành công: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void chooseImage() {
        String permission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
            return;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1
            );
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        getImage.launch(Intent.createChooser(intent, "Chọn ảnh"));
    }

    private final ActivityResultLauncher<Intent> getImage =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleImageResult
            );

    private void handleImageResult(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK) {
            return;
        }

        Intent data = result.getData();

        if (data == null) {
            Toast.makeText(this, "Không lấy được ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();

            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();

                File file = createFileFromUri(
                        imageUri,
                        "image_" + System.currentTimeMillis() + "_" + i
                );

                if (file != null) {
                    images.add(file);
                }
            }

        } else if (data.getData() != null) {
            Uri imageUri = data.getData();

            File file = createFileFromUri(
                    imageUri,
                    "image_" + System.currentTimeMillis()
            );

            if (file != null) {
                images.add(file);
            }
        }

        updateImageView();
    }

    private File createFileFromUri(Uri uri, String name) {
        File file = new File(getCacheDir(), name + ".png");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                return null;
            }

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo file ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            return null;
        }
    }

    private void updateImageView() {
        imageAdapter.setData(images);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openGallery();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.ChamLab;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ChamLab.adapter.BookAdapter;
import com.example.ChamLab.handle.BookHandle;
import com.example.ChamLab.model.Book;
import com.example.ChamLab.model.Response;
import com.example.ChamLab.service.HttpRequest;
import com.example.buoi1demo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeBook extends AppCompatActivity {
    private RecyclerView rcvBook;
    private HttpRequest httpRequest;
    private BookAdapter adapter;
    private FloatingActionButton fabAddBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        fabAddBook.setOnClickListener(v -> addBook());
    }

    private void addBook() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_book, null);
        AlertDialog builder = new AlertDialog.Builder(this).setView(view).create();
        EditText edtTenSach = view.findViewById(R.id.edtTenSach);
        EditText edtTacGia = view.findViewById(R.id.edtTacGia);
        EditText edtNamXb = view.findViewById(R.id.edtNamXb);
        MaterialButton btn_cancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        btn_cancel.setOnClickListener(v -> builder.dismiss());
        btnAdd.setOnClickListener(v -> {
            String tenSach = edtTenSach.getText().toString();
            String tacGia = edtTacGia.getText().toString();
            String namXb = edtNamXb.getText().toString();
            if (tenSach.isEmpty() || tacGia.isEmpty() || namXb.isEmpty()) {
                Toast.makeText(HomeBook.this, "Vui long nhap du thong tin", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                Integer.parseInt(namXb);
            } catch (NumberFormatException e) {
                Toast.makeText(HomeBook.this, "Nam xuat ban phai la so", Toast.LENGTH_LONG).show();
                return;
            }
            httpRequest.callAPI().addBook(new Book(tenSach, tacGia, namXb)).enqueue(addBookAPI);
            builder.dismiss();
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListBook();
    }

    private void getListBook() {
        httpRequest.callAPI().getListBook().enqueue(getListBookAPI);
    }

    Callback<Response<ArrayList<Book>>> getListBookAPI = new Callback<Response<ArrayList<Book>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Book>>> call, retrofit2.Response<Response<ArrayList<Book>>> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().getStatus() == 200) {
                    Response<ArrayList<Book>> ds = response.body();
                    getData(ds.getData());
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(HomeBook.this, "Khong lay duoc danh sach sach", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Book>>> call, Throwable throwable) {
            Toast.makeText(HomeBook.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    Callback<Response<Book>> addBookAPI = new Callback<Response<Book>>() {
        @Override
        public void onResponse(Call<Response<Book>> call, retrofit2.Response<Response<Book>> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().getStatus() == 200) {
                    httpRequest.callAPI().getListBook().enqueue(getListBookAPI);
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeBook.this, "Them sach that bai", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Book>> call, Throwable throwable) {
            Log.d(">>> AddBook", "onFailure: " + throwable.getMessage());

        }
    };

    private void initUi() {
        rcvBook = findViewById(R.id.rcvBook);
        fabAddBook = findViewById(R.id.fabAddBook);
        httpRequest = new HttpRequest();
        ArrayList<Book> list = new ArrayList<>();
        adapter = new BookAdapter(this, list, new BookHandle() {
            @Override
            public void onUpdateBook(int position) {
                showUpdateDialog(list.get(position));
            }

            @Override
            public void onDeleteBook(int position) {
                Book book = list.get(position);
                httpRequest.callAPI().deleteBook(book.getId()).enqueue(deleteBookAPI);
            }
        });

        rcvBook.setLayoutManager(new LinearLayoutManager(this));
        rcvBook.setAdapter(adapter);
    }


    private void showUpdateDialog(Book book) {
        View view = getLayoutInflater()
                .inflate(R.layout.dialog_add_book, null);

        AlertDialog builder = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        TextView tvDialog = view.findViewById(R.id.tvDialog);
        tvDialog.setText("Cap nhat sach");
        TextInputEditText edtTenSach = view.findViewById(R.id.edtTenSach);
        TextInputEditText edtTacGia = view.findViewById(R.id.edtTacGia);
        TextInputEditText edtNamXb = view.findViewById(R.id.edtNamXb);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setText("Cap nhat");
        edtTenSach.setText(book.getTenSach());
        edtTacGia.setText(book.getTacGia());
        edtNamXb.setText(book.getNamXb());
        btnAdd.setOnClickListener(v -> {
            String tenSach = edtTenSach.getText().toString();
            String tacGia = edtTacGia.getText().toString();
            String namXb = edtNamXb.getText().toString();
            if (tenSach.isEmpty() || tacGia.isEmpty() || namXb.isEmpty()) {
                Toast.makeText(this, "Vui long khong bo trong", Toast.LENGTH_LONG).show();
                return;
            }
            int namXbInt = Integer.parseInt(namXb);
            Book updateBook1 = new Book(tenSach, tacGia, namXbInt);
            httpRequest.callAPI().updateBook(book.getId(), updateBook1).enqueue(updateBookAPI);
            builder.dismiss();
        });
        btnCancel.setOnClickListener(v -> builder.dismiss());
        builder.show();
    }


    Callback<Response<Book>> updateBookAPI = new Callback<Response<Book>>() {
        @Override
        public void onResponse(Call<Response<Book>> call, retrofit2.Response<Response<Book>> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().getStatus() == 200) {
                    httpRequest.callAPI().getListBook().enqueue(getListBookAPI);
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeBook.this, "Cập nhật sach that bai", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Book>> call, Throwable throwable) {
            Log.e("UpdateBookAPI", "Error: " + throwable.getMessage());

            Toast.makeText(
                    HomeBook.this,
                    "Không kết nối được server",
                    Toast.LENGTH_SHORT
            ).show();
        }
    };

    Callback<Response<Book>> deleteBookAPI = new Callback<Response<Book>>() {
        @Override
        public void onResponse(Call<Response<Book>> call, retrofit2.Response<Response<Book>> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().getStatus() == 200) {
                    httpRequest.callAPI().getListBook().enqueue(getListBookAPI);
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeBook.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeBook.this, "Xóa sách that bai", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Book>> call, Throwable throwable) {
            Log.e("DeleteBookAPI", "Error: " + throwable.getMessage());

            Toast.makeText(
                    HomeBook.this,
                    "Không kết nối được server",
                    Toast.LENGTH_SHORT
            ).show();
        }
    };

    private void getData(ArrayList<Book> ds) {
        adapter.setData(ds);
    }
}

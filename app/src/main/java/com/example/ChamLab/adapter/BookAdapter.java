package com.example.ChamLab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ChamLab.handle.BookHandle;
import com.example.ChamLab.model.Book;
import com.example.buoi1demo.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Book> list;
    private BookHandle handle;

    public BookAdapter(Context context, ArrayList<Book> list, BookHandle handle) {
        this.context = context;
        this.list = list;
        this.handle = handle;
    }

    public void setData(ArrayList<Book> list) {
        this.list.clear();
        if (list != null) {
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        Book book = list.get(position);
        holder.tvId.setText("ID: " + getText(book.getId()));
        holder.tvTenSach.setText("Ten Sach: " + getText(book.getTenSach()));
        holder.tvTacGia.setText("Ten Tac Gia: " + getText(book.getTacGia()));
        holder.tvNamXb.setText("Nam xuat ban: " + String.valueOf(book.getNamXb()));
        holder.btnUpdate.setOnClickListener(v -> handle.onDeleteBook(position));
        holder.btnDelete.setOnClickListener(v -> handle.onDeleteBook(position));
    }

    private String getText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Chua co du lieu";
        }
        return text;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvTenSach;
        private TextView tvTacGia;
        private TextView tvNamXb;
        private MaterialButton btnUpdate;
        private MaterialButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTenSach = itemView.findViewById(R.id.tvTenSach);
            tvTacGia = itemView.findViewById(R.id.tvTacGia);
            tvNamXb = itemView.findViewById(R.id.tvNamXb);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

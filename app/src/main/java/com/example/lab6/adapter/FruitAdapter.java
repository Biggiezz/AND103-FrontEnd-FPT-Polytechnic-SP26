package com.example.lab6.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buoi1demo.R;
import com.example.lab6.model.Fruit;

import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitViewHolder> {
    private final ArrayList<Fruit> list;
    private final Context context;
    private final OnFruitClickListener listener;
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:3000/";


    public FruitAdapter(ArrayList<Fruit> list, Context context, OnFruitClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public void setData(ArrayList<Fruit> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public FruitAdapter.FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fruit, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitAdapter.FruitViewHolder holder, int position) {
        Fruit fruit = list.get(position);
        holder.tvName.setText("Tên: " + fruit.getName());
        holder.tvPrice.setText("Giá: " + String.valueOf(fruit.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + String.valueOf(fruit.getQuantity()));
        holder.tvDescription.setText("Mô tả: " + fruit.getDescription());
        Glide.with(context)
                .load(getFirstImageUrl(fruit))
                .into(holder.image);

        holder.imgEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdate(fruit);
            }
        });

        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(fruit);
            }
        });
    }

    private String getFirstImageUrl(Fruit fruit) {
        if (fruit.getImage() == null || fruit.getImage().isEmpty()) {
            return null;
        }

        String image = fruit.getImage().get(0);

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

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class FruitViewHolder extends RecyclerView.ViewHolder {
        ImageView image, imgEdit, imgDelete;
        TextView tvName, tvPrice, tvQuantity, tvDescription;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgFruit);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }

    public interface OnFruitClickListener {
        void onUpdate(Fruit fruit);

        void onDelete(Fruit fruit);
    }
}

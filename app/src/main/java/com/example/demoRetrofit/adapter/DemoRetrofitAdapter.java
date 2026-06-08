package com.example.demoRetrofit.adapter;

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
import com.example.demoRetrofit.handle.Item_User_Handle;
import com.example.demoRetrofit.model.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class DemoRetrofitAdapter extends RecyclerView.Adapter<DemoRetrofitAdapter.ViewHolder> {
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:3000/";

    private final ArrayList<User> list;
    private final Context context;
    private final Item_User_Handle itemUserHandle;

    public DemoRetrofitAdapter(ArrayList<User> list, Context context, Item_User_Handle itemUserHandle) {
        this.list = list;
        this.context = context;
        this.itemUserHandle = itemUserHandle;
    }

    @NonNull
    @Override
    public DemoRetrofitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_demo_retrofit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoRetrofitAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.tvUsername.setText("Họ và tên: " + user.getUsername());
        holder.tvAge.setText("Tuổi: " + String.valueOf(user.getAge()));
        holder.tvAdress.setText("Địa chỉ: " + user.getAdress());
        String imageUrl = getImageUrl(user.getImage());

        if (imageUrl == null) {
            holder.imgAvatar.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext()).clear(holder.imgAvatar);
        } else {
            holder.imgAvatar.setVisibility(View.VISIBLE);

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.bg_warning_pill)
                    .error(R.drawable.bg_warning_pill)
                    .into(holder.imgAvatar);
        }

        holder.btnAdd.setText("Update");
        /// sự kiện

        holder.btnAdd.setOnClickListener(v -> {
            itemUserHandle.onEdit(position);
        });
        holder.btnRemove.setOnClickListener(v -> {
            itemUserHandle.onDelete(position);
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private String getImageUrl(String image) {
        if (image == null || image.trim().isEmpty()) {
            return null;
        }

        image = image.trim().replace("\\", "/");
        if (image.startsWith("http://") || image.startsWith("https://")) {
            return image
                    .replace("http://localhost:3000/", BASE_IMAGE_URL)
                    .replace("http://127.0.0.1:3000/", BASE_IMAGE_URL);
        }
        if (image.startsWith("/")) {
            image = image.substring(1);
        }
        return BASE_IMAGE_URL + image;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;
        private final TextView tvAge;
        private final TextView tvAdress;
        private final ImageView imgAvatar;
        private final MaterialButton btnAdd;
        private final MaterialButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvAdress = itemView.findViewById(R.id.tvAdress);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}

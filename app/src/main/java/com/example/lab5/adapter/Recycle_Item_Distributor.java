package com.example.lab5.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buoi1demo.R;
import com.example.lab5.model.Distributor;
import com.example.lab5.model.Response;
import com.example.lab5.service.HttpRequest;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class Recycle_Item_Distributor extends RecyclerView.Adapter<Recycle_Item_Distributor.ViewHolder> {
    private final Context context;
    private final ArrayList<Distributor> list;

    public Recycle_Item_Distributor(Context context, ArrayList<Distributor> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_distributor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Distributor distributor = list.get(position);

        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvName.setText(distributor.getName());

        holder.itemView.setOnClickListener(v -> {

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_add_distributor, null);


            EditText edt_dialog_name = view.findViewById(R.id.edt_dialog_name);
            Button btn_add = view.findViewById(R.id.btn_add);


            btn_add.setText("Update");

            // hiển thị tên cũ
            edt_dialog_name.setText(distributor.getName());

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .create();

            btn_add.setOnClickListener(btn -> {

                String newName = edt_dialog_name.getText().toString().trim();

                if (newName.isEmpty()) {
                    edt_dialog_name.setError("Không được để trống");
                    return;
                }

                // tạo object mới để update
                Distributor updateDistributor = new Distributor();
                updateDistributor.setName(newName);

                HttpRequest httpRequest = new HttpRequest();

                httpRequest.callAPI()
                        .updateDistributorById(
                                distributor.getId(),
                                updateDistributor
                        )
                        .enqueue(new Callback<Response<Distributor>>() {

                            @Override
                            public void onResponse(
                                    Call<Response<Distributor>> call,
                                    retrofit2.Response<Response<Distributor>> response) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    Response<Distributor> body = response.body();

                                    Toast.makeText(context, body.getMessage(), Toast.LENGTH_SHORT).show();

                                    if (body.getStatus() == 200) {

                                        int currentPosition = holder.getAdapterPosition();

                                        if (currentPosition != RecyclerView.NO_POSITION) {

                                            // cập nhật item trong list
                                            list.get(currentPosition).setName(newName);

                                            // refresh item
                                            notifyItemChanged(currentPosition);
                                        }
                                        dialog.dismiss();
                                    }

                                } else {
                                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Response<Distributor>> call, Throwable t) {
                                Toast.makeText(context, "Lỗi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            dialog.show();
        });
        holder.imgDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Xóa nhà phân phối")
                    .setMessage("Bạn có muốn xóa không?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {

                        HttpRequest httpRequest = new HttpRequest();

                        httpRequest.callAPI()
                                .deleteDistributorById(distributor.getId())
                                .enqueue(new Callback<Response<Distributor>>() {

                                    @Override
                                    public void onResponse(
                                            Call<Response<Distributor>> call,
                                            retrofit2.Response<Response<Distributor>> response) {

                                        if (response.isSuccessful() && response.body() != null) {

                                            Response<Distributor> body = response.body();

                                            Toast.makeText(context, body.getMessage(), Toast.LENGTH_SHORT).show();

                                            if (body.getStatus() == 200) {

                                                int currentPosition = holder.getAdapterPosition();

                                                if (currentPosition != RecyclerView.NO_POSITION) {

                                                    list.remove(currentPosition);

                                                    notifyItemRemoved(currentPosition);

                                                    notifyItemRangeChanged(currentPosition, list.size());
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<Response<Distributor>> call,
                                            Throwable t) {

                                        Toast.makeText(context, "Lỗi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    })
                    .setNegativeButton("Hủy", null).show();
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStt, tvName;
        ImageView imgDelete;
        MaterialCardView materialCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.stt);
            tvName = itemView.findViewById(R.id.name);
            imgDelete = itemView.findViewById(R.id.ic_delete);
            materialCardView = itemView.findViewById(R.id.materialCardView);

        }
    }
}

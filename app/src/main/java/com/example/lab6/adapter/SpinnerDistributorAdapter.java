package com.example.lab6.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.buoi1demo.R;
import com.example.lab6.model.Distributor;

import java.util.ArrayList;

public class SpinnerDistributorAdapter extends BaseAdapter {
    private ArrayList<Distributor> list;
    private Context context;

    public SpinnerDistributorAdapter(ArrayList<Distributor> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.spinner_distributor, null);
        }
        TextView tvStt = view.findViewById(R.id.tvStt);
        TextView tvDistributor = view.findViewById(R.id.tvDistributor);
        tvStt.setText(list.get(position).getId());
        tvDistributor.setText(list.get(position).getName());
        return view;
    }
}

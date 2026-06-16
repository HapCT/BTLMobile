package com.example.btlmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlmobile.R;
import com.example.btlmobile.models.TaiKhoan;

import java.util.ArrayList;

public class TaiKhoanAdapter extends RecyclerView.Adapter<TaiKhoanAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TaiKhoan> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(TaiKhoan tk);
    }

    public TaiKhoanAdapter(Context context, ArrayList<TaiKhoan> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tai_khoan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaiKhoan tk = list.get(position);
        holder.txtTenDN.setText(tk.getTenDN());
        holder.txtEmail.setText(tk.getEmail());
        holder.txtTrangThai.setText(tk.getTrangThai());
        if ("Hoạt động".equals(tk.getTrangThai())) {
            holder.txtTrangThai.setTextColor(Color.GREEN);
        } else {
            holder.txtTrangThai.setTextColor(Color.RED);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(tk));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<TaiKhoan> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenDN, txtEmail, txtTrangThai;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenDN = itemView.findViewById(R.id.txtTenDN);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

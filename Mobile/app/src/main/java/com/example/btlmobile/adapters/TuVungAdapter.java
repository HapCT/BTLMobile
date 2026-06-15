package com.example.btlmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.btlmobile.R;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.TuVung;

import android.content.SharedPreferences;
import android.widget.TextView;

import java.util.List;

public class TuVungAdapter extends ArrayAdapter<TuVung> {
    private Context context;
    private int resource;
    private List<TuVung> objects;
    private TuVungDAO tuVungDAO;
    private int taiKhoanId;

    public TuVungAdapter(Context context, int resource, List<TuVung> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.tuVungDAO = new TuVungDAO(context);
        
        // Lấy ID người dùng đang đăng nhập
        SharedPreferences pref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.taiKhoanId = pref.getInt("taiKhoanId", -1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TuVung tuVung = objects.get(position);

        ImageView ivTuVung = convertView.findViewById(R.id.ivTuVung);
        TextView tvTuVung = convertView.findViewById(R.id.tvTuVung);
        TextView tvNghia = convertView.findViewById(R.id.tvNghia);
        TextView tvPhienAm = convertView.findViewById(R.id.tvPhienAm);
        ImageButton btnFavorite = convertView.findViewById(R.id.btnFavorite);

        tvTuVung.setText(tuVung.getTuVung());
        tvNghia.setText(tuVung.getNghia());
        tvPhienAm.setText(tuVung.getPhienAm());

        // Cập nhật trạng thái ngôi sao
        if (tuVungDAO.isFavorite(taiKhoanId, tuVung.getTuVung_id())) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        btnFavorite.setOnClickListener(v -> {
            if (tuVungDAO.isFavorite(taiKhoanId, tuVung.getTuVung_id())) {
                tuVungDAO.removeFavorite(taiKhoanId, tuVung.getTuVung_id());
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                tuVungDAO.addFavorite(taiKhoanId, tuVung.getTuVung_id());
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            }
        });

        // Load image using Glide
        if (tuVung.getHinhAnh() != null && !tuVung.getHinhAnh().isEmpty()) {
            Glide.with(context)
                .load(tuVung.getHinhAnh())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(ivTuVung);
        } else {
            ivTuVung.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return convertView;
    }
}
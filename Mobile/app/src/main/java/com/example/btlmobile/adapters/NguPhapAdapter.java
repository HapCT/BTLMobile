package com.example.btlmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btlmobile.R;
import com.example.btlmobile.models.BaiHocNguPhap;

import java.util.List;

public class NguPhapAdapter extends ArrayAdapter<BaiHocNguPhap> {
    private Context context;
    private int resource;
    private List<BaiHocNguPhap> objects;

    public NguPhapAdapter(Context context, int resource, List<BaiHocNguPhap> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        BaiHocNguPhap baiHoc = objects.get(position);

        ImageView ivLevelIcon = convertView.findViewById(R.id.ivLevelIcon);
        TextView tvTieuDe = convertView.findViewById(R.id.tvTieuDe);
        TextView tvCapDo = convertView.findViewById(R.id.tvCapDo);
        TextView tvMoTaNgan = convertView.findViewById(R.id.tvMoTaNgan);

        tvTieuDe.setText(baiHoc.getTieuDe());
        tvCapDo.setText(baiHoc.getCapDo());
        
        // Thay đổi icon và màu sắc theo cấp độ để bớt "khô khan"
        String capDo = baiHoc.getCapDo() != null ? baiHoc.getCapDo().toLowerCase() : "";
        if (capDo.contains("nâng cao")) {
            ivLevelIcon.setImageResource(android.R.drawable.btn_star_big_on);
            ivLevelIcon.setColorFilter(Color.parseColor("#FFD700"));
            tvCapDo.setBackgroundColor(Color.parseColor("#FFE0B2"));
            tvCapDo.setTextColor(Color.parseColor("#E65100"));
        } else if (capDo.contains("trung cấp")) {
            ivLevelIcon.setImageResource(android.R.drawable.ic_menu_edit);
            ivLevelIcon.setColorFilter(Color.parseColor("#4CAF50"));
            tvCapDo.setBackgroundColor(Color.parseColor("#C8E6C9"));
            tvCapDo.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            ivLevelIcon.setImageResource(android.R.drawable.ic_dialog_info);
            ivLevelIcon.setColorFilter(Color.parseColor("#2196F3"));
            tvCapDo.setBackgroundColor(Color.parseColor("#BBDEFB"));
            tvCapDo.setTextColor(Color.parseColor("#1565C0"));
        }
        String content = baiHoc.getNoiDungBai();
        if (content != null && content.length() > 60) {
            tvMoTaNgan.setText(content.substring(0, 60) + "...");
        } else {
            tvMoTaNgan.setText(content);
        }

        return convertView;
    }
}

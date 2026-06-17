package com.example.btlmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.btlmobile.R;
import com.example.btlmobile.models.BaiNghe;

import java.util.List;

public class BaiNgheAdapter extends ArrayAdapter<BaiNghe> {
    private Context context;
    private int resource;
    private List<BaiNghe> objects;

    public BaiNgheAdapter(@NonNull Context context, int resource, @NonNull List<BaiNghe> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        BaiNghe bn = objects.get(position);

        TextView tvTieuDe = convertView.findViewById(R.id.tvItemTieuDe);
        TextView tvMoTa = convertView.findViewById(R.id.tvItemMoTa);
        TextView tvAudio = convertView.findViewById(R.id.tvItemAudio);

        tvTieuDe.setText(bn.getTieuDe());
        tvMoTa.setText(bn.getMoTa());
        tvAudio.setText("Audio: " + bn.getAudio());

        return convertView;
    }
}

package com.example.btlmobile.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.adapters.TuVungAdapter;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.TuVung;

import java.util.ArrayList;

public class TuVungYeuThichActivity extends AppCompatActivity {
    private ListView lvTuVungYeuThich;
    private TextView tvNoData;
    private TuVungDAO tuVungDAO;
    private ArrayList<TuVung> listFavorite;
    private TuVungAdapter adapter;
    private int taiKhoanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tu_vung_yeu_thich);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Từ vựng đã lưu");
        }

        taiKhoanId = getIntent().getIntExtra("taiKhoanId", -1);
        tuVungDAO = new TuVungDAO(this);
        
        lvTuVungYeuThich = findViewById(R.id.lvTuVungYeuThich);
        tvNoData = findViewById(R.id.tvNoData);

        loadData();
    }

    private void loadData() {
        listFavorite = tuVungDAO.getFavoriteTuVung(taiKhoanId);
        if (listFavorite.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            lvTuVungYeuThich.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            lvTuVungYeuThich.setVisibility(View.VISIBLE);
            adapter = new TuVungAdapter(this, R.layout.item_tu_vung, listFavorite);
            lvTuVungYeuThich.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách nếu người dùng bỏ yêu thích ở màn hình này
        loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

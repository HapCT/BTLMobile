package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btlmobile.R;
import com.example.btlmobile.adapters.NguPhapAdapter;
import com.example.btlmobile.dao.NguPhapDAO;
import com.example.btlmobile.models.BaiHocNguPhap;
import java.util.ArrayList;

public class HocNguPhapActivity extends AppCompatActivity {
    private ListView lvNguPhap;
    private NguPhapDAO nguPhapDAO;
    private ArrayList<BaiHocNguPhap> listNguPhap;
    private NguPhapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoc_ngu_phap);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Học Ngữ Pháp");
        }

        nguPhapDAO = new NguPhapDAO(this);
        lvNguPhap = findViewById(R.id.lvNguPhap);

        loadData();

        lvNguPhap.setOnItemClickListener((parent, view, position, id) -> {
            BaiHocNguPhap selected = listNguPhap.get(position);
            Intent intent = new Intent(HocNguPhapActivity.this, NguPhapDetailActivity.class);
            intent.putExtra("tieuDe", selected.getTieuDe());
            intent.putExtra("noiDungBai", selected.getNoiDungBai());
            intent.putExtra("capDo", selected.getCapDo());
            startActivity(intent);
        });
    }

    private void loadData() {
        listNguPhap = nguPhapDAO.getAllBaiHoc();
        adapter = new NguPhapAdapter(this, R.layout.item_ngu_phap, listNguPhap);
        lvNguPhap.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

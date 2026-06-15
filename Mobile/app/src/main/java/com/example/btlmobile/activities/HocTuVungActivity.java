package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btlmobile.R;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.ChuDeTuVung;
import java.util.ArrayList;

public class HocTuVungActivity extends AppCompatActivity {
    private ListView lvChuDeHoc;
    private TuVungDAO tuVungDAO;
    private ArrayList<ChuDeTuVung> listChuDe;
    private ArrayAdapter<ChuDeTuVung> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoc_tu_vung);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Học Từ Vựng");
        }

        tuVungDAO = new TuVungDAO(this);
        lvChuDeHoc = findViewById(R.id.lvChuDeHoc);

        loadData();

        lvChuDeHoc.setOnItemClickListener((parent, view, position, id) -> {
            ChuDeTuVung selected = listChuDe.get(position);
            Intent intent = new Intent(HocTuVungActivity.this, HocChiTietTuVungActivity.class);
            intent.putExtra("chuDeId", selected.getChuDeTuVung_id());
            intent.putExtra("tenChuDe", selected.getTenChuDe());
            startActivity(intent);
        });
    }

    private void loadData() {
        listChuDe = tuVungDAO.getAllChuDe();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listChuDe);
        lvChuDeHoc.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

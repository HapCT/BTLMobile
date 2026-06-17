package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
            searchView.setQueryHint("Tìm kiếm ngữ pháp...");
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    performSearch(newText);
                    return true;
                }
            });
        }
        return true;
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadData();
        } else {
            listNguPhap = nguPhapDAO.searchNguPhap(query);
            adapter = new NguPhapAdapter(this, R.layout.item_ngu_phap, listNguPhap);
            lvNguPhap.setAdapter(adapter);
        }
    }
}

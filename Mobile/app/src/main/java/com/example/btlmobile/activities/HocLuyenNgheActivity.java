package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.btlmobile.R;
import com.example.btlmobile.adapters.BaiNgheAdapter;
import com.example.btlmobile.dao.BaiNgheDAO;
import com.example.btlmobile.models.BaiNghe;

import java.util.ArrayList;

public class HocLuyenNgheActivity extends AppCompatActivity {

    private ListView lvHocBaiNghe;
    private BaiNgheDAO baiNgheDAO;
    private ArrayList<BaiNghe> listBaiNghe;
    private BaiNgheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoc_luyen_nghe);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Luyện nghe Tiếng Anh");
        }

        lvHocBaiNghe = findViewById(R.id.lvHocBaiNghe);
        baiNgheDAO = new BaiNgheDAO(this);

        loadData();

        lvHocBaiNghe.setOnItemClickListener((parent, view, position, id) -> {
            BaiNghe selected = listBaiNghe.get(position);
            Intent intent = new Intent(HocLuyenNgheActivity.this, NgheChiTietActivity.class);
            intent.putExtra("baiNghe", selected);
            startActivity(intent);
        });
    }

    private void loadData() {
        listBaiNghe = baiNgheDAO.getAllBaiNghe();
        adapter = new BaiNgheAdapter(this, R.layout.item_bai_nghe, listBaiNghe);
        lvHocBaiNghe.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Tìm bài nghe...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { return false; }
                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        }
        return true;
    }

    private void filter(String text) {
        ArrayList<BaiNghe> filteredList = new ArrayList<>();
        for (BaiNghe item : listBaiNghe) {
            if (item.getTieuDe().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

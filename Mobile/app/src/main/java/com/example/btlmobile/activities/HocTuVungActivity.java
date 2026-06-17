package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        setContentView(R.layout.hoctuvungui);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
            searchView.setQueryHint("Tìm kiếm chủ đề...");
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
            listChuDe = tuVungDAO.searchChuDe(query);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listChuDe);
            lvChuDeHoc.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

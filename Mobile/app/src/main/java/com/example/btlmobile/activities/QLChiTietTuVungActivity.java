package com.example.btlmobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.adapters.TuVungAdapter;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.TuVung;

import java.util.ArrayList;

public class QLChiTietTuVungActivity extends AppCompatActivity {

    private ListView lvTuVung;
    private Button btnThemTuVung;
    private TextView tvTenChuDe;
    private TuVungDAO tuVungDAO;
    private ArrayList<TuVung> listTuVung;
    private TuVungAdapter adapter;
    private int chuDeId;
    private String tenChuDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlchitiettuvungui);

        chuDeId = getIntent().getIntExtra("chuDeId", -1);
        tenChuDe = getIntent().getStringExtra("tenChuDe");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý từ vựng");
        }
        tuVungDAO = new TuVungDAO(this);
        initViews();
        loadData();
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
            searchView.setQueryHint("Tìm kiếm từ vựng...");
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
            listTuVung = tuVungDAO.searchTuVung(chuDeId, query);
            adapter = new TuVungAdapter(this, R.layout.item_tu_vung, listTuVung);
            lvTuVung.setAdapter(adapter);
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
        // Xóa phiên đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        lvTuVung = findViewById(R.id.lvTuVung);
        btnThemTuVung = findViewById(R.id.btnThemTuVung);
        tvTenChuDe = findViewById(R.id.tvTenChuDe);

        tvTenChuDe.setText("Chủ đề: " + tenChuDe);

        btnThemTuVung.setOnClickListener(v -> showAddEditDialog(null));

        lvTuVung.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(listTuVung.get(position));
            return true;
        });
    }

    private void loadData() {
        listTuVung = tuVungDAO.getTuVungByChuDe(chuDeId);
        adapter = new TuVungAdapter(this, R.layout.item_tu_vung, listTuVung);
        lvTuVung.setAdapter(adapter);
    }

    private void showOptionsDialog(TuVung tuVung) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(tuVung.getTuVung())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddEditDialog(tuVung);
                    } else {
                        tuVungDAO.deleteTuVung(tuVung.getTuVung_id());
                        loadData();
                    }
                }).show();
    }

    private void showAddEditDialog(TuVung tuVung) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_tuvung, null);
        EditText etTuVung = view.findViewById(R.id.etTuVung);
        EditText etNghia = view.findViewById(R.id.etNghia);
        EditText etPhienAm = view.findViewById(R.id.etPhienAm);
        EditText etHinhAnh = view.findViewById(R.id.etHinhAnh);
        
        if (tuVung != null) {
            etTuVung.setText(tuVung.getTuVung());
            etNghia.setText(tuVung.getNghia());
            etPhienAm.setText(tuVung.getPhienAm());
            etHinhAnh.setText(tuVung.getHinhAnh());
            builder.setTitle("Sửa Từ vựng");
        } else {
            builder.setTitle("Thêm Từ vựng");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String word = etTuVung.getText().toString().trim();
            String mean = etNghia.getText().toString().trim();
            String phon = etPhienAm.getText().toString().trim();
            String img = etHinhAnh.getText().toString().trim();
            
            if (word.isEmpty() || mean.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (tuVung == null) {
                TuVung newTV = new TuVung();
                newTV.setChuDeTuVung_id(chuDeId);
                newTV.setTuVung(word);
                newTV.setNghia(mean);
                newTV.setPhienAm(phon);
                newTV.setHinhAnh(img);
                long res = tuVungDAO.insertTuVung(newTV);
                if (res > 0) {
                    Toast.makeText(this, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Thêm từ vựng thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                tuVung.setTuVung(word);
                tuVung.setNghia(mean);
                tuVung.setPhienAm(phon);
                tuVung.setHinhAnh(img);
                int res = tuVungDAO.updateTuVung(tuVung);
                if (res > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

package com.example.btlmobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.ChuDeTuVung;

import java.util.ArrayList;

public class QLTuVungActivity extends AppCompatActivity {

    private ListView lvChuDe;
    private Button btnThemChuDe;
    private TuVungDAO tuVungDAO;
    private ArrayList<ChuDeTuVung> listChuDe;
    private ArrayAdapter<ChuDeTuVung> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qltuvungui);
        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý chủ đề từ vựng");
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
        return true;
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
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        lvChuDe = findViewById(R.id.lvChuDe);
        btnThemChuDe = findViewById(R.id.btnThemChuDe);

        btnThemChuDe.setOnClickListener(v -> showAddEditDialog(null));

        lvChuDe.setOnItemClickListener((parent, view, position, id) -> {
            ChuDeTuVung selected = listChuDe.get(position);
            Intent intent = new Intent(this, QLChiTietTuVungActivity.class);
            intent.putExtra("chuDeId", selected.getChuDeTuVung_id());
            intent.putExtra("tenChuDe", selected.getTenChuDe());
            startActivity(intent);
        });

        lvChuDe.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(listChuDe.get(position));
            return true;
        });
    }

    private void loadData() {
        listChuDe = tuVungDAO.getAllChuDe();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listChuDe);
        lvChuDe.setAdapter(adapter);
    }

    private void showOptionsDialog(ChuDeTuVung chuDe) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(chuDe.getTenChuDe())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddEditDialog(chuDe);
                    } else {
                        tuVungDAO.deleteChuDe(chuDe.getChuDeTuVung_id());
                        loadData();
                    }
                }).show();
    }

    private void showAddEditDialog(ChuDeTuVung chuDe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_chude, null);
        EditText etTenChuDe = view.findViewById(R.id.etTenChuDe);
        
        if (chuDe != null) {
            etTenChuDe.setText(chuDe.getTenChuDe());
            builder.setTitle("Sửa Chủ đề");
        } else {
            builder.setTitle("Thêm Chủ đề");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ten = etTenChuDe.getText().toString().trim();
            if (ten.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên chủ đề", Toast.LENGTH_SHORT).show();
                return;
            }
            if (chuDe == null) {
                ChuDeTuVung newChuDe = new ChuDeTuVung();
                newChuDe.setTenChuDe(ten);
                long res = tuVungDAO.insertChuDe(newChuDe);
                if (res > 0) {
                    Toast.makeText(this, "Thêm chủ đề thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm chủ đề thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                chuDe.setTenChuDe(ten);
                tuVungDAO.updateChuDe(chuDe);
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
            loadData();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

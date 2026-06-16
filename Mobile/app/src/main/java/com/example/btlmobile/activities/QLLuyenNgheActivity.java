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
import com.example.btlmobile.dao.BaiNgheDAO;
import com.example.btlmobile.models.BaiNghe;

import java.util.ArrayList;

public class QLLuyenNgheActivity extends AppCompatActivity {

    private ListView lvBaiNghe;
    private Button btnThemBaiNghe;
    private BaiNgheDAO baiNgheDAO;
    private ArrayList<BaiNghe> listBaiNghe;
    private ArrayAdapter<BaiNghe> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_luyen_nghe);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý luyện nghe");
        }
        baiNgheDAO = new BaiNgheDAO(this);
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
        lvBaiNghe = findViewById(R.id.lvBaiNghe);
        btnThemBaiNghe = findViewById(R.id.btnThemBaiNghe);

        btnThemBaiNghe.setOnClickListener(v -> showAddEditDialog(null));

        lvBaiNghe.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(listBaiNghe.get(position));
            return true;
        });
    }

    private void loadData() {
        listBaiNghe = baiNgheDAO.getAllBaiNghe();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listBaiNghe);
        lvBaiNghe.setAdapter(adapter);
    }

    private void showOptionsDialog(BaiNghe bn) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(bn.getTieuDe())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddEditDialog(bn);
                    } else {
                        baiNgheDAO.deleteBaiNghe(bn.getBaiNghe_id());
                        loadData();
                        Toast.makeText(this, "Đã xóa bài nghe", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void showAddEditDialog(BaiNghe bn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_bainghe, null);
        EditText etTieuDe = view.findViewById(R.id.etTieuDe);
        EditText etMoTa = view.findViewById(R.id.etMoTa);
        EditText etAudio = view.findViewById(R.id.etAudio);

        if (bn != null) {
            etTieuDe.setText(bn.getTieuDe());
            etMoTa.setText(bn.getMoTa());
            etAudio.setText(bn.getAudio());
            builder.setTitle("Sửa Bài nghe");
        } else {
            builder.setTitle("Thêm Bài nghe");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String tieuDe = etTieuDe.getText().toString().trim();
            String moTa = etMoTa.getText().toString().trim();
            String audio = etAudio.getText().toString().trim();

            if (tieuDe.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bn == null) {
                BaiNghe newBN = new BaiNghe();
                newBN.setTieuDe(tieuDe);
                newBN.setMoTa(moTa);
                newBN.setAudio(audio);
                long res = baiNgheDAO.insertBaiNghe(newBN);
                if (res > 0) {
                    Toast.makeText(this, "Thêm bài nghe thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                bn.setTieuDe(tieuDe);
                bn.setMoTa(moTa);
                bn.setAudio(audio);
                int res = baiNgheDAO.updateBaiNghe(bn);
                if (res > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            loadData();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

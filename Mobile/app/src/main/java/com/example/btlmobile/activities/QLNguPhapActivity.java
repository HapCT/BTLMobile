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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.adapters.NguPhapAdapter;
import com.example.btlmobile.dao.NguPhapDAO;
import com.example.btlmobile.models.BaiHocNguPhap;

import java.util.ArrayList;

public class QLNguPhapActivity extends AppCompatActivity {

    private ListView lvBaiHoc;
    private Button btnThemBaiHoc;
    private NguPhapDAO nguPhapDAO;
    private ArrayList<BaiHocNguPhap> listBaiHoc;
    private NguPhapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_ngu_phap);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Ngữ pháp");
        }
        nguPhapDAO = new NguPhapDAO(this);
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
        lvBaiHoc = findViewById(R.id.lvBaiHoc);
        btnThemBaiHoc = findViewById(R.id.btnThemBaiHoc);

        btnThemBaiHoc.setOnClickListener(v -> showAddEditDialog(null));

        lvBaiHoc.setOnItemClickListener((parent, view, position, id) -> {
            BaiHocNguPhap selected = listBaiHoc.get(position);
            Intent intent = new Intent(this, NguPhapDetailActivity.class);
            intent.putExtra("tieuDe", selected.getTieuDe());
            intent.putExtra("noiDungBai", selected.getNoiDungBai());
            intent.putExtra("capDo", selected.getCapDo());
            startActivity(intent);
        });

        lvBaiHoc.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(listBaiHoc.get(position));
            return true;
        });
    }

    private void loadData() {
        listBaiHoc = nguPhapDAO.getAllBaiHoc();
        adapter = new NguPhapAdapter(this, R.layout.item_ngu_phap, listBaiHoc);
        lvBaiHoc.setAdapter(adapter);
    }

    private void showOptionsDialog(BaiHocNguPhap baiHoc) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(baiHoc.getTieuDe())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddEditDialog(baiHoc);
                    } else {
                        nguPhapDAO.deleteBaiHoc(baiHoc.getBaiHocNP_id());
                        loadData();
                    }
                }).show();
    }

    private void showAddEditDialog(BaiHocNguPhap baiHoc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_nguphap, null);
        EditText etTieuDe = view.findViewById(R.id.etTieuDe);
        EditText etNoiDungBai = view.findViewById(R.id.etNoiDungBai);
        EditText etCapDo = view.findViewById(R.id.etCapDo);
        
        if (baiHoc != null) {
            etTieuDe.setText(baiHoc.getTieuDe());
            etNoiDungBai.setText(baiHoc.getNoiDungBai());
            etCapDo.setText(baiHoc.getCapDo());
            builder.setTitle("Sửa Bài học");
        } else {
            builder.setTitle("Thêm Bài học");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String tieuDe = etTieuDe.getText().toString().trim();
            String noiDungBai = etNoiDungBai.getText().toString().trim();
            String capDo = etCapDo.getText().toString().trim();
            if (tieuDe.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }
            if (baiHoc == null) {
                BaiHocNguPhap newBaiHoc = new BaiHocNguPhap();
                newBaiHoc.setTieuDe(tieuDe);
                newBaiHoc.setNoiDungBai(noiDungBai);
                newBaiHoc.setCapDo(capDo);
                nguPhapDAO.insertBaiHoc(newBaiHoc);
            } else {
                baiHoc.setTieuDe(tieuDe);
                baiHoc.setNoiDungBai(noiDungBai);
                baiHoc.setCapDo(capDo);
                nguPhapDAO.updateBaiHoc(baiHoc);
            }
            loadData();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

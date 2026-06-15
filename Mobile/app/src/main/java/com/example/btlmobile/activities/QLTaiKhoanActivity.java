package com.example.btlmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlmobile.R;
import com.example.btlmobile.adapters.TaiKhoanAdapter;
import com.example.btlmobile.dao.TaiKhoanDAO;
import com.example.btlmobile.models.TaiKhoan;

import java.util.ArrayList;

public class QLTaiKhoanActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView rvTaiKhoan;
    private TaiKhoanDAO taiKhoanDAO;
    private TaiKhoanAdapter adapter;
    private ArrayList<TaiKhoan> listTaiKhoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_tai_khoan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý tài khoản");
        }

        taiKhoanDAO = new TaiKhoanDAO(this);
        initViews();
        loadData();
        setupSearch();
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
        rvTaiKhoan = findViewById(R.id.rvTaiKhoan);
        rvTaiKhoan.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        listTaiKhoan = taiKhoanDAO.getAllTaiKhoan();
        adapter = new TaiKhoanAdapter(this, listTaiKhoan, new TaiKhoanAdapter.OnItemClickListener() {
            @Override
            public void onLockUnlockClick(TaiKhoan tk) {
                String newStatus = "Hoạt động".equals(tk.getTrangThai()) ? "Bị khóa" : "Hoạt động";
                taiKhoanDAO.updateTrangThai(tk.getTaiKhoan_id(), newStatus);
                Toast.makeText(QLTaiKhoanActivity.this, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                refreshData();
            }

            @Override
            public void onDeleteClick(TaiKhoan tk) {
                // Có thể thêm Dialog xác nhận ở đây
                taiKhoanDAO.deleteTaiKhoan(tk.getTaiKhoan_id());
                Toast.makeText(QLTaiKhoanActivity.this, "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
                refreshData();
            }
        });
        rvTaiKhoan.setAdapter(adapter);
    }

    private void refreshData() {
        String query = edtSearch.getText().toString().trim();
        if (query.isEmpty()) {
            listTaiKhoan = taiKhoanDAO.getAllTaiKhoan();
        } else {
            listTaiKhoan = taiKhoanDAO.searchTaiKhoan(query);
        }
        adapter.updateList(listTaiKhoan);
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}

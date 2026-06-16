package com.example.btlmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;

public class AdminActivities extends AppCompatActivity {

    LinearLayout btnQLTaiKhoan, btnQLTuVung, btnQLNguPhap, btnQLQuiz, btnQLLuyenNghe, btnThongKe, btnDangXuat;
    private int taiKhoanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_form);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Admin DashBoard");
        }
        taiKhoanId = getIntent().getIntExtra("taiKhoanId", -1);

        initViews();
        setClickListeners();
    }

    private void initViews() {
        btnQLTaiKhoan = findViewById(R.id.btnQLTaiKhoan);
        btnQLTuVung = findViewById(R.id.btnQLTuVung);
        btnQLNguPhap = findViewById(R.id.btnQLNguPhap);
        btnQLQuiz = findViewById(R.id.btnQLQuiz);
        btnQLLuyenNghe = findViewById(R.id.btnQLLuyenNghe);
        btnThongKe = findViewById(R.id.btnThongKe);
        btnDangXuat = findViewById(R.id.btnDangXuat);
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

    private void setClickListeners() {
        btnQLTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(this, QLTaiKhoanActivity.class);
            startActivity(intent);
        });

        btnQLTuVung.setOnClickListener(v -> {
            Intent intent = new Intent(this, QLTuVungActivity.class);
            startActivity(intent);
        });

        btnQLNguPhap.setOnClickListener(v -> {
            Intent intent = new Intent(this, QLNguPhapActivity.class);
            startActivity(intent);
        });

        btnQLQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, QLQuizActivity.class);
            startActivity(intent);
        });

        btnQLLuyenNghe.setOnClickListener(v -> {
            Intent intent = new Intent(this, QLLuyenNgheActivity.class);
            startActivity(intent);
        });

        btnThongKe.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThongKeActivity.class);
            startActivity(intent);
        });

        btnDangXuat.setOnClickListener(v -> {
            performLogout();
        });
    }
}

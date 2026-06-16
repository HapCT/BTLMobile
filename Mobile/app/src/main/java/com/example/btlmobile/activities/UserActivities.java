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

public class UserActivities extends AppCompatActivity {

    LinearLayout btnHocTuVung, btnHocNguPhap, btnLuyenNghe, btnKiemTraQuiz, btnHoSo, btnTienDo, btnUserLogout, btnTuVungYeuThich;

    private int taiKhoanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_form);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("User DashBoard");
        }
        taiKhoanId = getIntent().getIntExtra("taiKhoanId", -1);

        initViews();
        setClickListeners();
    }

    private void initViews() {
        btnHocTuVung = findViewById(R.id.btnHocTuVung);
        btnHocNguPhap = findViewById(R.id.btnHocNguPhap);
        btnLuyenNghe = findViewById(R.id.btnLuyenNghe);
        btnKiemTraQuiz = findViewById(R.id.btnKiemTraQuiz);
        btnHoSo = findViewById(R.id.btnHoSo);
        btnTienDo = findViewById(R.id.btnTienDo);
        btnUserLogout = findViewById(R.id.btnUserLogout);
        btnTuVungYeuThich = findViewById(R.id.btnTuVungYeuThich);
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

    private void setClickListeners() {
        btnHocTuVung.setOnClickListener(v -> {
            Intent intent = new Intent(this, HocTuVungActivity.class);
            startActivity(intent);
        });
        btnHocNguPhap.setOnClickListener(v -> {
            Intent intent = new Intent(this, HocNguPhapActivity.class);
            startActivity(intent);
        });
        btnLuyenNghe.setOnClickListener(v -> Toast.makeText(this, "Chức năng Luyện nghe", Toast.LENGTH_SHORT).show());
        btnKiemTraQuiz.setOnClickListener(v -> Toast.makeText(this, "Chức năng Kiểm tra Quiz", Toast.LENGTH_SHORT).show());
        btnHoSo.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("taiKhoanId", taiKhoanId);
            startActivity(intent);
        });
        btnTienDo.setOnClickListener(v -> Toast.makeText(this, "Chức năng Tiến độ học tập", Toast.LENGTH_SHORT).show());
        
        btnTuVungYeuThich.setOnClickListener(v -> {
            Intent intent = new Intent(this, TuVungYeuThichActivity.class);
            intent.putExtra("taiKhoanId", taiKhoanId);
            startActivity(intent);
        });

        btnUserLogout.setOnClickListener(v -> {
            performLogout();
        });
    }
}

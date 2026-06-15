package com.example.btlmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.ThongKeDAO;

public class ThongKeActivity extends AppCompatActivity {

    private TextView tvCountUser, tvCountTuVung, tvCountNguPhap, tvCountQuiz;
    private ThongKeDAO thongKeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        thongKeDAO = new ThongKeDAO(this);
        initViews();
        displayStats();
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
        tvCountUser = findViewById(R.id.tvCountUser);
        tvCountTuVung = findViewById(R.id.tvCountTuVung);
        tvCountNguPhap = findViewById(R.id.tvCountNguPhap);
        tvCountQuiz = findViewById(R.id.tvCountQuiz);
    }

    private void displayStats() {
        tvCountUser.setText(String.valueOf(thongKeDAO.getCountNguoiDung()));
        tvCountTuVung.setText(String.valueOf(thongKeDAO.getCountTuVung()));
        tvCountNguPhap.setText(String.valueOf(thongKeDAO.getCountBaiHoc()));
        tvCountQuiz.setText(String.valueOf(thongKeDAO.getCountQuiz()));
    }
}

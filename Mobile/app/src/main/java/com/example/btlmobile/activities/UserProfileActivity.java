package com.example.btlmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.TaiKhoanDAO;
import com.example.btlmobile.models.NguoiDung;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etHoTen, etNgaySinh, etGioiTinh;
    private Button btnUpdate;
    private TaiKhoanDAO taiKhoanDAO;
    private NguoiDung currentUser;
    private int taiKhoanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        taiKhoanId = getIntent().getIntExtra("taiKhoanId", -1);
        taiKhoanDAO = new TaiKhoanDAO(this);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Hồ sơ cá nhân");
        }
        initViews();
        loadProfile();
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
        etHoTen = findViewById(R.id.etHoTen);
        etNgaySinh = findViewById(R.id.etNgaySinh);
        etGioiTinh = findViewById(R.id.etGioiTinh);
        btnUpdate = findViewById(R.id.btnUpdateProfile);

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadProfile() {
        currentUser = taiKhoanDAO.getNguoiDungByTaiKhoan(taiKhoanId);
        com.example.btlmobile.models.TaiKhoan tk = taiKhoanDAO.getTaiKhoanById(taiKhoanId);
        
        if (currentUser != null) {
            etHoTen.setText(currentUser.getHoTen());
            etNgaySinh.setText(currentUser.getNgaySinh());
            etGioiTinh.setText(currentUser.getGioiTinh());
        }
        
        // Nếu muốn hiển thị Email (giả sử có 1 EditText hoặc TextView cho Email)
        // Trong layout hiện tại chưa có trường Email, bạn có thể bổ sung sau.
        if (tk != null) {
            // etEmail.setText(tk.getEmail());
        }
    }

    private void updateProfile() {
        if (currentUser == null) return;

        currentUser.setHoTen(etHoTen.getText().toString().trim());
        currentUser.setNgaySinh(etNgaySinh.getText().toString().trim());
        currentUser.setGioiTinh(etGioiTinh.getText().toString().trim());

        taiKhoanDAO.updateNguoiDung(currentUser);
        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
    }
}

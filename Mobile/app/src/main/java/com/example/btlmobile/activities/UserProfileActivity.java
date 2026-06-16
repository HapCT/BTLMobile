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
        setContentView(R.layout.userprofileui);

        taiKhoanDAO = new TaiKhoanDAO(this);

        // Đảm bảo lấy được taiKhoanId: Ưu tiên Intent, nếu không có thì lấy từ Session
        taiKhoanId = getIntent().getIntExtra("taiKhoanId", -1);
        if (taiKhoanId == -1) {
            SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
            taiKhoanId = sp.getInt("taiKhoanId", -1);
        }

        // Nếu vẫn không có ID thì không thể tiếp tục
        if (taiKhoanId == -1) {
            Toast.makeText(this, "Không thể xác định tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(this, DangNhapActivity.class);
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
        if (tk != null) {
            // etEmail.setText(tk.getEmail());
        }
    }

    private void updateProfile() {
        String hoTen = etHoTen.getText().toString().trim();
        String ngaySinh = etNgaySinh.getText().toString().trim();
        String gioiTinh = etGioiTinh.getText().toString().trim();

        // 1. Validation (Boundary)
        if (hoTen.isEmpty() || ngaySinh.isEmpty() || gioiTinh.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUser != null) {
            currentUser.setHoTen(hoTen);
            currentUser.setNgaySinh(ngaySinh);
            currentUser.setGioiTinh(gioiTinh);
            taiKhoanDAO.updateNguoiDung(currentUser);
        } else {
            taiKhoanDAO.insertNguoiDung(taiKhoanId, hoTen, ngaySinh, gioiTinh);
            currentUser = taiKhoanDAO.getNguoiDungByTaiKhoan(taiKhoanId);
        }

        // Đồng bộ lên Firebase để các máy khác có thể nhận được dữ liệu mới
        com.google.firebase.database.DatabaseReference ref = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("NguoiDung");
        ref.child(String.valueOf(taiKhoanId)).setValue(currentUser);

        Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
    }
}

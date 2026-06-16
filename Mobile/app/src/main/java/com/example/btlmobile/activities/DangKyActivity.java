package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.TaiKhoanDAO;
import com.example.btlmobile.models.TaiKhoan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DangKyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    EditText edtHoTen, edtNgaySinh, edtTenDN, edtEmail, edtMatKhau, edtNhapLaiMK;
    RadioGroup rdGioiTinh;
    RadioButton rbNam, rbNu;
    Button btnDangKy;
    TextView linkDangNhap;
    TaiKhoanDAO taiKhoanDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangky_form);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        taiKhoanDAO = new TaiKhoanDAO(this);
        Inputs();
        setupEvents();
    }

    private void Inputs() {
        edtHoTen = findViewById(R.id.edtHoTen);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtTenDN = findViewById(R.id.edtTenDangNhap);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtNhapLaiMK = findViewById(R.id.edtNhapLaiMatKhau);
        rdGioiTinh = findViewById(R.id.rdGioiTinh);
        rbNam = findViewById(R.id.rbNam);
        rbNu = findViewById(R.id.rbNu);
        btnDangKy = findViewById(R.id.btnDangKy);
        linkDangNhap = findViewById(R.id.linkDangNhap);
    }

    private void setupEvents() {
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thucHienDangKy();
            }
        });

        linkDangNhap.setOnClickListener(v -> {
            finish();
        });
    }

    private void thucHienDangKy() {
        String hoTen = edtHoTen.getText().toString().trim();
        String ngaySinh = edtNgaySinh.getText().toString().trim();
        String tenDN = edtTenDN.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMK = edtNhapLaiMK.getText().toString().trim();

        if (TextUtils.isEmpty(hoTen) || TextUtils.isEmpty(tenDN) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(matKhau)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!matKhau.equals(nhapLaiMK)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        if (taiKhoanDAO.checkTenDN(tenDN)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        HashMap<String, Object> tkMap = new HashMap<>();
                        tkMap.put("TenDangNhap", tenDN);
                        tkMap.put("Email", email);
                        tkMap.put("VaiTro_id", 2);
                        tkMap.put("TrangThai", "Hoạt động");
                        mDatabase.child("TaiKhoan").child(userId).setValue(tkMap);
                        String gioiTinh = rbNam.isChecked() ? "Nam" : "Nữ";
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("HoTen", hoTen);
                        userMap.put("NgaySinh", ngaySinh);
                        userMap.put("GioiTinh", gioiTinh);
                        userMap.put("Email", email);
                        mDatabase.child("NguoiDung").child(userId).setValue(userMap);
                        TaiKhoan tk = new TaiKhoan();
                        tk.setTenDN(tenDN);
                        tk.setMatKhau(matKhau);
                        tk.setEmail(email);
                        tk.setVaiTro_id(2);
                        tk.setTrangThai("Hoạt động");
                        long taiKhoanId = taiKhoanDAO.insertTaiKhoan(tk);
                        if (taiKhoanId > 0) {
                            taiKhoanDAO.insertNguoiDung((int) taiKhoanId, hoTen, ngaySinh, gioiTinh);
                        }
                        Toast.makeText(DangKyActivity.this, "Đăng ký thành công và đã đồng bộ Cloud!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(DangKyActivity.this, DangNhapActivity.class));
                        finish();
                    } else {
                        Toast.makeText(DangKyActivity.this, "Lỗi Firebase: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}

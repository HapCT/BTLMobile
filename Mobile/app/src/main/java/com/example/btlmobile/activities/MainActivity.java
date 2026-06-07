package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.TaiKhoanDAO;
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.TaiKhoan;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler db ;
    TextView linkDangKy;
    Button btnDangNhap;
    EditText edtTenDN, edtMatKhau;
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHandler(this);
        db.DB2SDCard();
        Inputs();
        TransDKy();
        DangNhap();
    }
    private void Inputs() {
        linkDangKy = findViewById(R.id.linkDangKy);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        edtMatKhau = findViewById(R.id.edtPass);
        edtTenDN = findViewById(R.id.edtTenDN);
    }
    private void TransDKy() {
        linkDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getBaseContext(), DangKy.class);
                startActivity(it);
            }
        });
    }
    private void DangNhap() {
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TenDN = edtTenDN.getText().toString().trim();
                String MatKhau = edtMatKhau.getText().toString().trim();
                TaiKhoan tk = taiKhoanDAO.dangNhap(TenDN, MatKhau);
                if(tk == null) {
                    Toast.makeText(getBaseContext(), "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!"Hoạt động".equals(tk.getTrangThai())) {
                    Toast.makeText(getBaseContext(), "Tài khoản của bạn đã bị khoá", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tk.getVaiTro_id() == 1 ) {
                    Toast.makeText(MainActivity.this, "Đăng nhập Admin thành công", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(getBaseContext(), AdminActivities.class);
                    startActivity(it);
                }
                else if (tk.getVaiTro_id() == 2){
                    Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(getBaseContext(), UserActivities.class);
                    startActivity(it);
                }
            }
        });
    }
}
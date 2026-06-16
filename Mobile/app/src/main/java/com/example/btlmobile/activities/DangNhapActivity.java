package com.example.btlmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.btlmobile.models.TaiKhoan;
import com.example.btlmobile.utils.FirebaseSyncHelper;
import com.google.firebase.auth.FirebaseAuth;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class DangNhapActivity extends AppCompatActivity {
    TextView linkDangKy;
    Button btnDangNhap;
    EditText edtTenDN, edtMatKhau;
    private TaiKhoanDAO taiKhoanDAO;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 0. Bật Persistence để hỗ trợ Offline hoàn toàn
        try {
            if (com.google.firebase.database.FirebaseDatabase.getInstance() != null) {
                com.google.firebase.database.FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }
        } catch (Exception ignored) {}

        // 1. Luôn chuẩn bị Database đầu tiên, kể cả khi có session hay không
        com.example.btlmobile.database.DatabaseHandler dbHandler = new com.example.btlmobile.database.DatabaseHandler(this);
        dbHandler.DB2SDCard();

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        
        // 2. Sau khi đã có DB, mới kiểm tra session để chuyển màn hình
        checkExistingSession();

        EdgeToEdge.enable(this);
        setContentView(R.layout.dangnhap_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        taiKhoanDAO = new TaiKhoanDAO(this);
        // 3. Bắt đầu đồng bộ dữ liệu từ Cloud
        new FirebaseSyncHelper(this).startSync();

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
                Intent it = new Intent(getBaseContext(), DangKyActivity.class);
                startActivity(it);
            }
        });
    }

    private void checkExistingSession() {
        int savedId = sharedPreferences.getInt("taiKhoanId", -1);
        int savedRole = sharedPreferences.getInt("vaiTro_id", -1);

        if (savedId != -1 && savedRole != -1) {
            Intent intent;
            if (savedRole == 1) {
                intent = new Intent(this, AdminActivities.class);
            } else {
                intent = new Intent(this, UserActivities.class);
            }
            intent.putExtra("taiKhoanId", savedId);
            startActivity(intent);
            finish();
        }
    }

    private void saveSession(int id, int role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("taiKhoanId", id);
        editor.putInt("vaiTro_id", role);
        editor.apply();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void DangNhap() {
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputTenDN = edtTenDN.getText().toString().trim();
                String inputMatKhau = edtMatKhau.getText().toString().trim();

                if (inputTenDN.isEmpty() || inputMatKhau.isEmpty()) {
                    Toast.makeText(DangNhapActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                TaiKhoan tk = taiKhoanDAO.dangNhap(inputTenDN, inputMatKhau);

                if (tk != null) {
                    // Trường hợp 1: Tài khoản đã có ở máy cục bộ
                    if (isNetworkAvailable()) {
                        mAuth.signInWithEmailAndPassword(tk.getEmail(), inputMatKhau)
                                .addOnCompleteListener(DangNhapActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        startSession(tk);
                                    } else {
                                        Log.e("LOGIN", "Firebase Auth Failed: " + task.getException().getMessage());
                                        // Vẫn cho vào nếu local pass nhưng Firebase lỗi (chế độ offline)
                                        startSession(tk);
                                    }
                                });
                    } else {
                        startSession(tk);
                    }
                } else if (inputTenDN.contains("@")) {
                    // Trường hợp 2: Máy mới chưa có data cục bộ, thử đăng nhập Firebase trực tiếp bằng Email
                    if (isNetworkAvailable()) {
                        mAuth.signInWithEmailAndPassword(inputTenDN, inputMatKhau)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DangNhapActivity.this, "Đang đồng bộ dữ liệu...", Toast.LENGTH_SHORT).show();
                                        // Sau khi Firebase Auth OK, ta thử lấy lại từ Local vì SyncHelper có thể đã tải về
                                        TaiKhoan cloudTk = taiKhoanDAO.getTaiKhoanByEmail(inputTenDN);
                                        if (cloudTk != null) {
                                            // CẬP NHẬT: Lưu mật khẩu thật vào máy mới để lần sau dùng offline được
                                            taiKhoanDAO.updateMatKhau(inputTenDN, inputMatKhau);
                                            startSession(cloudTk);
                                        } else {
                                            // Nếu vẫn chưa có local, yêu cầu thử lại sau 1-2 giây
                                            Toast.makeText(DangNhapActivity.this, "Dữ liệu đang được tải về, vui lòng thử lại sau 2 giây", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(DangNhapActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(DangNhapActivity.this, "Không có kết nối mạng để kiểm tra tài khoản mới", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DangNhapActivity.this, "Tài khoản không tồn tại trên máy này. Thử đăng nhập bằng Email để đồng bộ.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startSession(TaiKhoan tk) {
        saveSession(tk.getTaiKhoan_id(), tk.getVaiTro_id());
        Intent intent;
        if (tk.getVaiTro_id() == 1) {
            intent = new Intent(DangNhapActivity.this, AdminActivities.class);
            Toast.makeText(DangNhapActivity.this, "Chào mừng Admin", Toast.LENGTH_SHORT).show();
        } else {
            intent = new Intent(DangNhapActivity.this, UserActivities.class);
            Toast.makeText(DangNhapActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        }
        intent.putExtra("taiKhoanId", tk.getTaiKhoan_id());
        startActivity(intent);
        finish();
    }
}
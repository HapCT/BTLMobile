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
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.TaiKhoan;
import com.example.btlmobile.utils.FirebaseSyncHelper;
import com.google.firebase.auth.FirebaseAuth;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler db ;
    TextView linkDangKy;
    Button btnDangNhap;
    EditText edtTenDN, edtMatKhau;
    private TaiKhoanDAO taiKhoanDAO;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        checkExistingSession();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        db = new DatabaseHandler(this);
        db.DB2SDCard();
        taiKhoanDAO = new TaiKhoanDAO(this);
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
                Intent it = new Intent(getBaseContext(), DangKy.class);
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
                if (!isNetworkAvailable()) {
                    Toast.makeText(MainActivity.this, "Không có kết nối mạng. Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                    return;
                }

                String inputTenDN = edtTenDN.getText().toString().trim();
                String inputMatKhau = edtMatKhau.getText().toString().trim();

                if (inputTenDN.isEmpty() || inputMatKhau.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Tìm Email tương ứng với Tên đăng nhập trong SQLite
                String emailToAuth = inputTenDN;
                if (!inputTenDN.contains("@")) {
                    TaiKhoan tkLookup = taiKhoanDAO.getTaiKhoanByTenDN(inputTenDN);
                    if (tkLookup != null) {
                        emailToAuth = tkLookup.getEmail();
                    } else {
                        Toast.makeText(MainActivity.this, "Tên đăng nhập chưa được đồng bộ. Hãy thử dùng Email!", Toast.LENGTH_SHORT).show();
                    }
                }

                Log.d("LOGIN", "Attempting login with: " + emailToAuth);

                mAuth.signInWithEmailAndPassword(emailToAuth, inputMatKhau)
                        .addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful()) {
                                TaiKhoan tkFinal = taiKhoanDAO.getTaiKhoanByEmail(mAuth.getCurrentUser().getEmail());
                                if (tkFinal != null) {
                                    if (!"Hoạt động".equals(tkFinal.getTrangThai())) {
                                        Toast.makeText(MainActivity.this, "Tài khoản của bạn đã bị khoá", Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        return;
                                    }
                                    saveSession(tkFinal.getTaiKhoan_id(), tkFinal.getVaiTro_id());
                                    if (tkFinal.getVaiTro_id() == 1) {
                                        Toast.makeText(MainActivity.this, "Đăng nhập Admin thành công", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(MainActivity.this, AdminActivities.class).putExtra("taiKhoanId", tkFinal.getTaiKhoan_id()));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(MainActivity.this, UserActivities.class).putExtra("taiKhoanId", tkFinal.getTaiKhoan_id()));
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Đăng nhập thành công! Đang tải dữ liệu cá nhân...", Toast.LENGTH_LONG).show();

                                }
                            } else {
                                String errorMsg = "Sai tài khoản hoặc mật khẩu";
                                if (task.getException() != null) {
                                    String rawError = task.getException().getMessage();
                                    if (rawError != null) {
                                        if (rawError.contains("incorrect") || rawError.contains("malformed") || rawError.contains("invalid")) {
                                            errorMsg = "Tài khoản hoặc mật khẩu không chính xác";
                                        } else if (rawError.contains("network")) {
                                            errorMsg = "Lỗi kết nối mạng, vui lòng kiểm tra lại";
                                        } else if (rawError.contains("user-not-found")) {
                                            errorMsg = "Tài khoản không tồn tại";
                                        }
                                    }
                                }
                                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                                if (task.getException() != null) {
                                    Log.e("LOGIN", "Login failed: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }
}
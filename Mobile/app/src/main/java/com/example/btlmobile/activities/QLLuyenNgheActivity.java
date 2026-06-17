package com.example.btlmobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.btlmobile.R;
import com.example.btlmobile.adapters.BaiNgheAdapter;
import com.example.btlmobile.dao.BaiNgheDAO;
import com.example.btlmobile.models.BaiNghe;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class QLLuyenNgheActivity extends AppCompatActivity {

    private ListView lvBaiNghe;
    private Button btnThemBaiNghe;
    private BaiNgheDAO baiNgheDAO;
    private ArrayList<BaiNghe> listBaiNghe;
    private BaiNgheAdapter adapter;
    private Uri audioUri;
    private EditText etAudioGlobal;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    audioUri = uri;
                    if (etAudioGlobal != null) {
                        etAudioGlobal.setText(uri.getLastPathSegment());
                    }
                    Toast.makeText(this, "Đã chọn file âm thanh", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_luyen_nghe);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý luyện nghe");
        }
        baiNgheDAO = new BaiNgheDAO(this);
        initViews();
        loadData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Tìm kiếm bài nghe...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        }
        return true;
    }

    private void filter(String text) {
        ArrayList<BaiNghe> filteredList = new ArrayList<>();
        for (BaiNghe item : listBaiNghe) {
            if (item.getTieuDe().toLowerCase().contains(text.toLowerCase()) ||
                    item.getMoTa().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
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

    private void initViews() {
        lvBaiNghe = findViewById(R.id.lvBaiNghe);
        btnThemBaiNghe = findViewById(R.id.btnThemBaiNghe);

        btnThemBaiNghe.setOnClickListener(v -> showAddEditDialog(null));

        lvBaiNghe.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(listBaiNghe.get(position));
            return true;
        });
    }

    private void loadData() {
        listBaiNghe = baiNgheDAO.getAllBaiNghe();
        adapter = new BaiNgheAdapter(this, R.layout.item_bai_nghe, listBaiNghe);
        lvBaiNghe.setAdapter(adapter);
    }

    private void showOptionsDialog(BaiNghe bn) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(bn.getTieuDe())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddEditDialog(bn);
                    } else {
                        baiNgheDAO.deleteBaiNghe(bn.getBaiNghe_id());
                        loadData();
                        Toast.makeText(this, "Đã xóa bài nghe", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void showAddEditDialog(BaiNghe bn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_bainghe, null);
        EditText etTieuDe = view.findViewById(R.id.etTieuDe);
        EditText etMoTa = view.findViewById(R.id.etMoTa);
        etAudioGlobal = view.findViewById(R.id.etAudio);
        ImageButton btnSelectAudio = view.findViewById(R.id.btnSelectAudio);

        audioUri = null; // Reset uri mỗi lần mở dialog

        if (bn != null) {
            etTieuDe.setText(bn.getTieuDe());
            etMoTa.setText(bn.getMoTa());
            etAudioGlobal.setText(bn.getAudio());
            builder.setTitle("Sửa Bài nghe");
        } else {
            builder.setTitle("Thêm Bài nghe");
        }

        btnSelectAudio.setOnClickListener(v -> mGetContent.launch("audio/*"));

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String tieuDe = etTieuDe.getText().toString().trim();
            String moTa = etMoTa.getText().toString().trim();
            String audio = etAudioGlobal.getText().toString().trim();

            if (tieuDe.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            if (audioUri != null) {
                // Có chọn file mới -> Upload lên Firebase
                uploadAudioToFirebase(bn, tieuDe, moTa);
            } else {
                // Không chọn file mới -> Lưu trực tiếp (có thể là tên file trong raw hoặc link cũ)
                saveBaiNghe(bn, tieuDe, moTa, audio);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void uploadAudioToFirebase(BaiNghe bn, String tieuDe, String moTa) {
        Toast.makeText(this, "Đang tải lên âm thanh...", Toast.LENGTH_SHORT).show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("audios/" + System.currentTimeMillis() + ".mp3");
        
        storageRef.putFile(audioUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveBaiNghe(bn, tieuDe, moTa, uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveBaiNghe(BaiNghe bn, String tieuDe, String moTa, String audio) {
        if (bn == null) {
            BaiNghe newBN = new BaiNghe();
            newBN.setTieuDe(tieuDe);
            newBN.setMoTa(moTa);
            newBN.setAudio(audio);
            long res = baiNgheDAO.insertBaiNghe(newBN);
            if (res > 0) {
                Toast.makeText(this, "Thêm bài nghe thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            bn.setTieuDe(tieuDe);
            bn.setMoTa(moTa);
            bn.setAudio(audio);
            int res = baiNgheDAO.updateBaiNghe(bn);
            if (res > 0) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }
        loadData();
    }
}

package com.example.btlmobile.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.CauHoiQuizDAO;
import com.example.btlmobile.models.CauHoiQuiz;

import java.util.ArrayList;

public class QLCauHoiQuizActivity extends AppCompatActivity {

    private TextView tvTenQuiz;
    private Button btnThemCauHoi;
    private ListView lvCauHoi;
    private CauHoiQuizDAO cauHoiDAO;
    private ArrayList<CauHoiQuiz> listCauHoi;
    private ArrayAdapter<String> adapter;
    private int quizId;
    private String tenQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlcauhoiquizui);

        quizId = getIntent().getIntExtra("quiz_id", -1);
        tenQuiz = getIntent().getStringExtra("ten_quiz");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Câu hỏi");
        }

        cauHoiDAO = new CauHoiQuizDAO(this);
        initViews();
        loadData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initViews() {
        tvTenQuiz = findViewById(R.id.tvTenQuiz);
        btnThemCauHoi = findViewById(R.id.btnThemCauHoi);
        lvCauHoi = findViewById(R.id.lvCauHoi);

        tvTenQuiz.setText(tenQuiz);

        btnThemCauHoi.setOnClickListener(v -> showAddEditDialog(null));

        lvCauHoi.setOnItemClickListener((parent, view, position, id) -> {
            showAddEditDialog(listCauHoi.get(position));
        });
    }

    private void loadData() {
        listCauHoi = cauHoiDAO.getCauHoiByQuizId(quizId);
        ArrayList<String> displayList = new ArrayList<>();
        for (CauHoiQuiz ch : listCauHoi) {
            displayList.add(ch.getNoiDung());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvCauHoi.setAdapter(adapter);
    }

    private void showAddEditDialog(CauHoiQuiz cauHoi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_cau_hoi, null);
        
        EditText etNoiDung = view.findViewById(R.id.etNoiDungCauHoi);
        EditText etA = view.findViewById(R.id.etDapAnA);
        EditText etB = view.findViewById(R.id.etDapAnB);
        EditText etC = view.findViewById(R.id.etDapAnC);
        EditText etD = view.findViewById(R.id.etDapAnD);
        Spinner spDung = view.findViewById(R.id.spDapAnDung);

        if (cauHoi != null) {
            etNoiDung.setText(cauHoi.getNoiDung());
            etA.setText(cauHoi.getDapAnA());
            etB.setText(cauHoi.getDapAnB());
            etC.setText(cauHoi.getDapAnC());
            etD.setText(cauHoi.getDapAnD());
            
            String dung = cauHoi.getDapAnDung();
            int pos = 0;
            if ("B".equals(dung)) pos = 1;
            else if ("C".equals(dung)) pos = 2;
            else if ("D".equals(dung)) pos = 3;
            spDung.setSelection(pos);
            
            builder.setTitle("Sửa Câu hỏi");
        } else {
            builder.setTitle("Thêm Câu hỏi");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String noiDung = etNoiDung.getText().toString().trim();
            String a = etA.getText().toString().trim();
            String b = etB.getText().toString().trim();
            String c = etC.getText().toString().trim();
            String d = etD.getText().toString().trim();
            String dung = spDung.getSelectedItem().toString();

            if (noiDung.isEmpty() || a.isEmpty() || b.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ nội dung và ít nhất 2 đáp án", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cauHoi == null) {
                CauHoiQuiz newCH = new CauHoiQuiz();
                newCH.setQuiz_id(quizId);
                newCH.setNoiDung(noiDung);
                newCH.setDapAnA(a);
                newCH.setDapAnB(b);
                newCH.setDapAnC(c);
                newCH.setDapAnD(d);
                newCH.setDapAnDung(dung);
                cauHoiDAO.insertCauHoi(newCH);
            } else {
                cauHoi.setNoiDung(noiDung);
                cauHoi.setDapAnA(a);
                cauHoi.setDapAnB(b);
                cauHoi.setDapAnC(c);
                cauHoi.setDapAnD(d);
                cauHoi.setDapAnDung(dung);
                cauHoiDAO.updateCauHoi(cauHoi);
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
            loadData();
        });
        
        if (cauHoi != null) {
            builder.setNeutralButton("Xóa", (dialog, which) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa câu hỏi này?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            cauHoiDAO.deleteCauHoi(cauHoi.getCauHoi_id());
                            loadData();
                            Toast.makeText(this, "Đã xóa câu hỏi", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }


}

package com.example.btlmobile.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.CauHoiQuizDAO;
import com.example.btlmobile.models.CauHoiQuiz;

import java.util.ArrayList;

public class LamQuizActivity extends AppCompatActivity {

    private TextView tvSoCauHoi, tvNoiDungCauHoi;
    private RadioGroup rgDapAn;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnTiepTheo;

    private CauHoiQuizDAO cauHoiDAO;
    private ArrayList<CauHoiQuiz> listCauHoi;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lam_quiz);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Luyện tập");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        
        cauHoiDAO = new CauHoiQuizDAO(this);
        int quizId = getIntent().getIntExtra("quiz_id", 1); // Mặc định quiz_id = 1 nếu không truyền
        
        listCauHoi = cauHoiDAO.getCauHoiByQuizId(quizId);
        
        if (listCauHoi != null && !listCauHoi.isEmpty()) {
            displayQuestion();
        } else {
            Toast.makeText(this, "Không có câu hỏi cho bài tập này!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnTiepTheo.setOnClickListener(v -> checkAnswerAndNext());
    }

    private void initViews() {
        tvSoCauHoi = findViewById(R.id.tvSoCauHoi);
        tvNoiDungCauHoi = findViewById(R.id.tvNoiDungCauHoi);
        rgDapAn = findViewById(R.id.rgDapAn);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        btnTiepTheo = findViewById(R.id.btnTiepTheo);
    }

    private void displayQuestion() {
        CauHoiQuiz current = listCauHoi.get(currentQuestionIndex);
        tvSoCauHoi.setText("Câu hỏi " + (currentQuestionIndex + 1) + "/" + listCauHoi.size());
        tvNoiDungCauHoi.setText(current.getNoiDung());
        rbA.setText(current.getDapAnA());
        rbB.setText(current.getDapAnB());
        rbC.setText(current.getDapAnC());
        rbD.setText(current.getDapAnD());
        rgDapAn.clearCheck();
        
        if (currentQuestionIndex == listCauHoi.size() - 1) {
            btnTiepTheo.setText("HOÀN THÀNH");
        } else {
            btnTiepTheo.setText("CÂU TIẾP THEO");
        }
    }

    private void checkAnswerAndNext() {
        int checkedId = rgDapAn.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn một đáp án!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRb = findViewById(checkedId);
        String selectedAnswer = "";
        if (selectedRb == rbA) selectedAnswer = "A";
        else if (selectedRb == rbB) selectedAnswer = "B";
        else if (selectedRb == rbC) selectedAnswer = "C";
        else if (selectedRb == rbD) selectedAnswer = "D";

        if (selectedAnswer.equals(listCauHoi.get(currentQuestionIndex).getDapAnDung())) {
            score++;
        }

        if (currentQuestionIndex < listCauHoi.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            showResultDialog();
        }
    }

    private void showResultDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Kết quả")
                .setMessage("Bạn đã hoàn thành bài tập!\nSố câu đúng: " + score + "/" + listCauHoi.size())
                .setCancelable(false)
                .setPositiveButton("Đóng", (dialog, which) -> finish())
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

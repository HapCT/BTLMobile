package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.QuizDAO;
import com.example.btlmobile.models.Quiz;
import java.util.ArrayList;

public class NguPhapDetailActivity extends AppCompatActivity {

    private TextView tvDetailTieuDe, tvDetailCapDo, tvDetailNoiDung;
    private Button btnLuyenTap;
    private QuizDAO quizDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngu_phap_detail);

        quizDAO = new QuizDAO(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết Ngữ pháp");
        }

        initViews();
        displayData();
    }

    private void initViews() {
        tvDetailTieuDe = findViewById(R.id.tvDetailTieuDe);
        tvDetailCapDo = findViewById(R.id.tvDetailCapDo);
        tvDetailNoiDung = findViewById(R.id.tvDetailNoiDung);
        btnLuyenTap = findViewById(R.id.btnLuyenTap);

        btnLuyenTap.setOnClickListener(v -> {
            String tieuDe = tvDetailTieuDe.getText().toString();
            
            // Tìm Quiz có tên giống hoặc chứa tiêu đề bài học
            ArrayList<Quiz> allQuizzes = quizDAO.getAllQuiz();
            int quizId = -1;
            for (Quiz q : allQuizzes) {
                if (q.getTenQuiz().toLowerCase().contains(tieuDe.toLowerCase()) || 
                    tieuDe.toLowerCase().contains(q.getTenQuiz().toLowerCase())) {
                    quizId = q.getQuiz_id();
                    break;
                }
            }

            if (quizId != -1) {
                Intent intent = new Intent(this, LamQuizActivity.class);
                intent.putExtra("quiz_id", quizId);
                startActivity(intent);
            } else {
                // Nếu không tìm thấy Quiz riêng, có thể mở Quiz tổng hợp theo cấp độ
                Toast.makeText(this, "Hiện chưa có bài tập riêng cho bài học này. Đang chuẩn bị bài tập cấp độ " + tvDetailCapDo.getText(), Toast.LENGTH_LONG).show();
                
                // Demo: Mở Quiz mặc định id = 1
                Intent intent = new Intent(this, LamQuizActivity.class);
                intent.putExtra("quiz_id", 1); 
                startActivity(intent);
            }
        });
    }

    private void displayData() {
        String tieuDe = getIntent().getStringExtra("tieuDe");
        String noiDungBai = getIntent().getStringExtra("noiDungBai");
        String capDo = getIntent().getStringExtra("capDo");

        tvDetailTieuDe.setText(tieuDe);
        tvDetailCapDo.setText("Cấp độ: " + capDo);

        // Hỗ trợ hiển thị HTML để bớt khô khan (nếu nội dung có chứa thẻ html)
        if (noiDungBai != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDetailNoiDung.setText(Html.fromHtml(noiDungBai, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvDetailNoiDung.setText(Html.fromHtml(noiDungBai));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
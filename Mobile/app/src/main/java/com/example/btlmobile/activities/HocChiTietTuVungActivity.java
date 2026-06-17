package com.example.btlmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.btlmobile.R;
import com.example.btlmobile.adapters.TuVungAdapter;
import com.example.btlmobile.dao.QuizDAO;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.Quiz;
import com.example.btlmobile.models.TuVung;
import java.util.ArrayList;

public class HocChiTietTuVungActivity extends AppCompatActivity {
    private ListView lvTuVungHoc;
    private TextView tvTenChuDeHoc;
    private Button btnLuyenTapTuVung;
    private TuVungDAO tuVungDAO;
    private QuizDAO quizDAO;
    private ArrayList<TuVung> listTuVung;
    private TuVungAdapter adapter;
    private int chuDeId;
    private String tenChuDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hocchitiettuvungui);

        chuDeId = getIntent().getIntExtra("chuDeId", -1);
        tenChuDe = getIntent().getStringExtra("tenChuDe");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(tenChuDe);
        }

        tuVungDAO = new TuVungDAO(this);
        quizDAO = new QuizDAO(this);
        lvTuVungHoc = findViewById(R.id.lvTuVungHoc);
        tvTenChuDeHoc = findViewById(R.id.tvTenChuDeHoc);
        btnLuyenTapTuVung = findViewById(R.id.btnLuyenTapTuVung);

        tvTenChuDeHoc.setText("Chủ đề: " + tenChuDe);

        loadData();
        setupEvents();
    }

    private void setupEvents() {
        btnLuyenTapTuVung.setOnClickListener(v -> {
            ArrayList<Quiz> allQuizzes = quizDAO.getAllQuiz();
            int quizId = -1;
            for (Quiz q : allQuizzes) {
                if (q.getTenQuiz().toLowerCase().contains(tenChuDe.toLowerCase()) ||
                        tenChuDe.toLowerCase().contains(q.getTenQuiz().toLowerCase())) {
                    quizId = q.getQuiz_id();
                    break;
                }
            }

            if (quizId != -1) {
                Intent intent = new Intent(this, LamQuizActivity.class);
                intent.putExtra("quiz_id", quizId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Hiện chưa có bài tập luyện tập cho chủ đề này!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        listTuVung = tuVungDAO.getTuVungByChuDe(chuDeId);
        adapter = new TuVungAdapter(this, R.layout.item_tu_vung, listTuVung);
        lvTuVungHoc.setAdapter(adapter);
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
            androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
            searchView.setQueryHint("Tìm kiếm từ vựng...");
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    performSearch(newText);
                    return true;
                }
            });
        }
        return true;
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadData();
        } else {
            listTuVung = tuVungDAO.searchTuVung(chuDeId, query);
            adapter = new TuVungAdapter(this, R.layout.item_tu_vung, listTuVung);
            lvTuVungHoc.setAdapter(adapter);
        }
    }
}

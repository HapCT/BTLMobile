package com.example.btlmobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.dao.QuizDAO;
import com.example.btlmobile.models.Quiz;

import java.util.ArrayList;

public class QLQuizActivity extends AppCompatActivity {

    private ListView lvQuiz;
    private Button btnThemQuiz;
    private QuizDAO quizDAO;
    private ArrayList<Quiz> listQuiz;
    private ArrayAdapter<Quiz> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_quiz);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Quiz");
        }
        quizDAO = new QuizDAO(this);
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        lvQuiz = findViewById(R.id.lvQuiz);
        btnThemQuiz = findViewById(R.id.btnThemQuiz);
        btnThemQuiz.setOnClickListener(v -> showAddEditDialog(null));
        lvQuiz.setOnItemClickListener((parent, view, position, id) -> {
            Quiz selected = listQuiz.get(position);
            Intent intent = new Intent(this, QLCauHoiQuizActivity.class);
            intent.putExtra("quiz_id", selected.getQuiz_id());
            intent.putExtra("ten_quiz", selected.getTenQuiz());
            startActivity(intent);
        });

        lvQuiz.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(listQuiz.get(position));
            return true;
        });
    }

    private void loadData() {
        listQuiz = quizDAO.getAllQuiz();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuiz);
        lvQuiz.setAdapter(adapter);
    }

    private void showOptionsDialog(Quiz quiz) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(quiz.getTenQuiz())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddEditDialog(quiz);
                    } else {
                        quizDAO.deleteQuiz(quiz.getQuiz_id());
                        loadData();
                    }
                }).show();
    }

    private void showAddEditDialog(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_quiz, null);
        EditText etTenQuiz = view.findViewById(R.id.etTenQuiz);
        
        if (quiz != null) {
            etTenQuiz.setText(quiz.getTenQuiz());
            builder.setTitle("Sửa Quiz");
        } else {
            builder.setTitle("Thêm Quiz");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ten = etTenQuiz.getText().toString().trim();
            if (ten.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên quiz", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quiz == null) {
                Quiz newQuiz = new Quiz();
                newQuiz.setTenQuiz(ten);
                newQuiz.setLoaiQuiz_id(1); // Default type
                quizDAO.insertQuiz(newQuiz);
            } else {
                quiz.setTenQuiz(ten);
                quizDAO.updateQuiz(quiz);
            }
            loadData();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

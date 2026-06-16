package com.example.btlmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.LichSuQuiz;
import com.example.btlmobile.models.Quiz;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class QuizDAO {
    private DatabaseHandler dbHandler;
    private DatabaseReference mDatabase;

    public QuizDAO(Context context) {
        dbHandler = new DatabaseHandler(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<Quiz> getAllQuiz() {
        ArrayList<Quiz> list = new ArrayList<>();
        Cursor cursor = dbHandler.getCursor("SELECT * FROM Quiz", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Quiz q = new Quiz();
                q.setQuiz_id(cursor.getInt(cursor.getColumnIndexOrThrow("Quiz_id")));
                q.setTenQuiz(cursor.getString(cursor.getColumnIndexOrThrow("TenQuiz")));
                q.setLoaiQuiz_id(cursor.getInt(cursor.getColumnIndexOrThrow("LoaiQuiz_id")));
                list.add(q);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertQuiz(Quiz quiz) {
        ContentValues values = new ContentValues();
        values.put("TenQuiz", quiz.getTenQuiz());
        values.put("LoaiQuiz_id", quiz.getLoaiQuiz_id());
        long id = dbHandler.insert("Quiz", values);
        if (id > 0) {
            quiz.setQuiz_id((int) id);
            mDatabase.child("Quiz").child(String.valueOf(id)).setValue(quiz);
        }
        return id;
    }

    public int updateQuiz(Quiz quiz) {
        ContentValues values = new ContentValues();
        values.put("TenQuiz", quiz.getTenQuiz());
        values.put("LoaiQuiz_id", quiz.getLoaiQuiz_id());
        int rows = dbHandler.update("Quiz", values, "Quiz_id = ?", new String[]{String.valueOf(quiz.getQuiz_id())});
        if (rows > 0) {
            mDatabase.child("Quiz").child(String.valueOf(quiz.getQuiz_id())).setValue(quiz);
        }
        return rows;
    }

    public void deleteQuiz(int id) {
        dbHandler.delete("CauHoiQuiz", "Quiz_id = ?", new String[]{String.valueOf(id)});
        dbHandler.delete("Quiz", "Quiz_id = ?", new String[]{String.valueOf(id)});
        mDatabase.child("Quiz").child(String.valueOf(id)).removeValue();
    }

    public boolean checkQuizExists(int id) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM Quiz WHERE Quiz_id = ?", new String[]{String.valueOf(id)});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public void saveLichSu(LichSuQuiz ls) {
        ContentValues values = new ContentValues();
        values.put("TaiKhoan_id", ls.getTaiKhoan_id());
        values.put("Quiz_id", ls.getQuiz_id());
        values.put("Diem", ls.getDiem());
        values.put("NgayLam", ls.getNgayLam());
        long id = dbHandler.insert("LichSuQuiz", values);
        if (id > 0) {
            ls.setLichSu_id((int) id);
            mDatabase.child("LichSuQuiz").child(String.valueOf(id)).setValue(ls);
        }
    }
}

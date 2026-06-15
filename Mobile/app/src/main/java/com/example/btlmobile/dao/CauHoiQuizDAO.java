package com.example.btlmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.CauHoiQuiz;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class CauHoiQuizDAO {
    private DatabaseHandler dbHandler;
    private DatabaseReference mDatabase;

    public CauHoiQuizDAO(Context context) {
        dbHandler = new DatabaseHandler(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<CauHoiQuiz> getCauHoiByQuizId(int quizId) {
        ArrayList<CauHoiQuiz> list = new ArrayList<>();
        Cursor cursor = dbHandler.getCursor("SELECT * FROM CauHoiQuiz WHERE Quiz_id = ?", new String[]{String.valueOf(quizId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CauHoiQuiz ch = new CauHoiQuiz();
                ch.setCauHoi_id(cursor.getInt(cursor.getColumnIndexOrThrow("CauHoi_id")));
                ch.setQuiz_id(cursor.getInt(cursor.getColumnIndexOrThrow("Quiz_id")));
                ch.setNoiDung(cursor.getString(cursor.getColumnIndexOrThrow("NoiDung")));
                ch.setDapAnA(cursor.getString(cursor.getColumnIndexOrThrow("DapAnA")));
                ch.setDapAnB(cursor.getString(cursor.getColumnIndexOrThrow("DapAnB")));
                ch.setDapAnC(cursor.getString(cursor.getColumnIndexOrThrow("DapAnC")));
                ch.setDapAnD(cursor.getString(cursor.getColumnIndexOrThrow("DapAnD")));
                ch.setDapAnDung(cursor.getString(cursor.getColumnIndexOrThrow("DapAnDung")));
                list.add(ch);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertCauHoi(CauHoiQuiz ch) {
        ContentValues values = new ContentValues();
        values.put("Quiz_id", ch.getQuiz_id());
        values.put("NoiDung", ch.getNoiDung());
        values.put("DapAnA", ch.getDapAnA());
        values.put("DapAnB", ch.getDapAnB());
        values.put("DapAnC", ch.getDapAnC());
        values.put("DapAnD", ch.getDapAnD());
        values.put("DapAnDung", ch.getDapAnDung());
        long id = dbHandler.insert("CauHoiQuiz", values);
        if (id > 0) {
            ch.setCauHoi_id((int) id);
            mDatabase.child("CauHoiQuiz").child(String.valueOf(id)).setValue(ch);
        }
        return id;
    }

    public int updateCauHoi(CauHoiQuiz ch) {
        ContentValues values = new ContentValues();
        values.put("NoiDung", ch.getNoiDung());
        values.put("DapAnA", ch.getDapAnA());
        values.put("DapAnB", ch.getDapAnB());
        values.put("DapAnC", ch.getDapAnC());
        values.put("DapAnD", ch.getDapAnD());
        values.put("DapAnDung", ch.getDapAnDung());
        int rows = dbHandler.update("CauHoiQuiz", values, "CauHoi_id = ?", new String[]{String.valueOf(ch.getCauHoi_id())});
        if (rows > 0) {
            mDatabase.child("CauHoiQuiz").child(String.valueOf(ch.getCauHoi_id())).setValue(ch);
        }
        return rows;
    }

    public int deleteCauHoi(int id) {
        int rows = dbHandler.delete("CauHoiQuiz", "CauHoi_id = ?", new String[]{String.valueOf(id)});
        if (rows > 0) {
            mDatabase.child("CauHoiQuiz").child(String.valueOf(id)).removeValue();
        }
        return rows;
    }

    public boolean checkCauHoiExists(int id) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM CauHoiQuiz WHERE CauHoi_id = ?", new String[]{String.valueOf(id)});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }
}

package com.example.btlmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.ChuDeTuVung;
import com.example.btlmobile.models.TuVung;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TuVungDAO {
    private DatabaseHandler dbHandler;
    private DatabaseReference mDatabase;

    public TuVungDAO(Context context) {
        dbHandler = new DatabaseHandler(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        dbHandler.execsql("CREATE TABLE IF NOT EXISTS TuVungYeuThich (" +
                "TaiKhoan_id INTEGER, " +
                "TuVung_id INTEGER, " +
                "PRIMARY KEY(TaiKhoan_id, TuVung_id))");
    }

    public ArrayList<ChuDeTuVung> getAllChuDe() {
        ArrayList<ChuDeTuVung> list = new ArrayList<>();
        Cursor cursor = dbHandler.getCursor("SELECT * FROM ChuDeTuVung", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ChuDeTuVung cd = new ChuDeTuVung();
                cd.setChuDeTuVung_id(cursor.getInt(cursor.getColumnIndexOrThrow("ChuDeTuVung_id")));
                cd.setTenChuDe(cursor.getString(cursor.getColumnIndexOrThrow("TenChuDe")));
                int moTaIndex = cursor.getColumnIndex("MoTa");
                if (moTaIndex != -1) {
                    cd.setHinhAnh(cursor.getString(moTaIndex));
                }
                list.add(cd);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertChuDe(ChuDeTuVung chuDe) {
        ContentValues values = new ContentValues();
        values.put("TenChuDe", chuDe.getTenChuDe());
        values.put("MoTa", ""); 
        long id = dbHandler.insert("ChuDeTuVung", values);
        if (id > 0) {
            chuDe.setChuDeTuVung_id((int) id);
            mDatabase.child("ChuDeTuVung").child(String.valueOf(id)).setValue(chuDe);
        }
        return id;
    }

    public int updateChuDe(ChuDeTuVung chuDe) {
        ContentValues values = new ContentValues();
        values.put("TenChuDe", chuDe.getTenChuDe());
        int rows = dbHandler.update("ChuDeTuVung", values, "ChuDeTuVung_id = ?", new String[]{String.valueOf(chuDe.getChuDeTuVung_id())});
        if (rows > 0) {
            mDatabase.child("ChuDeTuVung").child(String.valueOf(chuDe.getChuDeTuVung_id())).setValue(chuDe);
        }
        return rows;
    }

    public void deleteChuDe(int id) {
        dbHandler.delete("TuVung", "ChuDeTuVung_id = ?", new String[]{String.valueOf(id)});
        dbHandler.delete("ChuDeTuVung", "ChuDeTuVung_id = ?", new String[]{String.valueOf(id)});
        mDatabase.child("ChuDeTuVung").child(String.valueOf(id)).removeValue();
    }

    public ArrayList<TuVung> getTuVungByChuDe(int chuDeId) {
        ArrayList<TuVung> list = new ArrayList<>();
        Cursor cursor = dbHandler.getCursor("SELECT * FROM TuVung WHERE ChuDeTuVung_id = ?", new String[]{String.valueOf(chuDeId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TuVung tv = new TuVung();
                tv.setTuVung_id(cursor.getInt(cursor.getColumnIndexOrThrow("TuVung_id")));
                tv.setChuDeTuVung_id(cursor.getInt(cursor.getColumnIndexOrThrow("ChuDeTuVung_id")));
                tv.setTuVung(cursor.getString(cursor.getColumnIndexOrThrow("TuVung")));
                tv.setNghia(cursor.getString(cursor.getColumnIndexOrThrow("NghiaTiengViet")));
                tv.setPhienAm(cursor.getString(cursor.getColumnIndexOrThrow("PhienAm")));
                tv.setHinhAnh(cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")));
                list.add(tv);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertTuVung(TuVung tuVung) {
        ContentValues values = new ContentValues();
        values.put("ChuDeTuVung_id", tuVung.getChuDeTuVung_id());
        values.put("TuVung", tuVung.getTuVung());
        values.put("NghiaTiengViet", tuVung.getNghia());
        values.put("PhienAm", tuVung.getPhienAm());
        values.put("HinhAnh", tuVung.getHinhAnh());
        
        long id = dbHandler.insert("TuVung", values);
        if (id > 0) {
            tuVung.setTuVung_id((int) id);
            mDatabase.child("TuVung").child(String.valueOf(id)).setValue(tuVung);
        }
        return id;
    }

    public int updateTuVung(TuVung tuVung) {
        ContentValues values = new ContentValues();
        values.put("TuVung", tuVung.getTuVung());
        values.put("NghiaTiengViet", tuVung.getNghia());
        values.put("PhienAm", tuVung.getPhienAm());
        values.put("HinhAnh", tuVung.getHinhAnh());
        
        int rows = dbHandler.update("TuVung", values, "TuVung_id = ?", new String[]{String.valueOf(tuVung.getTuVung_id())});
        if (rows > 0) {
            mDatabase.child("TuVung").child(String.valueOf(tuVung.getTuVung_id())).setValue(tuVung);
        }
        return rows;
    }

    public int deleteTuVung(int id) {
        int rows = dbHandler.delete("TuVung", "TuVung_id = ?", new String[]{String.valueOf(id)});
        if (rows > 0) {
            mDatabase.child("TuVung").child(String.valueOf(id)).removeValue();
        }
        return rows;
    }

    public boolean checkChuDeExists(int id) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM ChuDeTuVung WHERE ChuDeTuVung_id = ?", new String[]{String.valueOf(id)});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean checkTuVungExists(int id) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM TuVung WHERE TuVung_id = ?", new String[]{String.valueOf(id)});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }
    public void addFavorite(int taiKhoanId, int tuVungId) {
        ContentValues values = new ContentValues();
        values.put("TaiKhoan_id", taiKhoanId);
        values.put("TuVung_id", tuVungId);
        long result = dbHandler.insert("TuVungYeuThich", values);
        if (result != -1) {
            // Đẩy lên Firebase: TuVungYeuThich/{taiKhoanId}_{tuVungId}
            mDatabase.child("TuVungYeuThich").child(taiKhoanId + "_" + tuVungId).setValue(values);
        }
    }

    public void removeFavorite(int taiKhoanId, int tuVungId) {
        int rows = dbHandler.delete("TuVungYeuThich", "TaiKhoan_id = ? AND TuVung_id = ?", 
            new String[]{String.valueOf(taiKhoanId), String.valueOf(tuVungId)});
        if (rows > 0) {
            // Xóa trên Firebase
            mDatabase.child("TuVungYeuThich").child(taiKhoanId + "_" + tuVungId).removeValue();
        }
    }

    public boolean isFavorite(int taiKhoanId, int tuVungId) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM TuVungYeuThich WHERE TaiKhoan_id = ? AND TuVung_id = ?", 
            new String[]{String.valueOf(taiKhoanId), String.valueOf(tuVungId)});
        boolean favorite = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return favorite;
    }

    public ArrayList<TuVung> getFavoriteTuVung(int taiKhoanId) {
        ArrayList<TuVung> list = new ArrayList<>();
        String sql = "SELECT tv.* FROM TuVung tv " +
                     "INNER JOIN TuVungYeuThich yv ON tv.TuVung_id = yv.TuVung_id " +
                     "WHERE yv.TaiKhoan_id = ?";
        Cursor cursor = dbHandler.getCursor(sql, new String[]{String.valueOf(taiKhoanId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TuVung tv = new TuVung();
                tv.setTuVung_id(cursor.getInt(cursor.getColumnIndexOrThrow("TuVung_id")));
                tv.setChuDeTuVung_id(cursor.getInt(cursor.getColumnIndexOrThrow("ChuDeTuVung_id")));
                tv.setTuVung(cursor.getString(cursor.getColumnIndexOrThrow("TuVung")));
                tv.setNghia(cursor.getString(cursor.getColumnIndexOrThrow("NghiaTiengViet")));
                tv.setPhienAm(cursor.getString(cursor.getColumnIndexOrThrow("PhienAm")));
                tv.setHinhAnh(cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")));
                list.add(tv);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}

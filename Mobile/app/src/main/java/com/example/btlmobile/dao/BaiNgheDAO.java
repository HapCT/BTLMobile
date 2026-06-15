package com.example.btlmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.BaiNghe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BaiNgheDAO {
    private DatabaseHandler dbHandler;
    private DatabaseReference mDatabase;

    public BaiNgheDAO(Context context) {
        dbHandler = new DatabaseHandler(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<BaiNghe> getAllBaiNghe() {
        ArrayList<BaiNghe> list = new ArrayList<>();
        Cursor cursor = dbHandler.getCursor("SELECT * FROM BaiNghe", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BaiNghe bn = new BaiNghe();
                bn.setBaiNghe_id(cursor.getInt(cursor.getColumnIndexOrThrow("BaiNghe_id")));
                bn.setTieuDe(cursor.getString(cursor.getColumnIndexOrThrow("TieuDe")));
                bn.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("MoTa")));
                bn.setHinhAnh(cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")));
                bn.setAudio(cursor.getString(cursor.getColumnIndexOrThrow("Audio")));
                list.add(bn);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertBaiNghe(BaiNghe bn) {
        ContentValues values = new ContentValues();
        values.put("TieuDe", bn.getTieuDe());
        values.put("MoTa", bn.getMoTa());
        values.put("HinhAnh", bn.getHinhAnh());
        values.put("Audio", bn.getAudio());
        long id = dbHandler.insert("BaiNghe", values);
        if (id > 0) {
            bn.setBaiNghe_id((int) id);
            mDatabase.child("BaiNghe").child(String.valueOf(id)).setValue(bn);
        }
        return id;
    }

    public int updateBaiNghe(BaiNghe bn) {
        ContentValues values = new ContentValues();
        values.put("TieuDe", bn.getTieuDe());
        values.put("MoTa", bn.getMoTa());
        values.put("HinhAnh", bn.getHinhAnh());
        values.put("Audio", bn.getAudio());
        int rows = dbHandler.update("BaiNghe", values, "BaiNghe_id = ?", new String[]{String.valueOf(bn.getBaiNghe_id())});
        if (rows > 0) {
            mDatabase.child("BaiNghe").child(String.valueOf(bn.getBaiNghe_id())).setValue(bn);
        }
        return rows;
    }

    public int deleteBaiNghe(int id) {
        int rows = dbHandler.delete("BaiNghe", "BaiNghe_id = ?", new String[]{String.valueOf(id)});
        if (rows > 0) {
            mDatabase.child("BaiNghe").child(String.valueOf(id)).removeValue();
        }
        return rows;
    }

    public boolean checkBaiNgheExists(int id) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM BaiNghe WHERE BaiNghe_id = ?", new String[]{String.valueOf(id)});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }
}

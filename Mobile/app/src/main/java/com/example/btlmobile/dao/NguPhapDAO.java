package com.example.btlmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.BaiHocNguPhap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NguPhapDAO {
    private DatabaseHandler dbHandler;

    private DatabaseReference mDatabase;
    public NguPhapDAO(Context context) {

        dbHandler = new DatabaseHandler(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<BaiHocNguPhap> getAllBaiHoc() {
        ArrayList<BaiHocNguPhap> list = new ArrayList<>();
        Cursor cursor = dbHandler.getCursor("SELECT * FROM BaiHocNguPhap", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BaiHocNguPhap bh = new BaiHocNguPhap();
                bh.setBaiHocNP_id(cursor.getInt(cursor.getColumnIndexOrThrow("BaiHocNP_id")));
                bh.setTieuDe(cursor.getString(cursor.getColumnIndexOrThrow("TieuDe")));
                bh.setNoiDungBai(cursor.getString(cursor.getColumnIndexOrThrow("NoiDungBai")));
                bh.setCapDo(cursor.getString(cursor.getColumnIndexOrThrow("CapDo")));
                list.add(bh);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertBaiHoc(BaiHocNguPhap baiHoc) {
        ContentValues values = new ContentValues();
        values.put("TieuDe", baiHoc.getTieuDe());
        values.put("NoiDungBai", baiHoc.getNoiDungBai());
        values.put("CapDo", baiHoc.getCapDo());
        long id = dbHandler.insert("BaiHocNguPhap", values);
        if(id > 0){
            baiHoc.setBaiHocNP_id((int) id);
            mDatabase.child("BaiHocNguPhap").child(String.valueOf(id)).setValue(baiHoc);
        }
        return id;
    }

    public int updateBaiHoc(BaiHocNguPhap baiHoc) {
        ContentValues values = new ContentValues();
        values.put("TieuDe", baiHoc.getTieuDe());
        values.put("NoiDungBai", baiHoc.getNoiDungBai());
        values.put("CapDo", baiHoc.getCapDo());
        long id = dbHandler.update("BaiHocNguPhap", values, "BaiHocNP_id = ?", new String[]{String.valueOf(baiHoc.getBaiHocNP_id())});
        if(id > 0){
            mDatabase.child("BaiHocNguPhap").child(String.valueOf(baiHoc.getBaiHocNP_id())).setValue(baiHoc);
        }
        return (int) id;
    }
    public boolean checkBaiNguPhapExists(int id) {
        Cursor cursor = dbHandler.getCursor("SELECT * FROM BaiHocNguPhap WHERE BaiHocNP_id = ?", new String[]{String.valueOf(id)});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public ArrayList<BaiHocNguPhap> searchNguPhap(String query) {
        ArrayList<BaiHocNguPhap> list = new ArrayList<>();
        String sql = "SELECT * FROM BaiHocNguPhap WHERE TieuDe LIKE ?";
        Cursor cursor = dbHandler.getCursor(sql, new String[]{"%" + query + "%"});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BaiHocNguPhap bh = new BaiHocNguPhap();
                bh.setBaiHocNP_id(cursor.getInt(cursor.getColumnIndexOrThrow("BaiHocNP_id")));
                bh.setTieuDe(cursor.getString(cursor.getColumnIndexOrThrow("TieuDe")));
                bh.setNoiDungBai(cursor.getString(cursor.getColumnIndexOrThrow("NoiDungBai")));
                bh.setCapDo(cursor.getString(cursor.getColumnIndexOrThrow("CapDo")));
                list.add(bh);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int deleteBaiHoc(int id) {
        long id1 = dbHandler.delete("BaiHocNguPhap", "BaiHocNP_id = ?", new String[]{String.valueOf(id)});
        if(id1 > 0){
            mDatabase.child("BaiHocNguPhap").child(String.valueOf(id)).removeValue();
        }
        return (int) id1;
    }
}

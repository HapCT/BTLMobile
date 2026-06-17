package com.example.btlmobile.dao;

import android.content.Context;
import android.database.Cursor;
import com.example.btlmobile.database.DatabaseHandler;

public class ThongKeDAO {
    private DatabaseHandler db;

    public ThongKeDAO(Context context) {
        db = new DatabaseHandler(context);
    }

    public int getCount(String table) {
        Cursor cr = db.getCursor("SELECT COUNT(*) FROM " + table, null);
        int count = 0;
        if (cr != null && cr.moveToFirst()) {
            count = cr.getInt(0);
            cr.close();
        }
        return count;
    }

    public int getCountTuVung() {
        return getCount("TuVung");
    }

    public int getCountNguoiDung() {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE VaiTro_id = 2 AND TrangThai != 'Đã xóa'";
        Cursor cr = db.getCursor(sql, null);
        int count = 0;
        if (cr != null && cr.moveToFirst()) {
            count = cr.getInt(0);
            cr.close();
        }
        return count;
    }

    public int getCountTaiKhoanDaXoa() {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE TrangThai = 'Đã xóa'";
        Cursor cr = db.getCursor(sql, null);
        int count = 0;
        if (cr != null && cr.moveToFirst()) {
            count = cr.getInt(0);
            cr.close();
        }
        return count;
    }

    public int getCountBaiHoc() {
        return getCount("BaiHocNguPhap");
    }

    public int getCountQuiz() {
        return getCount("Quiz");
    }

    public int getCountBaiNghe() {
        return getCount("BaiNghe");
    }
}

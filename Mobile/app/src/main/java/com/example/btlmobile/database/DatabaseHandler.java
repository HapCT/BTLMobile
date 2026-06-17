package com.example.btlmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String dbName = "BTL.sqlite";
    private static final int dbVersion = 1;
    private Context context;

    public DatabaseHandler(Context context) {
        super(context, dbName, null, dbVersion);
        this.context = context;
    }

    // Sao chép database từ assets vào bộ nhớ ứng dụng
    public void DB2SDCard() {
        File file = context.getDatabasePath(dbName);
        if (file.exists()) return;

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            try (InputStream in = context.getAssets().open(dbName);
                 OutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                Log.d("DatabaseHandler", "Database copied successfully");
            }
        } catch (Exception e) {
            Log.e("DatabaseHandler", "Error copying database", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Quiz (Quiz_id INTEGER PRIMARY KEY AUTOINCREMENT, TenQuiz TEXT, LoaiQuiz_id INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CauHoiQuiz (CauHoi_id INTEGER PRIMARY KEY AUTOINCREMENT, Quiz_id INTEGER, NoiDung TEXT, DapAnA TEXT, DapAnB TEXT, DapAnC TEXT, DapAnD TEXT, DapAnDung TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS LichSuQuiz (LichSu_id INTEGER PRIMARY KEY AUTOINCREMENT, TaiKhoan_id INTEGER, Quiz_id INTEGER, Diem INTEGER, NgayLam TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS LichSuHocNguPhap (LichSuNP_id INTEGER PRIMARY KEY AUTOINCREMENT, TaiKhoan_id INTEGER, BaiHocNP_id INTEGER, NgayHoc TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS LichSuLuyenNghe (LichSuLN_id INTEGER PRIMARY KEY AUTOINCREMENT, TaiKhoan_id INTEGER, BaiNghe_id INTEGER, NgayNghe TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS TuVungYeuThich (ID INTEGER PRIMARY KEY AUTOINCREMENT, TaiKhoan_id INTEGER, TuVung_id INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS BaiNghe (BaiNghe_id INTEGER PRIMARY KEY AUTOINCREMENT, TieuDe TEXT, MoTa TEXT, HinhAnh TEXT, Audio TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS TaiKhoan (TaiKhoan_id INTEGER PRIMARY KEY AUTOINCREMENT, TenDN TEXT, MatKhau TEXT, Email TEXT, HoTen TEXT, VaiTro_id INTEGER, TrangThai TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS TuVung (TuVung_id INTEGER PRIMARY KEY AUTOINCREMENT, Tu TEXT, Nghia TEXT, PhienAm TEXT, LoaiTu TEXT, ViDu TEXT, HinhAnh TEXT, Audio TEXT, ChuDe_id INTEGER)");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        createTables(db);

        // Vá lỗi schema cho các bản cài đặt cũ
        try { db.execSQL("ALTER TABLE CauHoiQuiz ADD COLUMN Quiz_id INTEGER"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE Quiz ADD COLUMN LoaiQuiz_id INTEGER"); } catch (Exception ignored) {}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createTables(db);
    }

    public Cursor getCursor(String sql, String[] args) {
        return getReadableDatabase().rawQuery(sql, args);
    }

    public void execsql(String sql) {
        getWritableDatabase().execSQL(sql);
    }

    public long insert(String table, ContentValues values) {
        return getWritableDatabase().insert(table, null, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return getWritableDatabase().update(table, values, whereClause, whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(table, whereClause, whereArgs);
    }
}

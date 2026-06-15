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
    private static final String TAG = "DatabaseHandler";
    private Context dbContext;
    private String dbPath;
    private static final String dbName = "BTL.sqlite";
    private static final int dbVersion = 1;
    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, dbName, null, dbVersion);
        this.dbContext = context;
        this.dbPath = context.getDatabasePath(dbName).getAbsolutePath();
    }

    public void DB2SDCard() {
        File file = new File(dbPath);
        if (file.exists()) return;

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            InputStream in = dbContext.getAssets().open(dbName);
            OutputStream out = new FileOutputStream(dbPath);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
            Log.d(TAG, "Database copied successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error copying database", e);
        }
    }

    private void openDatabase() {
        if (database == null || !database.isOpen()) {
            database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            createTablesIfNotExist();
        }
    }

    private void createTablesIfNotExist() {
        database.execSQL("CREATE TABLE IF NOT EXISTS Quiz (" +
                "Quiz_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TenQuiz TEXT, " +
                "LoaiQuiz_id INTEGER)");

        database.execSQL("CREATE TABLE IF NOT EXISTS CauHoiQuiz (" +
                "CauHoi_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Quiz_id INTEGER, " +
                "NoiDung TEXT, " +
                "DapAnA TEXT, " +
                "DapAnB TEXT, " +
                "DapAnC TEXT, " +
                "DapAnD TEXT, " +
                "DapAnDung TEXT)");
        try {
            database.execSQL("ALTER TABLE CauHoiQuiz ADD COLUMN Quiz_id INTEGER");
        } catch (Exception ignored) {}
        try {
            database.execSQL("ALTER TABLE Quiz ADD COLUMN LoaiQuiz_id INTEGER");
        } catch (Exception ignored) {}
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    public Cursor getCursor(String sql, String[] selectionArgs) {
        openDatabase();
        return database.rawQuery(sql, selectionArgs);
    }

    public void execsql(String sql) {
        openDatabase();
        database.execSQL(sql);
    }

    public long insert(String table, ContentValues values) {
        openDatabase();
        return database.insert(table, null, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        openDatabase();
        return database.update(table, values, whereClause, whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        openDatabase();
        return database.delete(table, whereClause, whereArgs);
    }

    @Override public void onCreate(SQLiteDatabase sqLiteDatabase) {}
    @Override public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}

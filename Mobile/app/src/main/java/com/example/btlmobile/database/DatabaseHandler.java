package com.example.btlmobile.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHandler extends SQLiteOpenHelper {
    Context dbContext;
    SQLiteDatabase db;
    String dbPath;
    static  String dbName = "BTL.sqlite";
    static int dbVersion = 1;
    public DatabaseHandler (Context context){
        super(context, dbName,null, dbVersion);
        dbContext = context;
        dbPath = context.getDatabasePath(dbName).getAbsolutePath(); // tự path không phải khai báo lại như kia
    }
    public void DB2SDCard(){
        try {
            File file = new File(dbPath);
            if(file.exists()){
                Toast.makeText(dbContext.getApplicationContext(), "file CSDL đã tồn tại", Toast.LENGTH_LONG).show();
                this.close();
            }
            else {
                try {
                    this.getReadableDatabase();
                    InputStream in = dbContext.getAssets().open(dbName);
                    OutputStream out = new FileOutputStream(dbPath);
                    byte []buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0){
                        out.write(buf,0,len);
                    }
                    Toast.makeText(dbContext.getApplicationContext(), "DB2SDCard done!", Toast.LENGTH_LONG).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(dbContext.getApplicationContext(), "lôi", Toast.LENGTH_LONG).show();
        }
    }
    public Cursor getCursor(String sql){
        db = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READWRITE);
        Cursor cr = db.rawQuery(sql,null);
        return cr;
    }
    public void execsql(String sql){
        db = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READWRITE);
        db.execSQL(sql);
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

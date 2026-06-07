package com.example.btlmobile.dao;

import android.content.Context;
import android.database.Cursor;

import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.TaiKhoan;

public class TaiKhoanDAO {
    private DatabaseHandler db;
    public TaiKhoanDAO(Context context) {
        db = new DatabaseHandler(context);
    }
    public TaiKhoan dangNhap(String TenDN, String MatKhau){
        String getDN = "SELECT * FROM TaiKhoan " +
                "WHERE TenDangNhap = '" + TenDN +
                "' AND MatKhau = '" + MatKhau + "'";
        Cursor cr = db.getCursor(getDN);
        TaiKhoan tk = null;
        if(cr.moveToFirst()){
            tk = new TaiKhoan();
            tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
            tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
            tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
            tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
            tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
            tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
        }
        cr.close();
        return tk;
    }
}

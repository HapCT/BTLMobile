package com.example.btlmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.btlmobile.database.DatabaseHandler;
import com.example.btlmobile.models.TaiKhoan;
import com.example.btlmobile.models.NguoiDung;
import java.util.ArrayList;

public class TaiKhoanDAO {
    private DatabaseHandler db;

    public TaiKhoanDAO(Context context) {
        db = new DatabaseHandler(context);
    }

    public TaiKhoan dangNhap(String TenDN, String MatKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
        Cursor cr = db.getCursor(sql, new String[]{TenDN, MatKhau});
        TaiKhoan tk = null;
        if (cr != null && cr.moveToFirst()) {
            tk = new TaiKhoan();
            tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
            tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
            tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
            tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
            tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
            tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
            cr.close();
        }
        return tk;
    }

    public TaiKhoan getTaiKhoanById(int id) {
        String sql = "SELECT * FROM TaiKhoan WHERE TaiKhoan_id = ?";
        Cursor cr = db.getCursor(sql, new String[]{String.valueOf(id)});
        if (cr != null && cr.moveToFirst()) {
            TaiKhoan tk = new TaiKhoan();
            tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
            tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
            tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
            tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
            tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
            tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
            cr.close();
            return tk;
        }
        return null;
    }

    public TaiKhoan getTaiKhoanByEmail(String email) {
        String sql = "SELECT * FROM TaiKhoan WHERE Email = ?";
        Cursor cr = db.getCursor(sql, new String[]{email});
        if (cr != null && cr.moveToFirst()) {
            TaiKhoan tk = new TaiKhoan();
            tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
            tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
            tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
            tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
            tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
            tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
            cr.close();
            return tk;
        }
        return null;
    }

    public TaiKhoan getTaiKhoanByTenDN(String tenDN) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        Cursor cr = db.getCursor(sql, new String[]{tenDN});
        if (cr != null && cr.moveToFirst()) {
            TaiKhoan tk = new TaiKhoan();
            tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
            tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
            tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
            tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
            tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
            tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
            cr.close();
            return tk;
        }
        return null;
    }

    public long insertTaiKhoan(TaiKhoan tk) {
        ContentValues values = new ContentValues();
        values.put("TenDangNhap", tk.getTenDN());
        values.put("MatKhau", tk.getMatKhau());
        values.put("Email", tk.getEmail());
        values.put("VaiTro_id", tk.getVaiTro_id());
        values.put("TrangThai", tk.getTrangThai());
        return db.insert("TaiKhoan", values);
    }

    public void insertNguoiDung(int taiKhoanId, String hoTen, String ngaySinh, String gioiTinh) {
        ContentValues values = new ContentValues();
        values.put("HoTen", hoTen);
        values.put("NgaySinh", ngaySinh);
        values.put("GioiTinh", gioiTinh);
        values.put("TaiKhoan_id", taiKhoanId);
        db.insert("NguoiDung", values);
    }

    public NguoiDung getNguoiDungByTaiKhoan(int taiKhoanId) {
        Cursor cursor = db.getCursor("SELECT * FROM NguoiDung WHERE TaiKhoan_id = ?", new String[]{String.valueOf(taiKhoanId)});
        if (cursor != null && cursor.moveToFirst()) {
            NguoiDung user = new NguoiDung();
            user.setNguoiDungId(cursor.getInt(cursor.getColumnIndexOrThrow("NguoiDung_id")));
            user.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow("HoTen")));
            user.setNgaySinh(cursor.getString(cursor.getColumnIndexOrThrow("NgaySinh")));
            user.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh")));
            user.setTaiKhoanId(cursor.getInt(cursor.getColumnIndexOrThrow("TaiKhoan_id")));
            cursor.close();
            return user;
        }
        return null;
    }

    public void updateNguoiDung(NguoiDung user) {
        db.execsql("UPDATE NguoiDung SET HoTen = '" + user.getHoTen() + "', NgaySinh = '" + user.getNgaySinh() + "', GioiTinh = '" + user.getGioiTinh() + "' WHERE NguoiDung_id = " + user.getNguoiDungId());
    }

    public boolean checkTenDN(String TenDN) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        Cursor cr = db.getCursor(sql, new String[]{TenDN});
        boolean exists = cr != null && cr.getCount() > 0;
        if (cr != null) cr.close();
        return exists;
    }

    public void updateVaiTroVaEmail(String tenDN, String email, int vaiTroId) {
        db.execsql("UPDATE TaiKhoan SET Email = '" + email + "', VaiTro_id = " + vaiTroId + " WHERE TenDangNhap = '" + tenDN + "'");
    }

    public void updateTrangThai(int taiKhoanId, String trangThai) {
        db.execsql("UPDATE TaiKhoan SET TrangThai = '" + trangThai + "' WHERE TaiKhoan_id = " + taiKhoanId);
    }

    public void deleteTaiKhoan(int taiKhoanId) {
        db.execsql("DELETE FROM NguoiDung WHERE TaiKhoan_id = " + taiKhoanId);
        db.execsql("DELETE FROM TaiKhoan WHERE TaiKhoan_id = " + taiKhoanId);
    }

    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        ArrayList<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan WHERE VaiTro_id = 2 ";
        Cursor cr = db.getCursor(sql, null);
        if (cr != null && cr.moveToFirst()) {
            do {
                TaiKhoan tk = new TaiKhoan();
                tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
                tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
                tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
                tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
                tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
                tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
                list.add(tk);
            } while (cr.moveToNext());
            cr.close();
        }
        return list;
    }

    public ArrayList<TaiKhoan> searchTaiKhoan(String query) {
        ArrayList<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap LIKE ? AND VaiTro_id = 2";
        Cursor cr = db.getCursor(sql, new String[]{"%" + query + "%"});
        if (cr != null && cr.moveToFirst()) {
            do {
                TaiKhoan tk = new TaiKhoan();
                tk.setTaiKhoan_id(cr.getInt(cr.getColumnIndexOrThrow("TaiKhoan_id")));
                tk.setTenDN(cr.getString(cr.getColumnIndexOrThrow("TenDangNhap")));
                tk.setMatKhau(cr.getString(cr.getColumnIndexOrThrow("MatKhau")));
                tk.setEmail(cr.getString(cr.getColumnIndexOrThrow("Email")));
                tk.setVaiTro_id(cr.getInt(cr.getColumnIndexOrThrow("VaiTro_id")));
                tk.setTrangThai(cr.getString(cr.getColumnIndexOrThrow("TrangThai")));
                list.add(tk);
            } while (cr.moveToNext());
            cr.close();
        }
        return list;
    }
}

package com.example.btlmobile.models;

public class NguoiDung {
    private int nguoiDungId;
    private String hoTen;
    private String ngaySinh;
    private String gioiTinh;
    private int taiKhoanId;

    public NguoiDung() {}

    public NguoiDung(int nguoiDungId, String hoTen, String ngaySinh, String gioiTinh, int taiKhoanId) {
        this.nguoiDungId = nguoiDungId;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.taiKhoanId = taiKhoanId;
    }

    public int getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(int nguoiDungId) { this.nguoiDungId = nguoiDungId; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public int getTaiKhoanId() { return taiKhoanId; }
    public void setTaiKhoanId(int taiKhoanId) { this.taiKhoanId = taiKhoanId; }
}

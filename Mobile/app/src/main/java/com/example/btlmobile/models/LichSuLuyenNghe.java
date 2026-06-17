package com.example.btlmobile.models;

public class LichSuLuyenNghe {
    private int taiKhoan_id;
    private int baiNghe_id;
    private String ngayNghe;

    public LichSuLuyenNghe() {}

    public LichSuLuyenNghe(int taiKhoan_id, int baiNghe_id, String ngayNghe) {
        this.taiKhoan_id = taiKhoan_id;
        this.baiNghe_id = baiNghe_id;
        this.ngayNghe = ngayNghe;
    }

    public int getTaiKhoan_id() { return taiKhoan_id; }
    public void setTaiKhoan_id(int taiKhoan_id) { this.taiKhoan_id = taiKhoan_id; }

    public int getBaiNghe_id() { return baiNghe_id; }
    public void setBaiNghe_id(int baiNghe_id) { this.baiNghe_id = baiNghe_id; }

    public String getNgayNghe() { return ngayNghe; }
    public void setNgayNghe(String ngayNghe) { this.ngayNghe = ngayNghe; }
}

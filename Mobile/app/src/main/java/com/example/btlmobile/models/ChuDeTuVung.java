package com.example.btlmobile.models;

public class ChuDeTuVung {
    private int chuDeTuVung_id;
    private String tenChuDe;
    private String hinhAnh;

    public ChuDeTuVung() {}

    public ChuDeTuVung(int chuDeTuVung_id, String tenChuDe, String hinhAnh) {
        this.chuDeTuVung_id = chuDeTuVung_id;
        this.tenChuDe = tenChuDe;
        this.hinhAnh = hinhAnh;
    }

    public int getChuDeTuVung_id() {
        return chuDeTuVung_id;
    }

    public void setChuDeTuVung_id(int chuDeTuVung_id) {
        this.chuDeTuVung_id = chuDeTuVung_id;
    }

    public String getTenChuDe() {
        return tenChuDe;
    }

    public void setTenChuDe(String tenChuDe) {
        this.tenChuDe = tenChuDe;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    @Override
    public String toString() {
        return tenChuDe;
    }
}

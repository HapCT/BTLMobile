package com.example.btlmobile.models;

public class TuVung {
    private int tuVung_id;
    private int chuDeTuVung_id;
    private String tuVung;
    private String nghia;
    private String phienAm;
    private String viDu;
    private String hinhAnh;
    private String amThanh;

    public TuVung() {}

    public TuVung(int tuVung_id, int chuDeTuVung_id, String tuVung, String nghia, String phienAm, String viDu, String hinhAnh, String amThanh) {
        this.tuVung_id = tuVung_id;
        this.chuDeTuVung_id = chuDeTuVung_id;
        this.tuVung = tuVung;
        this.nghia = nghia;
        this.phienAm = phienAm;
        this.viDu = viDu;
        this.hinhAnh = hinhAnh;
        this.amThanh = amThanh;
    }

    public int getTuVung_id() {
        return tuVung_id;
    }

    public void setTuVung_id(int tuVung_id) {
        this.tuVung_id = tuVung_id;
    }

    public int getChuDeTuVung_id() {
        return chuDeTuVung_id;
    }

    public void setChuDeTuVung_id(int chuDeTuVung_id) {
        this.chuDeTuVung_id = chuDeTuVung_id;
    }

    public String getTuVung() {
        return tuVung;
    }

    public void setTuVung(String tuVung) {
        this.tuVung = tuVung;
    }

    public String getNghia() {
        return nghia;
    }

    public void setNghia(String nghia) {
        this.nghia = nghia;
    }

    public String getPhienAm() {
        return phienAm;
    }

    public void setPhienAm(String phienAm) {
        this.phienAm = phienAm;
    }

    public String getViDu() {
        return viDu;
    }

    public void setViDu(String viDu) {
        this.viDu = viDu;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getAmThanh() {
        return amThanh;
    }

    public void setAmThanh(String amThanh) {
        this.amThanh = amThanh;
    }
}

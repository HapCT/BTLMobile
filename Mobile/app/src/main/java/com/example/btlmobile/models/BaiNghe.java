package com.example.btlmobile.models;

import java.io.Serializable;

public class BaiNghe implements Serializable {
    private int baiNghe_id;
    private String tieuDe;
    private String moTa;
    private String hinhAnh;
    private String audio;

    public BaiNghe() {}

    public int getBaiNghe_id() { return baiNghe_id; }
    public void setBaiNghe_id(int baiNghe_id) { this.baiNghe_id = baiNghe_id; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getAudio() { return audio; }
    public void setAudio(String audio) { this.audio = audio; }

    @Override
    public String toString() {
        return tieuDe;
    }
}

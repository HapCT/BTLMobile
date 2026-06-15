package com.example.btlmobile.models;

public class BaiHocNguPhap {
    private int baiHocNP_id;
    private String tieuDe;
    private String noiDungBai;
    private String capDo;

    public BaiHocNguPhap() {}

    public BaiHocNguPhap(int baiHocNP_id, String tieuDe, String noiDungBai, String capDo) {
        this.baiHocNP_id = baiHocNP_id;
        this.tieuDe = tieuDe;
        this.noiDungBai = noiDungBai;
        this.capDo = capDo;
    }

    public int getBaiHocNP_id() {
        return baiHocNP_id;
    }

    public void setBaiHocNP_id(int baiHocNP_id) {
        this.baiHocNP_id = baiHocNP_id;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDungBai() {
        return noiDungBai;
    }

    public void setNoiDungBai(String noiDungBai) {
        this.noiDungBai = noiDungBai;
    }

    public String getCapDo() {
        return capDo;
    }

    public void setCapDo(String capDo) {
        this.capDo = capDo;
    }

    @Override
    public String toString() {
        return tieuDe;
    }
}

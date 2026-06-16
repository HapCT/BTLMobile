package com.example.btlmobile.models;

public class LichSuQuiz {
    private int lichSu_id;
    private int taiKhoan_id;
    private int quiz_id;
    private int diem;
    private String ngayLam;

    public LichSuQuiz() {}

    public LichSuQuiz(int taiKhoan_id, int quiz_id, int diem, String ngayLam) {
        this.taiKhoan_id = taiKhoan_id;
        this.quiz_id = quiz_id;
        this.diem = diem;
        this.ngayLam = ngayLam;
    }

    public int getLichSu_id() { return lichSu_id; }
    public void setLichSu_id(int lichSu_id) { this.lichSu_id = lichSu_id; }
    public int getTaiKhoan_id() { return taiKhoan_id; }
    public void setTaiKhoan_id(int taiKhoan_id) { this.taiKhoan_id = taiKhoan_id; }
    public int getQuiz_id() { return quiz_id; }
    public void setQuiz_id(int quiz_id) { this.quiz_id = quiz_id; }
    public int getDiem() { return diem; }
    public void setDiem(int diem) { this.diem = diem; }
    public String getNgayLam() { return ngayLam; }
    public void setNgayLam(String ngayLam) { this.ngayLam = ngayLam; }
}

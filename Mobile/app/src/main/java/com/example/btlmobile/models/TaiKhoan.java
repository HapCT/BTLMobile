package com.example.btlmobile.models;

import android.widget.TableRow;

public class TaiKhoan {
    private int TaiKhoan_id;
    private String TenDN;
    private String MatKhau;
    private String email;
    private int VaiTro_id;
    private String TrangThai;
    public TaiKhoan(){

    }
    public TaiKhoan(int TaiKhoan_id, String TenDN, String MatKhau, String email, int VaiTro_id, String TrangThai){
        this.TaiKhoan_id = TaiKhoan_id;
        this.TenDN = TenDN;
        this.MatKhau = MatKhau;
        this.email = email;
        this.VaiTro_id = VaiTro_id;
        this.TrangThai = TrangThai;
    }
    public int getTaiKhoan_id(){
        return TaiKhoan_id;
    }
    public void setTaiKhoan_id(int TaiKhoan_id){
        this.TaiKhoan_id = TaiKhoan_id;
    }
    public String getTenDN(){
        return TenDN;
    }
    public void setTenDN(String TenDN){
        this.TenDN = TenDN;
    }
    public String getMatKhau() {
        return MatKhau;
    }
    public void setMatKhau(String MatKhau){
        this.MatKhau = MatKhau;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public int getVaiTro_id(){
        return VaiTro_id;
    }
    public void setVaiTro_id(int VaiTro_id){
        this.VaiTro_id = VaiTro_id;
    }
    public String getTrangThai(){
        return TrangThai;
    }
    public void setTrangThai(String TrangThai){
        this.TrangThai = TrangThai;
    }
}

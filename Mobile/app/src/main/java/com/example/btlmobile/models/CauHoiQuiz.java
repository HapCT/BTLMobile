package com.example.btlmobile.models;

public class CauHoiQuiz {
    private int cauHoi_id;
    private int quiz_id;
    private String noiDung;
    private String dapAnA;
    private String dapAnB;
    private String dapAnC;
    private String dapAnD;
    private String dapAnDung;

    public CauHoiQuiz() {}

    public int getCauHoi_id() { return cauHoi_id; }
    public void setCauHoi_id(int cauHoi_id) { this.cauHoi_id = cauHoi_id; }

    public int getQuiz_id() { return quiz_id; }
    public void setQuiz_id(int quiz_id) { this.quiz_id = quiz_id; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getDapAnA() { return dapAnA; }
    public void setDapAnA(String dapAnA) { this.dapAnA = dapAnA; }

    public String getDapAnB() { return dapAnB; }
    public void setDapAnB(String dapAnB) { this.dapAnB = dapAnB; }

    public String getDapAnC() { return dapAnC; }
    public void setDapAnC(String dapAnC) { this.dapAnC = dapAnC; }

    public String getDapAnD() { return dapAnD; }
    public void setDapAnD(String dapAnD) { this.dapAnD = dapAnD; }

    public String getDapAnDung() { return dapAnDung; }
    public void setDapAnDung(String dapAnDung) { this.dapAnDung = dapAnDung; }
}

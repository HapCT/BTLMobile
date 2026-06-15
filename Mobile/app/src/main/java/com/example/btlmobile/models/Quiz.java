package com.example.btlmobile.models;

public class Quiz {
    private int quiz_id;
    private String tenQuiz;
    private int loaiQuiz_id;

    public Quiz() {}

    public Quiz(int quiz_id, String tenQuiz, int loaiQuiz_id) {
        this.quiz_id = quiz_id;
        this.tenQuiz = tenQuiz;
        this.loaiQuiz_id = loaiQuiz_id;
    }

    public int getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getTenQuiz() {
        return tenQuiz;
    }

    public void setTenQuiz(String tenQuiz) {
        this.tenQuiz = tenQuiz;
    }

    public int getLoaiQuiz_id() {
        return loaiQuiz_id;
    }

    public void setLoaiQuiz_id(int loaiQuiz_id) {
        this.loaiQuiz_id = loaiQuiz_id;
    }

    @Override
    public String toString() {
        return tenQuiz;
    }
}

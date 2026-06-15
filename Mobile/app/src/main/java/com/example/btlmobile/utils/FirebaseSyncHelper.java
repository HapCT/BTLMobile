package com.example.btlmobile.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.btlmobile.dao.BaiNgheDAO;
import com.example.btlmobile.dao.CauHoiQuizDAO;
import com.example.btlmobile.dao.NguPhapDAO;
import com.example.btlmobile.dao.QuizDAO;
import com.example.btlmobile.dao.TaiKhoanDAO;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.models.BaiHocNguPhap;
import com.example.btlmobile.models.BaiNghe;
import com.example.btlmobile.models.CauHoiQuiz;
import com.example.btlmobile.models.Quiz;
import com.example.btlmobile.models.TaiKhoan;
import com.example.btlmobile.models.TuVung;
import com.example.btlmobile.models.ChuDeTuVung;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseSyncHelper {
    private Context context;
    private TaiKhoanDAO taiKhoanDAO;
    private TuVungDAO tuVungDAO;
    private BaiNgheDAO baiNgheDAO;
    private QuizDAO quizDAO;
    private NguPhapDAO nguPhapDAO;
    private CauHoiQuizDAO cauHoiQuizDAO;

    public FirebaseSyncHelper(Context context) {
        this.context = context;
        this.taiKhoanDAO = new TaiKhoanDAO(context);
        this.tuVungDAO = new TuVungDAO(context);
        this.nguPhapDAO = new NguPhapDAO(context);
        this.baiNgheDAO = new BaiNgheDAO(context);
        this.quizDAO = new QuizDAO(context);
        this.cauHoiQuizDAO = new CauHoiQuizDAO(context);
    }

    public void startSync() {
        syncTaiKhoan();
        syncChuDeTuVung();
        syncTuVung();
        syncNguPhap();
        syncBaiNghe();
        syncQuiz();
        syncCauHoiQuiz();
    }

    private void syncChuDeTuVung() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChuDeTuVung");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("chuDeTuVung_id").getValue(Integer.class);
                            if (id == null) id = data.child("ChuDeTuVung_id").getValue(Integer.class);
                            
                            String ten = data.child("tenChuDe").getValue(String.class);
                            if (ten == null) ten = data.child("TenChuDe").getValue(String.class);

                            String anh = data.child("hinhAnh").getValue(String.class);
                            if (anh == null) anh = data.child("HinhAnh").getValue(String.class);

                            if (id != null && !tuVungDAO.checkChuDeExists(id)) {
                                ChuDeTuVung cd = new ChuDeTuVung(id, ten, anh);
                                tuVungDAO.insertChuDe(cd);
                                Log.d("SYNC", "Đã đồng bộ chủ đề mới: " + ten);
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi ChuDeTuVung: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void syncTaiKhoan() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            String tenDN = data.child("tenDN").getValue(String.class);
                            if (tenDN == null) tenDN = data.child("TenDangNhap").getValue(String.class);

                            if (tenDN != null) {
                                String email = data.child("email").getValue(String.class);
                                if (email == null) email = data.child("Email").getValue(String.class);

                                Integer role = data.child("vaiTro_id").getValue(Integer.class);
                                if (role == null) role = data.child("VaiTro_id").getValue(Integer.class);

                                String status = data.child("trangThai").getValue(String.class);
                                if (status == null) status = data.child("TrangThai").getValue(String.class);

                                if (!taiKhoanDAO.checkTenDN(tenDN)) {
                                    TaiKhoan tk = new TaiKhoan();
                                    tk.setTenDN(tenDN);
                                    tk.setEmail(email);
                                    tk.setMatKhau("123456"); // Mật khẩu mặc định local
                                    tk.setVaiTro_id(role != null ? role : 2);
                                    tk.setTrangThai(status != null ? status : "Hoạt động");

                                    taiKhoanDAO.insertTaiKhoan(tk);
                                    Log.d("SYNC", "Đã đồng bộ tài khoản mới: " + tenDN + " (" + email + ")");
                                } else {
                                    if (email != null && role != null) {
                                        taiKhoanDAO.updateVaiTroVaEmail(tenDN, email, role);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi đồng bộ tài khoản: " + e.getMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("SYNC", "Firebase Sync Cancelled: " + error.getMessage());
            }
        });
    }

    private void syncTuVung() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TuVung");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("tuVung_id").getValue(Integer.class);
                            if (id == null) id = data.child("TuVung_id").getValue(Integer.class);

                            if (id != null && !tuVungDAO.checkTuVungExists(id)) {
                                Integer idCD = data.child("chuDeTuVung_id").getValue(Integer.class);
                                if (idCD == null) idCD = data.child("ChuDeTuVung_id").getValue(Integer.class);

                                String tu = data.child("tuVung").getValue(String.class);
                                if (tu == null) tu = data.child("TuVung").getValue(String.class);

                                String nghia = data.child("nghia").getValue(String.class);
                                if (nghia == null) nghia = data.child("Nghia").getValue(String.class);

                                String phienAm = data.child("phienAm").getValue(String.class);
                                if (phienAm == null) phienAm = data.child("PhienAm").getValue(String.class);

                                String hinh = data.child("hinhAnh").getValue(String.class);
                                if (hinh == null) hinh = data.child("HinhAnh").getValue(String.class);

                                TuVung tv = new TuVung();
                                tv.setTuVung_id(id);
                                tv.setChuDeTuVung_id(idCD != null ? idCD : 0);
                                tv.setTuVung(tu);
                                tv.setNghia(nghia);
                                tv.setPhienAm(phienAm);
                                tv.setHinhAnh(hinh);

                                tuVungDAO.insertTuVung(tv);
                                Log.d("SYNC", "Đã đồng bộ từ vựng mới: " + tu);
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi TuVung: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
    private void syncNguPhap() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BaiHocNguPhap");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("baiHocNP_id").getValue(Integer.class);
                            if (id == null) id = data.child("BaiHocNP_id").getValue(Integer.class);

                            if (id != null && !nguPhapDAO.checkBaiNguPhapExists(id)) {
                                String tieuDe = data.child("tieuDe").getValue(String.class);
                                if (tieuDe == null) tieuDe = data.child("TieuDe").getValue(String.class);

                                String noiDung = data.child("noiDungBai").getValue(String.class);
                                if (noiDung == null) noiDung = data.child("NoiDungBai").getValue(String.class);

                                String capDo = data.child("capDo").getValue(String.class);
                                if (capDo == null) capDo = data.child("CapDo").getValue(String.class);

                                BaiHocNguPhap bh = new BaiHocNguPhap();
                                bh.setBaiHocNP_id(id);
                                bh.setTieuDe(tieuDe);
                                bh.setNoiDungBai(noiDung);
                                bh.setCapDo(capDo);

                                nguPhapDAO.insertBaiHoc(bh);
                                Log.d("SYNC", "Đã đồng bộ ngữ pháp: " + tieuDe);
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi NguPhap: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void syncBaiNghe() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BaiNghe");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("baiNghe_id").getValue(Integer.class);
                            if (id == null) id = data.child("BaiNghe_id").getValue(Integer.class);

                            if (id != null && !baiNgheDAO.checkBaiNgheExists(id)) {
                                String tieuDe = data.child("tieuDe").getValue(String.class);
                                if (tieuDe == null) tieuDe = data.child("TieuDe").getValue(String.class);

                                String moTa = data.child("moTa").getValue(String.class);
                                if (moTa == null) moTa = data.child("MoTa").getValue(String.class);

                                String url = data.child("url").getValue(String.class);
                                if (url == null) url = data.child("Url").getValue(String.class);

                                String hinh = data.child("hinhAnh").getValue(String.class);
                                if (hinh == null) hinh = data.child("HinhAnh").getValue(String.class);

                                BaiNghe bn = new BaiNghe();
                                bn.setBaiNghe_id(id);
                                bn.setTieuDe(tieuDe);
                                bn.setMoTa(moTa);
                                bn.setAudio(url);
                                bn.setHinhAnh(hinh);

                                baiNgheDAO.insertBaiNghe(bn);
                                Log.d("SYNC", "Đã đồng bộ bài nghe: " + tieuDe);
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi BaiNghe: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void syncQuiz() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Quiz");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("quiz_id").getValue(Integer.class);
                            if (id == null) id = data.child("Quiz_id").getValue(Integer.class);

                            if (id != null && !quizDAO.checkQuizExists(id)) {
                                String ten = data.child("tenQuiz").getValue(String.class);
                                if (ten == null) ten = data.child("TenQuiz").getValue(String.class);

                                Integer loaiId = data.child("loaiQuiz_id").getValue(Integer.class);
                                if (loaiId == null) loaiId = data.child("LoaiQuiz_id").getValue(Integer.class);

                                Quiz q = new Quiz();
                                q.setQuiz_id(id);
                                q.setTenQuiz(ten);
                                q.setLoaiQuiz_id(loaiId != null ? loaiId : 1);

                                quizDAO.insertQuiz(q);
                                Log.d("SYNC", "Đã đồng bộ Quiz: " + ten);
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi Quiz: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void syncCauHoiQuiz() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CauHoiQuiz");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("cauHoi_id").getValue(Integer.class);
                            if (id == null) id = data.child("CauHoi_id").getValue(Integer.class);

                            if (id != null && !cauHoiQuizDAO.checkCauHoiExists(id)) {
                                Integer qId = data.child("quiz_id").getValue(Integer.class);
                                if (qId == null) qId = data.child("Quiz_id").getValue(Integer.class);

                                String cauHoi = data.child("cauHoi").getValue(String.class);
                                if (cauHoi == null) cauHoi = data.child("CauHoi").getValue(String.class);

                                String a = data.child("phuongAnA").getValue(String.class);
                                if (a == null) a = data.child("PhuongAnA").getValue(String.class);
                                String b = data.child("phuongAnB").getValue(String.class);
                                if (b == null) b = data.child("PhuongAnB").getValue(String.class);
                                String c = data.child("phuongAnC").getValue(String.class);
                                if (c == null) c = data.child("PhuongAnC").getValue(String.class);
                                String d = data.child("phuongAnD").getValue(String.class);
                                if (d == null) d = data.child("PhuongAnD").getValue(String.class);

                                String dung = data.child("dapAnDung").getValue(String.class);
                                if (dung == null) dung = data.child("DapAnDung").getValue(String.class);

                                CauHoiQuiz ch = new CauHoiQuiz();
                                ch.setCauHoi_id(id);
                                ch.setQuiz_id(qId != null ? qId : 0);
                                ch.setNoiDung(cauHoi);
                                ch.setDapAnA(a);
                                ch.setDapAnB(b);
                                ch.setDapAnC(c);
                                ch.setDapAnD(d);
                                ch.setDapAnDung(dung);

                                cauHoiQuizDAO.insertCauHoi(ch);
                                Log.d("SYNC", "Đã đồng bộ câu hỏi ID: " + id);
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi CauHoiQuiz: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}

package com.example.btlmobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.btlmobile.dao.BaiNgheDAO;
import com.example.btlmobile.dao.CauHoiQuizDAO;
import com.example.btlmobile.dao.NguPhapDAO;
import com.example.btlmobile.dao.QuizDAO;
import com.example.btlmobile.dao.TaiKhoanDAO;
import com.example.btlmobile.dao.TuVungDAO;
import com.example.btlmobile.database.DatabaseHandler;
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
        syncNguoiDung();
        syncChuDeTuVung();
        syncTuVung();
        syncNguPhap();
        syncBaiNghe();
        syncQuiz();
        syncCauHoiQuiz();
        syncLichSuQuiz();
        syncLichSuNguPhap();
        syncLichSuLuyenNghe();
        syncTuVungYeuThich();
    }

    private void syncNguoiDung() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NguoiDung");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer tkId = data.child("taiKhoan_id").getValue(Integer.class);
                            if (tkId == null) tkId = Integer.parseInt(data.getKey());

                            String hoTen = data.child("hoTen").getValue(String.class);
                            String ngaySinh = data.child("ngaySinh").getValue(String.class);
                            String gioiTinh = data.child("gioiTinh").getValue(String.class);

                            if (tkId != null) {
                                com.example.btlmobile.models.NguoiDung ndLocal = taiKhoanDAO.getNguoiDungByTaiKhoan(tkId);
                                if (ndLocal == null) {
                                    taiKhoanDAO.insertNguoiDung(tkId, hoTen, ngaySinh, gioiTinh);
                                } else {
                                    ndLocal.setHoTen(hoTen);
                                    ndLocal.setNgaySinh(ngaySinh);
                                    ndLocal.setGioiTinh(gioiTinh);
                                    taiKhoanDAO.updateNguoiDung(ndLocal);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi đồng bộ NguoiDung: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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
                            String anh = data.child("hinhAnh").getValue(String.class);

                            if (id != null && !tuVungDAO.checkChuDeExists(id)) {
                                ChuDeTuVung cd = new ChuDeTuVung(id, ten, anh);
                                tuVungDAO.insertChuDe(cd);
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
                                Integer role = data.child("vaiTro_id").getValue(Integer.class);
                                String status = data.child("trangThai").getValue(String.class);

                                if (!taiKhoanDAO.checkTenDN(tenDN)) {
                                    TaiKhoan tk = new TaiKhoan();
                                    tk.setTenDN(tenDN);
                                    tk.setEmail(email);
                                    tk.setMatKhau("123456");
                                    tk.setVaiTro_id(role != null ? role : 2);
                                    tk.setTrangThai(status != null ? status : "Hoạt động");
                                    taiKhoanDAO.insertTaiKhoan(tk);
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
            public void onCancelled(DatabaseError error) {}
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
                            if (id != null && !tuVungDAO.checkTuVungExists(id)) {
                                TuVung tv = new TuVung();
                                tv.setTuVung_id(id);
                                tv.setChuDeTuVung_id(data.child("chuDeTuVung_id").getValue(Integer.class));
                                tv.setTuVung(data.child("tuVung").getValue(String.class));
                                tv.setNghia(data.child("nghia").getValue(String.class));
                                tv.setPhienAm(data.child("phienAm").getValue(String.class));
                                tv.setHinhAnh(data.child("hinhAnh").getValue(String.class));
                                tuVungDAO.insertTuVung(tv);
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
                            if (id != null && !nguPhapDAO.checkBaiNguPhapExists(id)) {
                                BaiHocNguPhap bh = new BaiHocNguPhap();
                                bh.setBaiHocNP_id(id);
                                bh.setTieuDe(data.child("tieuDe").getValue(String.class));
                                bh.setNoiDungBai(data.child("noiDungBai").getValue(String.class));
                                bh.setCapDo(data.child("capDo").getValue(String.class));
                                nguPhapDAO.insertBaiHoc(bh);
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
                            if (id != null && !baiNgheDAO.checkBaiNgheExists(id)) {
                                BaiNghe bn = new BaiNghe();
                                bn.setBaiNghe_id(id);
                                bn.setTieuDe(data.child("tieuDe").getValue(String.class));
                                bn.setMoTa(data.child("moTa").getValue(String.class));
                                bn.setAudio(data.child("url").getValue(String.class));
                                bn.setHinhAnh(data.child("hinhAnh").getValue(String.class));
                                baiNgheDAO.insertBaiNghe(bn);
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
                            if (id != null && !quizDAO.checkQuizExists(id)) {
                                Quiz q = new Quiz();
                                q.setQuiz_id(id);
                                q.setTenQuiz(data.child("tenQuiz").getValue(String.class));
                                q.setLoaiQuiz_id(data.child("loaiQuiz_id").getValue(Integer.class));
                                quizDAO.insertQuiz(q);
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
                            if (id != null && !cauHoiQuizDAO.checkCauHoiExists(id)) {
                                CauHoiQuiz ch = new CauHoiQuiz();
                                ch.setCauHoi_id(id);
                                ch.setQuiz_id(data.child("quiz_id").getValue(Integer.class));
                                ch.setNoiDung(data.child("noiDung").getValue(String.class));
                                ch.setDapAnA(data.child("dapAnA").getValue(String.class));
                                ch.setDapAnB(data.child("dapAnB").getValue(String.class));
                                ch.setDapAnC(data.child("dapAnC").getValue(String.class));
                                ch.setDapAnD(data.child("dapAnD").getValue(String.class));
                                ch.setDapAnDung(data.child("dapAnDung").getValue(String.class));
                                cauHoiQuizDAO.insertCauHoi(ch);
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

    private void syncLichSuQuiz() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LichSuQuiz");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("lichSu_id").getValue(Integer.class);
                            if (id != null) {
                                Cursor cursor = new DatabaseHandler(context).getCursor("SELECT * FROM LichSuQuiz WHERE LichSu_id = ?", new String[]{String.valueOf(id)});
                                if (cursor == null || cursor.getCount() == 0) {
                                    ContentValues values = new ContentValues();
                                    values.put("LichSu_id", id);
                                    values.put("TaiKhoan_id", data.child("taiKhoan_id").getValue(Integer.class));
                                    values.put("Quiz_id", data.child("quiz_id").getValue(Integer.class));
                                    values.put("Diem", data.child("diem").getValue(Integer.class));
                                    values.put("NgayLam", data.child("ngayLam").getValue(String.class));
                                    new DatabaseHandler(context).insert("LichSuQuiz", values);
                                }
                                if (cursor != null) cursor.close();
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi LichSuQuiz: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void syncLichSuNguPhap() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LichSuHocNguPhap");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("lichSuNP_id").getValue(Integer.class);
                            if (id != null) {
                                Cursor cursor = new DatabaseHandler(context).getCursor("SELECT * FROM LichSuHocNguPhap WHERE LichSuNP_id = ?", new String[]{String.valueOf(id)});
                                if (cursor == null || cursor.getCount() == 0) {
                                    ContentValues values = new ContentValues();
                                    values.put("LichSuNP_id", id);
                                    values.put("TaiKhoan_id", data.child("taiKhoan_id").getValue(Integer.class));
                                    values.put("BaiHocNP_id", data.child("baiHocNP_id").getValue(Integer.class));
                                    values.put("NgayHoc", data.child("ngayHoc").getValue(String.class));
                                    new DatabaseHandler(context).insert("LichSuHocNguPhap", values);
                                }
                                if (cursor != null) cursor.close();
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi LichSuNguPhap: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void syncLichSuLuyenNghe() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LichSuLuyenNghe");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer id = data.child("lichSuLN_id").getValue(Integer.class);
                            if (id != null) {
                                Cursor cursor = new DatabaseHandler(context).getCursor("SELECT * FROM LichSuLuyenNghe WHERE LichSuLN_id = ?", new String[]{String.valueOf(id)});
                                if (cursor == null || cursor.getCount() == 0) {
                                    ContentValues values = new ContentValues();
                                    values.put("LichSuLN_id", id);
                                    values.put("TaiKhoan_id", data.child("taiKhoan_id").getValue(Integer.class));
                                    values.put("BaiNghe_id", data.child("baiNghe_id").getValue(Integer.class));
                                    values.put("NgayNghe", data.child("ngayNghe").getValue(String.class));
                                    new DatabaseHandler(context).insert("LichSuLuyenNghe", values);
                                }
                                if (cursor != null) cursor.close();
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi LichSuLuyenNghe: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void syncTuVungYeuThich() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TuVungYeuThich");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new Thread(() -> {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Integer tkId = data.child("TaiKhoan_id").getValue(Integer.class);
                            Integer tvId = data.child("TuVung_id").getValue(Integer.class);
                            
                            // Hỗ trợ cả key lowercase nếu Firebase schema bị lệch
                            if (tkId == null) tkId = data.child("taiKhoan_id").getValue(Integer.class);
                            if (tvId == null) tvId = data.child("tuVung_id").getValue(Integer.class);

                            if (tkId != null && tvId != null) {
                                Cursor cursor = new DatabaseHandler(context).getCursor("SELECT * FROM TuVungYeuThich WHERE TaiKhoan_id = ? AND TuVung_id = ?", new String[]{String.valueOf(tkId), String.valueOf(tvId)});
                                if (cursor == null || cursor.getCount() == 0) {
                                    ContentValues values = new ContentValues();
                                    values.put("TaiKhoan_id", tkId);
                                    values.put("TuVung_id", tvId);
                                    new DatabaseHandler(context).insert("TuVungYeuThich", values);
                                }
                                if (cursor != null) cursor.close();
                            }
                        } catch (Exception e) {
                            Log.e("SYNC", "Lỗi TuVungYeuThich: " + e.getMessage());
                        }
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}

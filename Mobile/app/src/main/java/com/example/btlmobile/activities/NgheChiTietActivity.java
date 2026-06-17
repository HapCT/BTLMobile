package com.example.btlmobile.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlmobile.R;
import com.example.btlmobile.models.BaiNghe;

public class NgheChiTietActivity extends AppCompatActivity {

    private TextView tvTieuDe, tvMoTa, tvCurrentTime, tvTotalTime;
    private ImageButton btnPlayPause;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private BaiNghe baiNghe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghe_chi_tiet);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        baiNghe = (BaiNghe) getIntent().getSerializableExtra("baiNghe");

        initViews();
        setupPlayer();
    }

    private void initViews() {
        tvTieuDe = findViewById(R.id.tvChiTietTieuDe);
        tvMoTa = findViewById(R.id.tvChiTietMoTa);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        seekBar = findViewById(R.id.seekBarAudio);

        if (baiNghe != null) {
            tvTieuDe.setText(baiNghe.getTieuDe());
            tvMoTa.setText(baiNghe.getMoTa());
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(baiNghe.getTieuDe());
        }
    }

    private void setupPlayer() {
        // Giả sử audio để trong folder raw hoặc link URL. 
        // Ở đây demo check link/resource
        try {
            // Thử lấy resource id từ tên file (không bao gồm extension)
            String audioFileName = baiNghe.getAudio();
            if (audioFileName != null && audioFileName.contains(".")) {
                audioFileName = audioFileName.substring(0, audioFileName.lastIndexOf("."));
            }
            int resId = getResources().getIdentifier(audioFileName, "raw", getPackageName());
            
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId);
            } else {
                // Thử load qua URL nếu là path/URL
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(baiNghe.getAudio());
                mediaPlayer.prepare();
            }

            tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
            seekBar.setMax(mediaPlayer.getDuration());

            btnPlayPause.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mediaPlayer.start();
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    updateSeekBar();
                    saveHistory();
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        } catch (Exception e) {
            Toast.makeText(this, "Không thể phát âm thanh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void saveHistory() {
        android.content.SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sp.getInt("taiKhoanId", -1);
        if (userId != -1 && baiNghe != null) {
            com.example.btlmobile.database.DatabaseHandler db = new com.example.btlmobile.database.DatabaseHandler(this);
            android.content.ContentValues values = new android.content.ContentValues();
            values.put("TaiKhoan_id", userId);
            values.put("BaiNghe_id", baiNghe.getBaiNghe_id());
            values.put("NgayNghe", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            db.insert("LichSuLuyenNghe", values);
            
            // Sync Firebase
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("LichSuLuyenNghe")
                .child(userId + "_" + baiNghe.getBaiNghe_id())
                .setValue(new com.example.btlmobile.models.LichSuLuyenNghe(userId, baiNghe.getBaiNghe_id(), values.getAsString("NgayNghe")));
        }
    }

    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

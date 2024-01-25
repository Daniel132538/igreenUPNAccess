package com.example.ecoproyect.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Looper;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.ecoproyect.R;

public class CouponDialog {
    private Dialog dialog;
    private TextView txtCodigo;
    private TextView txtCountDownTimer;
    private CountDownTimer timer;

    public CouponDialog(Dialog dialog, String code) {
        this.dialog = dialog;
        initializeViews(code);
        setupCountdownTimer();
    }

    private void initializeViews(String code) {
        txtCodigo = dialog.findViewById(R.id.txtCode);
        txtCodigo.setText(code);
        txtCountDownTimer = dialog.findViewById(R.id.txt_coupon_countdown_timer);
    }

    private void setupCountdownTimer() {
        long totalTimeInMillis = 2 * 60 * 1000; // 2 minutos en milisegundos

        timer = new CountDownTimer(totalTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long minutes = secondsRemaining / 60;
                long seconds = secondsRemaining % 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                txtCountDownTimer.setText(time);
            }

            @Override
            public void onFinish() {
                txtCountDownTimer.setText("00:00");
                dismissDialog();
            }
        };

        timer.start();
    }

    private void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}



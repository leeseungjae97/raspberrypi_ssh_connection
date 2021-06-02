package com.example.AndroidSSHWithRaspberryPi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.AndroidSSHWithRaspberryPi.Dialog.ConnectionDialog;

import maes.tech.intentanim.CustomIntent;

public class IoTMainPage extends AppCompatActivity implements ConnectionDialog.ClickConfirm {
    public interface GetPi {
        ConnectPi getPi();
    }

    private ProgressBar initLoading;
    public static GetPi getPi;
    private BlindController blindController;
    private ConnectPi pi;

    private boolean PROGRESSING = false;
    private ConstraintLayout loading;
    private TextView content;
    private int progressDot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_main_page);

        initLoading = findViewById(R.id.init_loading);
        loading = findViewById(R.id.loading_layout);
        content = findViewById(R.id.content);

        pi = getPi.getPi();
        blindController = pi.getBlindController();

        setStatusBarColor();
    }

    public void setStatusBarColor() {
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.main));
        initLoading.setVisibility(View.GONE);
    }

    public void progressText(String message) {
        loading.setVisibility(View.VISIBLE);
        content.setText(message);
        StringBuilder contentBuilder = new StringBuilder(message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (PROGRESSING) {
                        if (progressDot == 3) {
                            progressDot = 0;
                            contentBuilder.replace(contentBuilder.indexOf("."), contentBuilder.toString().length(), "");
                        }
                        Thread.sleep(1000);
                        contentBuilder.append(".");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                content.setText(contentBuilder.toString());
                            }
                        });
                        progressDot++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void blindDown(View view) {
        PROGRESSING = true;
        progressText("작동중입니다.");
        blindController.blindDown(pi.getSsh());
    }

    public void blindStop(View view) {
        PROGRESSING = false;
        loading.setVisibility(View.GONE);
        blindController.blindStop(pi.getSsh());
    }

//    public void refresh(View view) {
//        PROGRESSING = false;
//        loading.setVisibility(View.GONE);
//        blindController.refresh(pi.getSsh());
//    }

    public void blindUp(View view) {
        PROGRESSING = true;
        progressText("작동중입니다.");
        blindController.blindUp(pi.getSsh());
    }

    public void backPressed(View view) {
        new ConnectionDialog(IoTMainPage.this, IoTMainPage.this, ConnectionDialog.CLOSE);
    }

    @Override
    public void clickClose() {
        super.onBackPressed();
        PROGRESSING = false;
        pi.closeConnection(pi.getSsh());
        CustomIntent.customType(this,"right-to-left");
        finish();
    }
}
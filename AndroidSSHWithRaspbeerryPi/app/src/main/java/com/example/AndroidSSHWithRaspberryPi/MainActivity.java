package com.example.AndroidSSHWithRaspberryPi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ConnectPi pi;
    private BlindController blindController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor();

        pi = new ConnectPi();
        blindController = pi.getBlindController();
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarColor() {
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.white));
    }
    public void blindDown(View view) {
        blindController.blindDown(pi.getSsh());
    }

    public void blindStop(View view) {
        blindController.blindStop(pi.getSsh());
    }

    public void refresh(View view) {
        blindController.refresh(pi.getSsh());
    }

    public void blindUp(View view) {
        blindController.blindUp(pi.getSsh());
    }
}
package com.example.AndroidSSHWithRaspberryPi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ConnectPi connectPi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor();
        connectPi = new ConnectPi();
    }
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarColor() {
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.white));
    }
    public void blindDown(View view) {
        connectPi.blindDown(connectPi.getChannel(), connectPi.getSession());
    }

    public void blindStop(View view) {
        connectPi.blindStop(connectPi.getChannel(), connectPi.getSession());
    }

    public void refresh(View view) {
        connectPi.refresh(connectPi.getChannel(), connectPi.getSession());
    }

    public void blindUp(View view) {
        connectPi.blindUp(connectPi.getChannel(), connectPi.getSession());
    }
}
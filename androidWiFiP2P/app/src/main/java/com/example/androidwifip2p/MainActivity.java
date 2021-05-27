package com.example.androidwifip2p;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ConnectPi connectPi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor();
        connectPi = new ConnectPi(this);
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
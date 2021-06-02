package com.example.AndroidSSHWithRaspberryPi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.AndroidSSHWithRaspberryPi.Dialog.ConnectionDialog;
import com.example.AndroidSSHWithRaspberryPi.PiSettings.Properties;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.Objects;

import maes.tech.intentanim.CustomIntent;

public class InsertPiInfoPage extends AppCompatActivity implements AutoPermissionsListener, ConnectionDialog.ClickConfirm, IoTMainPage.GetPi {
    private EditText server;
    private EditText id;
    private EditText pw;

    private TextView WiFiName;
    private ImageView refresh;

    private Properties PI_PROPERTIES;

    private String SERVER;
    private String ID;
    private String PASSWORD;

    private ConnectPi pi;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(InsertPiInfoPage.this, requestCode, permissions, InsertPiInfoPage.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_pi_info_page);
        AutoPermissions.Companion.loadAllPermissions(InsertPiInfoPage.this, 101);
        PI_PROPERTIES       = new Properties(InsertPiInfoPage.this);

        server      = findViewById(R.id.pi_server);
        id          = findViewById(R.id.pi_id);
        pw          = findViewById(R.id.pi_pw);
        WiFiName    = findViewById(R.id.wifi_name);
        refresh     = findViewById(R.id.refresh_page);

        if(!PI_PROPERTIES.getID().equals("NO_VALUE")
            && !PI_PROPERTIES.getPW().equals("NO_VALUE")
            && !PI_PROPERTIES.getSERVER().equals("NO_VALUE")) {

            id.setText(PI_PROPERTIES.getID());
            pw.setText(PI_PROPERTIES.getPW());
            server.setText(PI_PROPERTIES.getSERVER());
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String ssid = wifiInfo.getSSID();
            WiFiName.setText(getApplicationContext().getResources().getString(R.string.phone_current_wifi) + ssid);
        }
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public void connect(View view) {
        SERVER      = Objects.requireNonNull(server.getText()).toString();
        ID          = Objects.requireNonNull(id.getText()).toString();
        PASSWORD    = Objects.requireNonNull(pw.getText()).toString();

        PI_PROPERTIES.saveProperties(SERVER, ID, PASSWORD);

        pi          = new ConnectPi(PI_PROPERTIES);

        if(pi.getSsh().getChannel() != null
                && pi.getSsh().getSession() != null) {
            new ConnectionDialog(InsertPiInfoPage.this, InsertPiInfoPage.this, ConnectionDialog.CONFIRM);
        }
        else {
            Log.e("okay", "okay");
            new ConnectionDialog(InsertPiInfoPage.this, InsertPiInfoPage.this, ConnectionDialog.OKAY);
        }
    }
    public void refreshPage(View view) {
        final Animation rotate = AnimationUtils.loadAnimation(InsertPiInfoPage.this, R.anim.rotate_360);
        refresh.startAnimation(rotate);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 1000);
        startActivity(new Intent(InsertPiInfoPage.this, InsertPiInfoPage.class));
        CustomIntent.customType(this,"fadein-to-fadeout");
        Toast.makeText(InsertPiInfoPage.this, "새로고침 하였습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

    @Override
    public void clickConfirm() {
        IoTMainPage.getPi = InsertPiInfoPage.this;
        startActivity(new Intent(InsertPiInfoPage.this, IoTMainPage.class));
    }

    @Override
    public void clickOkay() {

    }

    @Override
    public ConnectPi getPi() {
        return pi;
    }
}
package com.example.AndroidSSHWithRaspberryPi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private ProgressBar connectLoading;
    private CheckBox infoSaveCheckBox;

    private Properties PI_PROPERTIES;

    private String SERVER;
    private String ID;
    private String PASSWORD;
    private boolean isConnect = false;

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

        server          = findViewById(R.id.pi_server);
        id              = findViewById(R.id.pi_id);
        pw              = findViewById(R.id.pi_pw);
        WiFiName        = findViewById(R.id.wifi_name);
        refresh         = findViewById(R.id.refresh_page);
        connectLoading  = findViewById(R.id.loading_connect);
        infoSaveCheckBox = findViewById(R.id.save_insert_info);

        if(PI_PROPERTIES.getCHECK()
                && !PI_PROPERTIES.getSAVE_ID().equals("NO_VALUE")
                && !PI_PROPERTIES.getSAVE_PASSWORD().equals("NO_VALUE")
                && !PI_PROPERTIES.getSAVE_SERVER().equals("NO_VALUE")) {
            infoSaveCheckBox.setChecked(true);
            server.setText(PI_PROPERTIES.getSAVE_SERVER());
            id.setText(PI_PROPERTIES.getSAVE_ID());
            pw.setText(PI_PROPERTIES.getSAVE_PASSWORD());
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
        final String SERVER = server.getText().toString();
        final String ID = id.getText().toString();
        final String PW = pw.getText().toString();

        connectLoading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int threadRun = 0;
                while(!isConnect) {
                    pi = new ConnectPi(SERVER, ID, PW);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threadRun++;
                    if(pi.getSsh().channel != null && pi.getSsh().getSession() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectLoading.setVisibility(View.GONE);
                                new ConnectionDialog(InsertPiInfoPage.this, InsertPiInfoPage.this, ConnectionDialog.CONFIRM);
                                if(!PI_PROPERTIES.getCHECK()) {
                                    server.setText("");
                                    id.setText("");
                                    pw.setText("");
                                }
                            }
                        });
                        break;
                    }
                    if(threadRun > 10) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectLoading.setVisibility(View.GONE);
                                new ConnectionDialog(InsertPiInfoPage.this, InsertPiInfoPage.this, ConnectionDialog.OKAY);
                            }
                        });
                        break;
                    }
                }
            }
        }).start();
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
    public void checkSave(View view) {
        if(((CheckBox)view).isChecked()) {
            SERVER      = Objects.requireNonNull(server.getText()).toString();
            ID          = Objects.requireNonNull(id.getText()).toString();
            PASSWORD    = Objects.requireNonNull(pw.getText()).toString();

            PI_PROPERTIES.saveInfo(SERVER, ID, PASSWORD, true);
        }else {
            PI_PROPERTIES.deleteSaveInfo();
        }
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
        CustomIntent.customType(this,"left-to-right");
    }

    @Override
    public ConnectPi getPi() {
        return pi;
    }
}
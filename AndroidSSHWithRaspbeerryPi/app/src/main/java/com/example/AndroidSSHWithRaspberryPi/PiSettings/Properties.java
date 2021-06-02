package com.example.AndroidSSHWithRaspberryPi.PiSettings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Properties {
    public static final int PORT = 22;
    public static final String CHANNEL_TYPE = "exec";
    public static final String CONFIG_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    public static final String NO = "no";

    public SharedPreferences piPreferences;
    public SharedPreferences.Editor piEdit;
    public static final String PI_INTENT = "pi_intent";
    public static final String PI_PROPERTIES = "pi_properties";

    public String CHECK = "CHECK";
    public String SAVE_ID = "SAVE_ID";
    public String SAVE_SERVER = "SAVE_SERVER";
    public String SAVE_PASSWORD = "SAVE_PASSWORD";

    @SuppressLint("CommitPrefEdits")
    public Properties(Context context) {
        piPreferences = context.getSharedPreferences(PI_PROPERTIES, Context.MODE_PRIVATE);
        piEdit = piPreferences.edit();
    }
    public void saveInfo(String server, String id, String pw, boolean check) {
        piEdit.putString(SAVE_ID, id);
        piEdit.putString(SAVE_SERVER, server);
        piEdit.putString(SAVE_PASSWORD, pw);
        piEdit.putBoolean(CHECK, check);
        piEdit.apply();
        piEdit.commit();
    }
    public void deleteSaveInfo() {
        piEdit.putBoolean(CHECK, false);
        piEdit.remove(SAVE_ID);
        piEdit.remove(SAVE_SERVER);
        piEdit.remove(SAVE_PASSWORD);
        piEdit.apply();
        piEdit.commit();
    }

    public Boolean getCHECK() {
        return piPreferences.getBoolean(CHECK,false);
    }

    public String getSAVE_ID() {
        return piPreferences.getString(SAVE_ID,"NO_VALUE");
    }

    public String getSAVE_SERVER() {
        return piPreferences.getString(SAVE_SERVER,"NO_VALUE");
    }

    public String getSAVE_PASSWORD() {
        return piPreferences.getString(SAVE_PASSWORD,"NO_VALUE");
    }
}

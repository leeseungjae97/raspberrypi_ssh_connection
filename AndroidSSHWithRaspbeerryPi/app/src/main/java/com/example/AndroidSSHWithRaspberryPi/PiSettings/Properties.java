package com.example.AndroidSSHWithRaspberryPi.PiSettings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Properties {
    public SharedPreferences piPreferences;
    public SharedPreferences.Editor piEdit;
    public static final String PI_INTENT = "pi_intent";
    public static final String PI_PROPERTIES = "pi_properties";

    public String ID = "PI_ID";
    public String SERVER = "PI_SERVER";
    public String PASSWORD = "PI_PW";

    public static final int PORT = 22;
    public static final String CHANNEL_TYPE = "exec";

    public static final String CONFIG_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    public static final String NO = "no";

    @SuppressLint("CommitPrefEdits")
    public Properties(Context context) {
        piPreferences = context.getSharedPreferences(PI_PROPERTIES, Context.MODE_PRIVATE);
        piEdit = piPreferences.edit();
    }
    public void saveProperties(String server, String id, String pw) {
        piEdit.putString(SERVER, server);
        piEdit.putString(ID, id);
        piEdit.putString(PASSWORD, pw);
        piEdit.apply();
        piEdit.commit();
    }
    public void deleteProperties() {
        piEdit.clear();
        piEdit.apply();
        piEdit.commit();
    }
    public String getID() {
        return piPreferences.getString(ID, "NO_VALUE");
    }
    public String getPW() {
        return piPreferences.getString(PASSWORD, "NO_VALUE");
    }
    public String getSERVER() {
        return piPreferences.getString(SERVER, "NO_VALUE");
    }

    public void setID(String id) {
        piEdit.putString(ID, id);
        piEdit.apply();
        piEdit.commit();
    }

    public void setSERVER(String server) {
        piEdit.putString(SERVER, server);
        piEdit.apply();
        piEdit.commit();
    }

    public void setPASSWORD(String passowrd) {
        piEdit.putString(PASSWORD, passowrd);
        piEdit.apply();
        piEdit.commit();
    }

    @Override
    public String toString() {
        return "SERVER : " + piPreferences.getString(SERVER, "NO_VALUE") +
                "\nID : " + piPreferences.getString(ID, "NO_VALUE") +
                "\nPW : " + piPreferences.getString(PASSWORD, "NO_VALUE");
    }
}

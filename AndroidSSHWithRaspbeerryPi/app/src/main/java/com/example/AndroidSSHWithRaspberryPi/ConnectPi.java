package com.example.AndroidSSHWithRaspberryPi;

import android.util.Log;
import android.view.View;

import com.example.AndroidSSHWithRaspberryPi.PiSettings.Properties;
import com.example.AndroidSSHWithRaspberryPi.PiSettings.SSH;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.io.Serializable;

import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.BLIND_DOWN;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.BLIND_STOP;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.BLIND_UP;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.CONNECTING;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.REFRESH;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.Properties.CHANNEL_TYPE;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.Properties.CONFIG_HOST_KEY_CHECKING;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.Properties.NO;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.Properties.PORT;

public class ConnectPi {
    private SSH ssh;
    private JSch jSch;

    private int WAIT_CONNECTION_THREAD = 0;

    private BlindController blindController;

    private String SERVER;
    private String ID;
    private String PW;

    public ConnectPi(String SERVER, String ID, String PW) {
        this.SERVER = SERVER;
        this.ID = ID;
        this.PW = PW;

        this.ssh = new SSH();
        this.jSch = new JSch();

        connectPi().start();
    }

    public Thread connectPi() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!SERVER.equals("") || !ID.equals("")  || !PW.equals("")) {
                        ssh.setSession(jSch.getSession(ID, SERVER, PORT));
                        ssh.session.setPassword(PW);

                        java.util.Properties config = new java.util.Properties();
                        config.put(CONFIG_HOST_KEY_CHECKING, NO);
                        ssh.session.setConfig(config);
                        ssh.session.connect();

                        ssh.setChannel(ssh.session.openChannel(CHANNEL_TYPE));

                        ChannelExec channelExec = (ChannelExec) ssh.channel;
                        channelExec.setPty(true);

                        blindController = new BlindController(ssh, ConnectPi.this);

                        CONNECTING = false;
                        ifReconnectPi();
                    }
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ifReconnectPi() {
        if(BLIND_STOP || REFRESH) {
            BLIND_DOWN  = false;
            BLIND_UP    = false;
            REFRESH     = false;
            BLIND_STOP  = false;
        }
        if(BLIND_DOWN) {
            BLIND_UP    = false;
            blindController.blindDown(ssh);
        }
        if(BLIND_UP) {
            BLIND_DOWN  = false;
            blindController.blindUp(ssh);
        }
    }
    public SSH getSsh() {
        return ssh;
    }
    public void setSsh(SSH ssh) {
        this.ssh = ssh;
    }
    public BlindController getBlindController() {
        return blindController;
    }
    public void closeConnection(SSH ssh) {
        BLIND_UP = false;
        BLIND_DOWN = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    WAIT_CONNECTION_THREAD++;
                    if(WAIT_CONNECTION_THREAD > 10) {
                        break;
                    }
                }
                if (ssh.channel != null) {
                    ssh.channel.disconnect();
                }else {
                }
                if (ssh.session != null) {
                    ssh.session.disconnect();
                }else {
                }
            }
        }).start();
    }
    public void reConnection(SSH ssh, boolean reconnect) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    WAIT_CONNECTION_THREAD++;
                    if(WAIT_CONNECTION_THREAD > 10) {
                        break;
                    }
                }
                if (ssh.channel != null) {
                    ssh.channel.disconnect();
                }else {
                }
                if (ssh.session != null) {
                    ssh.session.disconnect();
                }else {
                }
                if(reconnect) {
                    connectPi().start();
                }
            }
        }).start();
    }
}

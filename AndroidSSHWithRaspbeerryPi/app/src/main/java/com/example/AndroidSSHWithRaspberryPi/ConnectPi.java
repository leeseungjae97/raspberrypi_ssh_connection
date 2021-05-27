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
    private Properties PI_PROPERTIES;

    private BlindController blindController;

    public ConnectPi(Properties properties) {
        PI_PROPERTIES        = properties;
        this.ssh = new SSH();
        this.jSch = new JSch();

        new Thread(new Runnable() {
            @Override
            public void run() {
                connectPi();
            }
        }).start();
    }

    public void connectPi() {
        try {
            if(!PI_PROPERTIES.getID().equals("NO_VALUE")
                    || !PI_PROPERTIES.getSERVER().equals("NO_VALUE")
                    || !PI_PROPERTIES.getPW().equals("NO_VALUE")) {

                String id       = PI_PROPERTIES.getID();
                String pw       = PI_PROPERTIES.getPW();
                String server   = PI_PROPERTIES.getSERVER();

                ssh.setSession(jSch.getSession(id, server, PORT));
                ssh.session.setPassword(pw);

                java.util.Properties config = new java.util.Properties();
                config.put(CONFIG_HOST_KEY_CHECKING, NO);
                ssh.session.setConfig(config);
                ssh.session.connect();

                Log.e("session_host", ssh.session.getHost());
                Log.e("session_user_name", ssh.session.getUserName());

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

    public void ifReconnectPi() {
        Log.e("BLIND_STOP", BLIND_STOP+"");
        Log.e("REFRESH", REFRESH+"");
        Log.e("BLIND_DOWN", BLIND_DOWN+"");
        Log.e("BLIND_UP", BLIND_UP+"");

        if(BLIND_STOP || REFRESH) {
            BLIND_DOWN = false;
            BLIND_UP = false;
            REFRESH = false;
            BLIND_STOP = false;
        }
        if(BLIND_DOWN) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ssh.session.getHost().equals("")) {
                        waitSec();
                    }
                    BLIND_UP = false;
                    blindController.blindDown(ssh);
                }
            }).start();
        }
        if(BLIND_UP) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ssh.session.getHost().equals("")) {
                        waitSec();
                    }
                    BLIND_DOWN = false;
                    blindController.blindUp(ssh);
                }
            }).start();
        }
    }
    public void waitSec() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public SSH getSsh() {
        waitSec();
        return ssh;
    }
    public void setSsh(SSH ssh) {
        this.ssh = ssh;
    }
    public BlindController getBlindController() {
        waitSec();
        return blindController;
    }
    public void closeConnection(SSH ssh) {
        BLIND_UP = false;
        BLIND_DOWN = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {

                    waitSec();

                    WAIT_CONNECTION_THREAD++;
                    if(WAIT_CONNECTION_THREAD > 10) {
                        Log.e("close fail timeout", "check connection");
                        break;
                    }
                }
                if (ssh.channel != null) {
                    Log.e("channel close", "channel close");
                    ssh.channel.disconnect();
                }else {
                    Log.e("channel null", "close fail");
                }
                if (ssh.session != null) {
                    Log.e("session close", "session close");
                    ssh.session.disconnect();
                }else {
                    Log.e("session null", "close fail");
                }
            }
        }).start();
    }
    public void closeConnection(SSH ssh, boolean reconnect) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {
                    Log.e("CLOSE_THREAD", WAIT_CONNECTION_THREAD +"");

                    waitSec();

                    WAIT_CONNECTION_THREAD++;
                    if(WAIT_CONNECTION_THREAD > 10) {
                        Log.e("close fail timeout", "check connection");
                        break;
                    }
                }
                if (ssh.channel != null) {
                    Log.e("channel close", "channel close");
                    ssh.channel.disconnect();
                }else {
                    Log.e("channel null", "close fail");
                }
                if (ssh.session != null) {
                    Log.e("session close", "session close");
                    ssh.session.disconnect();
                }else {
                    Log.e("session null", "close fail");
                }
                if(reconnect) {
                    connectPi();
                }
            }
        }).start();
    }
}

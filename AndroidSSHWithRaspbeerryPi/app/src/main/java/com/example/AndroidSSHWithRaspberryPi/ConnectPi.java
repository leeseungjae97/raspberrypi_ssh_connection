package com.example.AndroidSSHWithRaspberryPi;

import android.util.Log;

import com.example.AndroidSSHWithRaspberryPi.PiString.Properties;
import com.example.AndroidSSHWithRaspberryPi.PiString.SSH;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import static com.example.AndroidSSHWithRaspberryPi.PiString.Properties.CHANNEL_TYPE;
import static com.example.AndroidSSHWithRaspberryPi.PiString.Properties.ID;
import static com.example.AndroidSSHWithRaspberryPi.PiString.Properties.SERVER;
import static com.example.AndroidSSHWithRaspberryPi.PiString.Properties.PORT;
import static com.example.AndroidSSHWithRaspberryPi.PiString.Properties.CONFIG_HOST_KEY_CHECKING;
import static com.example.AndroidSSHWithRaspberryPi.PiString.Properties.NO;

import static com.example.AndroidSSHWithRaspberryPi.PiString.PiConduct.BLIND_DOWN;
import static com.example.AndroidSSHWithRaspberryPi.PiString.PiConduct.BLIND_STOP;
import static com.example.AndroidSSHWithRaspberryPi.PiString.PiConduct.BLIND_UP;
import static com.example.AndroidSSHWithRaspberryPi.PiString.PiConduct.REFRESH;
import static com.example.AndroidSSHWithRaspberryPi.PiString.PiConduct.CONNECTING;

public class ConnectPi {
    private SSH ssh;
    private JSch jSch;
    private int WAIT_CONNECTION_THREAD = 0;
    private BlindController blindController;

    public ConnectPi() {
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
            ssh.setSession(jSch.getSession(ID, SERVER, PORT));

            ssh.session.setPassword(Properties.PASSWORD);
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
        }
        if(BLIND_DOWN) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ssh.session.getHost().equals("")) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    BLIND_DOWN = false;
                    blindController.blindUp(ssh);
                }
            }).start();
        }
    }
    public SSH getSsh() {
        return ssh;
    }
    public BlindController getBlindController() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        return blindController;
    }
    public void closeConnection(SSH ssh) {
        BLIND_UP = false;
        BLIND_DOWN = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {
                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
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

                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
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

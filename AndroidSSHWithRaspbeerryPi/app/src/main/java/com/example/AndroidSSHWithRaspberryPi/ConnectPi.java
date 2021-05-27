package com.example.AndroidSSHWithRaspberryPi;

import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ConnectPi {
    private Channel channel;
    private Session session;
    private int CLOSE_THREAD = 0;
    private static boolean CONNECTING = false;

    private static boolean BLIND_UP = false;
    private static boolean BLIND_DOWN = false;
    private static boolean BLIND_STOP = false;
    private static boolean REFRESH = false;

    public ConnectPi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectPi();
            }
        }).start();
    }

    public Session getSession() {
        return session;
    }
    public Channel getChannel() {
        return channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    public void setSession(Session session) {
        this.session = session;
    }

    public void connectPi() {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession("pi", "192.168.35.75", 22);
            session.setPassword("1234");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Log.e("session_host", session.getHost());
            Log.e("session_user_name", session.getUserName());

            channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec) channel;
            channelExec.setPty(true);

            CONNECTING = false;
            if(BLIND_STOP || REFRESH) {
                BLIND_DOWN = false;
                BLIND_UP = false;
            }
            if(BLIND_DOWN) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (session.getHost().equals("")) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        BLIND_UP = false;
                        blindDown(channel, session);
                    }
                }).start();
            }
            if(BLIND_UP) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (session.getHost().equals("")) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        BLIND_DOWN = false;
                        blindUp(channel, session);
                    }
                }).start();
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection(Channel channel, Session session) {
        BLIND_UP = false;
        BLIND_DOWN = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {
                    Log.e("CLOSE_THREAD", CLOSE_THREAD+"");

                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                    CLOSE_THREAD++;
                    if(CLOSE_THREAD > 10) {
                        Log.e("close fail timeout", "check connection");
                        break;
                    }
                }
                if (channel != null) {
                    Log.e("channel close", "channel close");
                    channel.disconnect();
                }else {
                    Log.e("channel null", "close fail");
                }
                if (session != null) {
                    Log.e("session close", "session close");
                    session.disconnect();
                }else {
                    Log.e("session null", "close fail");
                }
            }
        }).start();
    }
    public void closeConnection(Channel channel, Session session, boolean reconnect) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(CONNECTING) {
                    Log.e("CLOSE_THREAD", CLOSE_THREAD+"");

                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                    CLOSE_THREAD++;
                    if(CLOSE_THREAD > 10) {
                        Log.e("close fail timeout", "check connection");
                        break;
                    }
                }
                if (channel != null) {
                    Log.e("channel close", "channel close");
                    channel.disconnect();
                }else {
                    Log.e("channel null", "close fail");
                }
                if (session != null) {
                    Log.e("session close", "session close");
                    session.disconnect();
                }else {
                    Log.e("session null", "close fail");
                }
                if(reconnect) {
                    connectPi();
                }
            }
        }).start();
    }
    public void blindDown(Channel channel, Session session){
        BLIND_DOWN = true;

        if(BLIND_UP) {
            BLIND_UP = false;
            closeConnection(channel, session, true);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(channel != null) {
                        try {
                            ChannelExec channelExec = (ChannelExec) channel;
                            channelExec.setPty(true);
                            channelExec.setCommand("python Desktop/servo_motor.py");
                            channelExec.connect();
                        } catch (JSchException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    public void blindUp(Channel channel, Session session){
        BLIND_UP = true;

        if(BLIND_DOWN) {
            BLIND_DOWN = false;
            closeConnection(channel, session, true);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(channel != null) {
                        try {
                            ChannelExec channelExec = (ChannelExec) channel;
                            channelExec.setPty(true);
                            channelExec.setCommand("python Desktop/servo_motor_reverse.py");
                            channelExec.connect();
                        } catch (JSchException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    public void blindStop(Channel channel, Session session){
        BLIND_STOP = true;
        closeConnection(channel, session, true);
    }
    public void refresh(Channel channel, Session session) {
        REFRESH = true;
        closeConnection(channel, session, true);
    }
}

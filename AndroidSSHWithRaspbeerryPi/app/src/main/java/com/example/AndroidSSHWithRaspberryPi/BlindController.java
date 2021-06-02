package com.example.AndroidSSHWithRaspberryPi;

import com.example.AndroidSSHWithRaspberryPi.PiSettings.PythonScript;
import com.example.AndroidSSHWithRaspberryPi.PiSettings.SSH;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.BLIND_DOWN;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.BLIND_STOP;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.BLIND_UP;
import static com.example.AndroidSSHWithRaspberryPi.PiSettings.PiConduct.REFRESH;

public class BlindController {
    private SSH ssh;
    private ConnectPi pi;

    public BlindController(SSH ssh, ConnectPi pi) {
        this.ssh = ssh;
        this.pi = pi;
    }

    public void blindDown(SSH ssh){
        BLIND_DOWN = true;

        if(pi != null) {
            if(BLIND_UP) {
                BLIND_UP = false;
                pi.reConnection(ssh, true);
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(ssh.channel != null) {
                            try {
                                ChannelExec channelExec = (ChannelExec) ssh.channel;
                                channelExec.setPty(true);
                                channelExec.setCommand(PythonScript.SERVO_MOTOR_PY);
                                channelExec.connect();
                            } catch (JSchException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }
    public void blindUp(SSH ssh){
        BLIND_UP = true;
        if(pi != null) {
            if(BLIND_DOWN) {
                BLIND_DOWN = false;
                pi.reConnection(ssh, true);
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(ssh.channel != null) {
                            try {
                                ChannelExec channelExec = (ChannelExec) ssh.channel;
                                channelExec.setPty(true);
                                channelExec.setCommand(PythonScript.SERVO_MOTOR_REVERSE_PY);
                                channelExec.connect();
                            } catch (JSchException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }
    public void blindStop(SSH ssh){
        BLIND_STOP = true;
        if(pi != null) {
            pi.reConnection(ssh, true);
        }
    }
    public void refresh(SSH ssh) {
        REFRESH = true;
        if(pi != null) {
            pi.reConnection(ssh, true);
        }
    }
}

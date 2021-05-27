package com.example.AndroidSSHWithRaspberryPi.PiSettings;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

import java.io.Serializable;

public class SSH {
    public Channel channel;
    public Session session;

    public SSH() { }

    public SSH(Channel channel, Session session) {
        this.channel = channel;
        this.session = session;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "SSH{" +
                "channel=" + channel +
                ", session=" + session +
                '}';
    }
}

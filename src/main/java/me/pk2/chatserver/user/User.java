package me.pk2.chatserver.user;

import me.pk2.chatserver.message.Message;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private long alive;
    public final String username;
    public ArrayList<Message> messagesInQueue;
    public String verification_serial = "";
    public User(String username) {
        this.username = username;
        this.messagesInQueue = new ArrayList<>();
        keepAlive();
    }

    public boolean isAlive() { return System.currentTimeMillis()<=alive; }
    public void keepAlive() { alive = System.currentTimeMillis()+30000; }
}
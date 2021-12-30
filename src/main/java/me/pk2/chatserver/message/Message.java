package me.pk2.chatserver.message;

import me.pk2.chatserver.user.User;

import java.io.Serializable;

public class Message implements Serializable {
    public final User user;
    public final String message;
    public Message(User user, String message) {
        this.user = user;
        this.message = message;
    }
}
package me.pk2.chatserver.packets;

import me.pk2.chatserver.message.Message;
import me.pk2.chatserver.user.User;

import java.util.List;

public class SCUpdatePacket extends Packet {
    public final List<Message> messages;
    public final List<User> users;
    public final String verification_serial;
    public SCUpdatePacket(List<Message> messages, List<User> users, String verification_serial) {
        this.messages = messages;
        this.users = users;
        this.verification_serial = verification_serial;
    }
}
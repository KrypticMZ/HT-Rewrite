package me.pk2.chatserver.packets;

public class CSChatPacket extends Packet {
    public final String username, message;
    public CSChatPacket(String username, String message) {
        this.username = username;
        this.message = message;
    }
}
package me.pk2.chatserver.packets;

public class CSKeepAlivePacket extends Packet {
    public final String username;
    public CSKeepAlivePacket(String username) {
        this.username = username;
    }
}
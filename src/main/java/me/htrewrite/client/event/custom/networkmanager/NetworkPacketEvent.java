package me.htrewrite.client.event.custom.networkmanager;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.network.Packet;

public class NetworkPacketEvent extends CustomEvent {
    private Packet packet;
    public final boolean reading;

    public NetworkPacketEvent(Packet packet, Era era, boolean reading) {
        super();
        this.setEra(era);
        this.packet = packet;
        this.reading = reading;
    }

    public Packet getPacket() { return packet; }
}
package me.htrewrite.client.event.custom.client;

import me.htrewrite.client.event.custom.CustomEvent;

public class ClientShutdownEvent extends CustomEvent {
    public final ShutdownType shutdownType;
    public ClientShutdownEvent(ShutdownType shutdownType) {
        super();
        this.shutdownType = shutdownType;
    }

    public static enum ShutdownType {
        CRASH, SHUTDOWN
    }
}
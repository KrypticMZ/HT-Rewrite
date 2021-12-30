package me.htrewrite.client.event.custom.client;

import me.htrewrite.client.event.custom.CustomEvent;

public class ClientKeyEvent extends CustomEvent {
    public boolean info;
    public boolean pressed;

    public ClientKeyEvent(boolean info, boolean pressed) {
        super();
        this.info = info;
        this.pressed = pressed;
    }
}
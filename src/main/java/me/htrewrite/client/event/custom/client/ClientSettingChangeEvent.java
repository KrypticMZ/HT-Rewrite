package me.htrewrite.client.event.custom.client;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.exeterimports.mcapi.settings.Setting;

public class ClientSettingChangeEvent extends CustomEvent {
    public final Setting setting;
    public ClientSettingChangeEvent(Setting setting) {
        super();
        this.setting = setting;
    }
}
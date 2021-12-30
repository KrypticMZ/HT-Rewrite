package me.htrewrite.client.event.custom.module;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.module.Module;

public class ModuleToggleEvent extends CustomEvent {
    public final Module module;
    public final boolean toggled;
    public ModuleToggleEvent(Module module, boolean toggled) {
        this.module = module;
        this.toggled = toggled;
    }
}
package me.htrewrite.exeterimports.mcapi.settings;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;
import me.htrewrite.client.module.Module;
import org.lwjgl.input.Keyboard;

public class BindSetting extends Setting {
    public final Module module;
    public BindSetting(Module module) {
        super("Bind", null);
        this.module = module;
    }

    public int getBind() { return module.getKey(); }
    public String getFormattedBind() { return Keyboard.getKeyName(getBind()); }

    public void bind(int key) {
        module.setKey(key);
        HTRewrite.EVENT_BUS.post(new ClientSettingChangeEvent(this));
    }

    public String getConfigLabel() { return "key"; }
}
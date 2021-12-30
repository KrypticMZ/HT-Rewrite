package me.htrewrite.exeterimports.mcapi.settings;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;

import java.util.function.Predicate;

public class ModeSetting extends Setting {
    public final BetterMode[] modes;
    public final int defaultIndex;
    private int i = 0;

    public ModeSetting(String label, String[] aliases, int defaultIndex, BetterMode[] modes) {
        super(label, aliases);
        this.modes = modes;
        this.defaultIndex = defaultIndex;
        this.i = defaultIndex;
    }

    public ModeSetting(String label, int defaultIndex, BetterMode[] modes) { this(label, null, defaultIndex, modes); }

    public String getValue() {
        return modes[i].mode;
    }
    public void setValue(int i) {
        this.i = i;
        if(i > modes.length-1 || i < 0)
            this.i = modes.length-1;
        HTRewrite.EVENT_BUS.post(new ClientSettingChangeEvent(this));
    }
    public int getI() { return i; }
    public BetterMode[] getModes() { return modes; }
    public BetterMode getModeByName(String name) {
        for(int i = 0; i < modes.length; i++)
            if(modes[i].mode.equalsIgnoreCase(name))
                return modes[i];
        return null;
    }
    public int getModeIndexByName(String name) {
        for(int i = 0; i < modes.length; i++)
            if(modes[i].mode.equalsIgnoreCase(name))
                return i;
        return -1;
    }

    public void increment() {
        i++;
        if(i > modes.length-1)
            i = 0;
    }

    public void decrement() {
        i--;
        if(i < 0)
            i = modes.length-1;
    }

    private Predicate<String> visibility;
    public ModeSetting setVisibility(Predicate<String> visibility) { this.visibility = visibility; return this; }
    public boolean isVisible() {
        if(visibility == null)
            return true;
        return visibility.test(this.getValue());
    }
}
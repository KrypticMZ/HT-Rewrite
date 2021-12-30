package me.htrewrite.client.clickgui.components.buttons.settings.bettermode;

public class BetterMode {
    public static BetterMode construct(String mode) { return new BetterMode(mode); }
    public static BetterMode[] construct(String... modes) {
        BetterMode[] betterModes = new BetterMode[modes.length];
        for(int i = 0; i < modes.length; i++)
            betterModes[i] = new BetterMode(modes[i]);

        return betterModes;
    }

    public String mode;
    public BetterMode(String mode) { this.mode = mode; }
}
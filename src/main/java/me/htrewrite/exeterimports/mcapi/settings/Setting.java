package me.htrewrite.exeterimports.mcapi.settings;

import me.htrewrite.exeterimports.mcapi.interfaces.Labeled;

public class Setting implements Labeled {
    private final String label;
    private final String[] aliases;

    public Setting(String label, String[] aliases) {
        this.label = label;
        this.aliases = aliases;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String[] getAliases() {
        return aliases;
    }
}
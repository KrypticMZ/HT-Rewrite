package me.htrewrite.exeterimports.mcapi.settings;

import java.util.function.Predicate;

public class SeparatorSetting extends Setting {
    public SeparatorSetting(String label, String[] aliases) {
        super(label, aliases);
    }

    private Predicate<String> visibility;
    public SeparatorSetting setVisibility(Predicate<String> visibility) { this.visibility = visibility; return this; }
    public boolean isVisible() {
        if(visibility == null)
            return true;
        return visibility.test(this.getLabel());
    }
}
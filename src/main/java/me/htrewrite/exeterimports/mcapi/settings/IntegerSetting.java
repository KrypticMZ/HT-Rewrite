package me.htrewrite.exeterimports.mcapi.settings;

public class IntegerSetting extends ValueSetting<Integer> {
    public IntegerSetting(String label, String[] aliases, Integer value, Integer minimum, Integer maximum) {
        super(label, aliases, value, minimum, maximum);
    }
    public IntegerSetting(String label, Integer value, Integer minimum, Integer maximum) {
        super(label, value, minimum, maximum);
    }
    public IntegerSetting(String label, String[] aliases, Integer value) {
        super(label, aliases, value);
    }

    @Override
    public void setValue(Number value) {super.setValue((int)Math.round(value.doubleValue()));}
}
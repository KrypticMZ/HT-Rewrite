package me.htrewrite.exeterimports.mcapi.settings;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;

import java.util.function.Predicate;

public class ValueSetting<T extends Number> extends Setting {
    public final T defaultValue;
    private final T minimum, maximum;
    private T value;

    private boolean clamp = true;

    public ValueSetting(String label, String[] aliases, T value, T minimum, T maximum) {
        super(label, aliases);
        this.value = value;
        this.defaultValue = value;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public ValueSetting(String label, T value, T minimum, T maximum) {
        this(label, null, value, minimum, maximum);
    }

    public ValueSetting(String label, String[] aliases, T value) {
        this(label, aliases, value, null, null);
        clamp = false;
    }

    public void setValue(Number value) {
        if (clamp) {
            if (this.value instanceof Float) {
                if (value.floatValue() > this.getMaximum().floatValue()) {
                    value = this.getMaximum();
                } else if (value.floatValue() < this.getMinimum().floatValue()) {
                    value = this.getMinimum();
                }
            } else if (this.value instanceof Double) {
                if (value.doubleValue() > this.getMaximum().doubleValue()) {
                    value = this.getMaximum();
                } else if (value.doubleValue() < this.getMinimum().doubleValue()) {
                    value = this.getMinimum();
                }
            } else if (this.value instanceof Integer) {
                if (value.intValue() > this.getMaximum().intValue()) {
                    value = this.getMaximum();
                } else if (value.intValue() < this.getMinimum().intValue()) {
                    value = this.getMinimum();
                }
            }
        }
        this.value = (T)value;
        HTRewrite.EVENT_BUS.post(new ClientSettingChangeEvent(this));
    }

    public T getValue() {
        return value;
    }

    public T getMinimum() {
        return minimum;
    }

    public T getMaximum() {
        return maximum;
    }

    private Predicate<T> visibility;
    public ValueSetting<T> setVisibility(Predicate<T> visibility) { this.visibility = visibility; return this; }
    public boolean isVisible() {
        if(visibility == null)
            return true;
        return visibility.test(this.getValue());
    }
}
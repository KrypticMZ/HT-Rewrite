package me.htrewrite.exeterimports.mcapi.interfaces;

public interface Toggleable {
    boolean isEnabled();

    void setEnabled(boolean flag);

    void toggle();
}
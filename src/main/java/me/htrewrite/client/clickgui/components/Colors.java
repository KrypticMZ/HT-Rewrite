package me.htrewrite.client.clickgui.components;

public enum Colors {
    BUTTON(0x66565656),
    BUTTON_ENABLED(0xBAb21e0d),
    BUTTON_HOVER(0xAAc4c4c4),
    BUTTON_ENABLED_HOVER(0xAAeaab19),
    BUTTON_COMPONENT(0x2E565656),
    BUTTON_COMPONENT_ENABLED(0x2Eb21e0d),
    BUTTON_LABEL(0xFFDDDDDD),
    BUTTON_LABEL_ENABLED(0xFFDDDDDD),
    BUTTON_LABEL_HOVER(0xFFCCCCCC),
    PANEL_INSIDE(0x88000000),
    PANEL_BORDER(0x55000000),
    PANEL_LABEL(0xFFFFFFFF);

    private int color;

    Colors(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int newColor) { color = newColor; }
}
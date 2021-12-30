package me.htrewrite.client.clickgui;

import java.awt.*;

public class StaticClickGuiColor {
    // public static int newColor(int r, int g, int b, int a) { return (r << 24 | (g << 16) | (b << 8) | a); }
    public static int newColor(int r, int g, int b, int a) { return new Color(r, g, b, a).getRGB(); }
}
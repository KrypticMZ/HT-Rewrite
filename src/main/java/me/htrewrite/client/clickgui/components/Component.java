package me.htrewrite.client.clickgui.components;

import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.exeterimports.mcapi.interfaces.Labeled;
import me.htrewrite.exeterimports.mcapi.utilities.minecraft.render.CustomFont;
import me.htrewrite.exeterimports.mcapi.utilities.minecraft.render.RenderHelper;

public abstract class Component extends RenderHelper implements Labeled {
    private final String label;
    private int positionX, positionY, width, height;
    protected final CustomFont font = new CustomFont("Tahoma");

    public Component(String label, int positionX, int positionY, int width, int height) {
        this.label = label;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}
    public void mouseRelease() {}

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public abstract void onClicked(int mouseX, int mouseY, int mouseButton);

    public abstract void drawComponent(int mouseX, int mouseY);

    protected boolean isHovering(int mouseX, int mouseY, int height) {
        return ((mouseX >= getPositionX())
                && (mouseX <= (getPositionX() + getWidth()))
                && ((mouseY >= StaticScrollOffset.offset+getPositionY())
                && (mouseY <= (StaticScrollOffset.offset+getPositionY() + height))));
    }
}
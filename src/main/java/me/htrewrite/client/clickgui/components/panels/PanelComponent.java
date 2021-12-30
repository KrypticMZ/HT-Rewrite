package me.htrewrite.client.clickgui.components.panels;

import me.htrewrite.client.clickgui.StaticGuiConfig;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.client.clickgui.components.buttons.settings.ModeComponent;
import me.htrewrite.client.clickgui.components.buttons.settings.StringComponent;
import me.htrewrite.client.clickgui.components.buttons.settings.ToggleableComponent;
import me.htrewrite.client.clickgui.components.buttons.settings.ValueComponent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class PanelComponent extends Component {
    private final List<Component> components = new CopyOnWriteArrayList<>();
    private int draggingPositionX, draggingPositionY;
    private boolean dragging = false, pinned = false, open = true;

    protected PanelComponent(String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        registerComponents();
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY, StaticGuiConfig.CATEGORY_PANEL_HEIGHT)) {
            switch (mouseButton) {
                case 0:
                    setDragging(true);
                    setDraggingPositionX(getPositionX() - mouseX);
                    setDraggingPositionY(getPositionY() - mouseY);
                    break;
                case 1:
                    this.open = !open;
                    break;
            }
        }

        if(open) 
            components.forEach(component -> component.onClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if (isDragging()) {
            setPositionX(getDraggingPositionX() + mouseX);
            setPositionY(getDraggingPositionY() + mouseY);
        }
        drawBorderedRectReliant(getPositionX(), StaticScrollOffset.offset + getPositionY(), getPositionX() + getWidth(),
                StaticScrollOffset.offset + getPositionY() + (open ? getHeight() : StaticGuiConfig.MOD_COMPONENT_HEIGHT), 1.7F,
                Colors.PANEL_INSIDE.getColor(), Colors.PANEL_BORDER.getColor());
        font.drawString(getLabel(), getPositionX() + 4, StaticScrollOffset.offset + getPositionY() + 1, Colors.PANEL_LABEL.getColor());
        if (open) {
            int componentPositionY = getPositionY() + StaticGuiConfig.MOD_COMPONENT_HEIGHT;
            for (Component component : components) {
                if((component instanceof ModeComponent) && !(((ModeComponent)component).modeSetting.isVisible()))
                    continue;
                if((component instanceof StringComponent) && !(((StringComponent)component).stringSetting.isVisible()))
                    continue;
                if((component instanceof ToggleableComponent) && !(((ToggleableComponent)component).toggleableSetting.isVisible()))
                    continue;
                if((component instanceof ValueComponent) && !(((ValueComponent)component).valueSetting.isVisible()))
                    continue;

                component.drawComponent(mouseX, mouseY);
                component.setPositionX(getPositionX());
                component.setPositionY(componentPositionY);
                componentPositionY += component.getHeight();
            }
        }
    }

    @Override
    public int getHeight() {
        int height = StaticGuiConfig.MOD_COMPONENT_HEIGHT;
        for (Component component : components) {
            height += component.getHeight();
        }
        return height;
    }

    public void mouseReleased() {
        setDragging(false);

        if(open)
            components.forEach(Component::mouseRelease);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if(open)
            components.forEach(component -> component.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
    }

    public List<Component> getComponents() {
        return components;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public int getDraggingPositionX() {
        return draggingPositionX;
    }

    public int getDraggingPositionY() {
        return draggingPositionY;
    }

    public void setDraggingPositionX(int draggingPositionX) {
        this.draggingPositionX = draggingPositionX;
    }

    public void setDraggingPositionY(int draggingPositionY) {
        this.draggingPositionY = draggingPositionY;
    }

    public abstract void registerComponents();
}
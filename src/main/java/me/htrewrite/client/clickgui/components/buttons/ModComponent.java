package me.htrewrite.client.clickgui.components.buttons;

import me.htrewrite.client.clickgui.StaticGuiConfig;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.client.clickgui.components.buttons.settings.*;
import me.htrewrite.client.module.Module;
import me.htrewrite.exeterimports.mcapi.settings.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModComponent extends Component {
    private final Module mod;
    private boolean open = false;
    private final List<Component> components = new CopyOnWriteArrayList<>();

    public ModComponent(Module mod, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.mod = mod;
        for (Setting setting : mod.getOptions()) {
            if (setting instanceof ModeSetting) {
                components.add(new ModeComponent((ModeSetting) setting, setting.getLabel(), positionX,
                        positionY, width, height));
            } else if (setting instanceof StringSetting) {
                components.add(new StringComponent((StringSetting) setting, setting.getLabel(), positionX,
                        positionY, width, height));
            } else if (setting instanceof ValueSetting) {
                components.add(new ValueComponent((ValueSetting) setting, setting.getLabel(), positionX,
                        positionY, width, height));
            } else if (setting instanceof ToggleableSetting) {
                components.add(new ToggleableComponent((ToggleableSetting) setting, setting.getLabel(), positionX,
                        positionY, width, height));
            } else if (setting instanceof BindSetting) {
                components.add(new BindComponent((BindSetting) setting, setting.getLabel(), positionX,
                        positionY, width, height));
            } else if(setting instanceof SeparatorSetting) {
                components.add(new SeparatorComponent((SeparatorSetting)setting, setting.getLabel(), positionX,
                        positionY, width, height));
            }
        }
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY, StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT)) {
            switch (mouseButton) {
                case 0:
                    mod.toggle();
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
    public void mouseRelease() {
        super.mouseRelease();

        components.forEach(Component::mouseRelease);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        drawBorderedRectReliant(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + 19, 1.7F, mod.isEnabled() ?
                        Colors.BUTTON_ENABLED.getColor() : Colors.BUTTON.getColor(),
                Colors.PANEL_BORDER.getColor());
        font.drawString(getLabel(), getPositionX() + 4, StaticScrollOffset.offset + getPositionY() + 1, mod.isEnabled() ?
                Colors.BUTTON_LABEL_ENABLED.getColor() : Colors.BUTTON_LABEL.getColor());
        if (components.size() > 0) {
            font.drawString("...", getPositionX() + getWidth() - 13, StaticScrollOffset.offset + getPositionY() - 1, Colors.BUTTON_LABEL.getColor());
        }
        ArrayList<Component> drawComponents = new ArrayList<>();
        for(Component comp : components) {
            if((comp instanceof ModeComponent) && !(((ModeComponent)comp).modeSetting.isVisible()))
                continue;
            if((comp instanceof StringComponent) && !(((StringComponent)comp).stringSetting.isVisible()))
                continue;
            if((comp instanceof ToggleableComponent) && !(((ToggleableComponent)comp).toggleableSetting.isVisible()))
                continue;
            if((comp instanceof ValueComponent) && !(((ValueComponent)comp).valueSetting.isVisible()))
                continue;

            drawComponents.add(comp);
        }
        setHeight(!open ? StaticGuiConfig.MOD_COMPONENT_HEIGHT : StaticGuiConfig.MOD_COMPONENT_HEIGHT + drawComponents.size() * StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT);
        if (open) {
            int positionY = getPositionY() + StaticGuiConfig.MOD_COMPONENT_HEIGHT;
            for (Component component : drawComponents) {
                component.drawComponent(mouseX, mouseY);
                component.setPositionX(getPositionX());
                component.setPositionY(positionY);
                positionY += StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT;
            }
        }
    }

    public List<Component> getComponents() {
        return components;
    }

    public Module getMod() {
        return mod;
    }
}
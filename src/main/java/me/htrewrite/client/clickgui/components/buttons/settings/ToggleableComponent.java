package me.htrewrite.client.clickgui.components.buttons.settings;

import me.htrewrite.client.clickgui.StaticGuiConfig;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;

public class ToggleableComponent extends Component {
    public final ToggleableSetting toggleableSetting;

    public ToggleableComponent(ToggleableSetting toggleableSetting, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.toggleableSetting = toggleableSetting;
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY, StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT) && mouseButton == 0 && toggleableSetting.isVisible())  {
            toggleableSetting.toggle();
        }
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        drawRect(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1, toggleableSetting.isEnabled() ?
                        Colors.BUTTON_COMPONENT_ENABLED.getColor() : Colors.BUTTON_COMPONENT.getColor());
        font.drawString(getLabel(), getPositionX() + 4, StaticScrollOffset.offset + getPositionY() + 1, toggleableSetting.isEnabled() ?
                Colors.BUTTON_LABEL_ENABLED.getColor() : Colors.BUTTON_LABEL.getColor());
    }
}
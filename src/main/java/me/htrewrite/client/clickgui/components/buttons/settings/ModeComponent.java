package me.htrewrite.client.clickgui.components.buttons.settings;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.clickgui.StaticGuiConfig;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;

public class ModeComponent extends Component {
    public final ModeSetting modeSetting;

    public ModeComponent(ModeSetting modeSetting, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.modeSetting = modeSetting;
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {

        if (isHovering(mouseX, mouseY, StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT) && modeSetting.isVisible()) {
            switch (mouseButton) {
                case 0:
                    modeSetting.increment();
                    break;
                case 1:
                    modeSetting.decrement();
                    break;
            }
        }

    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        drawRect(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1, Colors.BUTTON_COMPONENT.getColor());
        font.drawString(String.format("%s (%s)", getLabel(), modeSetting.getValue()), getPositionX() + 4,
                StaticScrollOffset.offset + getPositionY() + 1, Colors.BUTTON_LABEL_ENABLED.getColor());
    }
}
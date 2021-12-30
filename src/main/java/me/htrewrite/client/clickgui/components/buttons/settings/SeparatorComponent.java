package me.htrewrite.client.clickgui.components.buttons.settings;

import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.exeterimports.mcapi.settings.SeparatorSetting;

public class SeparatorComponent extends Component {
    SeparatorSetting separatorSetting;
    public SeparatorComponent(SeparatorSetting setting, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.separatorSetting = setting;
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {}

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        drawRect(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1, 0x3E565656);
        font.drawString(String.format("%s", getLabel()), getPositionX() + 4,
                StaticScrollOffset.offset + getPositionY() + 1, Colors.BUTTON_LABEL_ENABLED.getColor());
    }
}

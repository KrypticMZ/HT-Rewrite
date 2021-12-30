package me.htrewrite.client.clickgui.components.buttons.settings;

import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;

public class StringComponent extends Component {
    public final StringSetting stringSetting;

    public StringComponent(StringSetting stringSetting, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.stringSetting = stringSetting;
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {
        //TODO make a way for this to be typed into, using keyTyped
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        drawRect(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1, Colors.BUTTON_COMPONENT.getColor());
        font.drawString(String.format("%s (\"%s\")", getLabel(), stringSetting.getValue()), getPositionX() + 4,
                StaticScrollOffset.offset + getPositionY() + 1, Colors.BUTTON_LABEL_ENABLED.getColor());
    }
}
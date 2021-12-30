package me.htrewrite.client.clickgui.components.buttons.settings;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.clickgui.StaticGuiConfig;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;
import me.htrewrite.exeterimports.mcapi.settings.BindSetting;

public class BindComponent extends Component {
    public final BindSetting bindSetting;
    public BindComponent(BindSetting bindSetting, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.bindSetting = bindSetting;
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHovering(mouseX, mouseY, StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT) && mouseButton == 0 && Wrapper.binding == null)
            Wrapper.binding = bindSetting;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        drawRect(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1, Colors.BUTTON_COMPONENT.getColor());

        String stringToDraw = String.format("%s %s", "Bind:", bindSetting.getFormattedBind());
        if(Wrapper.binding != null && Wrapper.binding.module.getName().equalsIgnoreCase(bindSetting.module.getName()))
            stringToDraw = "Binding...";
        font.drawString(stringToDraw, getPositionX() + 4, StaticScrollOffset.offset + getPositionY() + 1, Colors.BUTTON_LABEL_ENABLED.getColor());
    }
}
package me.htrewrite.client.clickgui.components.buttons.settings;

import me.htrewrite.client.clickgui.StaticGuiConfig;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.Component;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ValueComponent extends Component {
    public final ValueSetting valueSetting;

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public ValueComponent(ValueSetting valueSetting, String label, int positionX, int positionY, int width, int height) {
        super(label, positionX, positionY, width, height);
        this.valueSetting = valueSetting;
    }

    private void handleClick(int mouseX, int mouseY) {
        if(!isHovering(mouseX, mouseY, StaticGuiConfig.MOD_CONFIG_COMPONENT_HEIGHT))
            return;
        if(!valueSetting.isVisible())
            return;

        double x = mouseX-getPositionX();
        double num = x/getWidth();
        double max = valueSetting.getMaximum().doubleValue() + (-valueSetting.getMinimum().doubleValue());
        double value = max*num + valueSetting.getMinimum().doubleValue();

        valueSetting.setValue(round(value, 2));
    }

    private boolean isDragging = false;

    @Override
    public void onClicked(int mouseX, int mouseY, int mouseButton) {
        handleClick(mouseX, mouseY);

        if(mouseButton == 0)
            isDragging = true;
    }

    @Override
    public void mouseRelease() {
        super.mouseRelease();

        isDragging = false;
    }

    /*
    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        handleClick(mouseX, mouseY);
    }*/

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if(isDragging)
            handleClick(mouseX, mouseY);



        drawRect(getPositionX() + 1, StaticScrollOffset.offset + getPositionY() + 1, getPositionX() + getWidth() - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1, Colors.BUTTON_COMPONENT.getColor());
        drawRect(
                getPositionX() + 1,
                StaticScrollOffset.offset + getPositionY() + 1,

                getPositionX() + (float)MathUtil.transform(valueSetting.getMaximum().doubleValue() - valueSetting.getMinimum().floatValue(), getWidth(), valueSetting.getValue().doubleValue() - valueSetting.getMinimum().floatValue()) - 1,
                StaticScrollOffset.offset + getPositionY() + getHeight() - 1,

                Colors.BUTTON_COMPONENT_ENABLED.getColor());
        font.drawString(String.format("%s (%s)", getLabel(), valueSetting.getValue()), getPositionX() + 4, StaticScrollOffset.offset + getPositionY() + 1,
                Colors.BUTTON_LABEL_ENABLED.getColor());
    }
}
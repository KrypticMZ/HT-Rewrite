package me.htrewrite.client.clickgui;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.clickgui.components.buttons.ModComponent;
import me.htrewrite.client.clickgui.components.panels.PanelComponent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ClickGuiScreen extends GuiScreen {
    private final List<PanelComponent> panels = new CopyOnWriteArrayList<>();

    public ClickGuiScreen() {
        int positionX = 30;
        for (ModuleType modType : ModuleType.values()) {
            panels.add(new PanelComponent(modType.name(), positionX, 20, 85, StaticGuiConfig.CATEGORY_PANEL_HEIGHT) {
                @Override
                public void registerComponents() {
                    for (Module mod : HTRewrite.INSTANCE.getModuleManager().getModules()) {
                        if (mod.getCategory().equals(modType)) {
                            getComponents().add(new ModComponent(mod, mod.getLabel(), getPositionX(), getPositionY(), 85, StaticGuiConfig.MOD_COMPONENT_HEIGHT));
                        }
                    }
                }
            });
            positionX += 87;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        panels.forEach(panelComponent -> panelComponent.drawComponent(mouseX, mouseY));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(Wrapper.binding != null) {
            if(keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE) { /* RESET KEY */
                Wrapper.binding.bind(0);
                Wrapper.sendClientText("&cUnbinded module &l" + Wrapper.binding.module.getName() + "&c!");
                Wrapper.binding = null;

                return;
            }
            Wrapper.binding.bind(keyCode);
            Wrapper.sendClientText("&aBinded module &l" + Wrapper.binding.module.getName() + " &ato '" + Keyboard.getKeyName(keyCode) + "' key!");
            Wrapper.binding = null;
            return;
        }

        if(keyCode == Keyboard.KEY_UP)
            StaticScrollOffset.offset-=10;
        if(keyCode == Keyboard.KEY_DOWN)
            StaticScrollOffset.offset+=10;

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        panels.forEach(panelComponent -> panelComponent.onClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        panels.forEach(PanelComponent::mouseReleased);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        panels.forEach(panelComponent -> panelComponent.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
    }

    @Override
    public void onGuiClosed() {
        //client.fileManager.getConfig("gui_config.json").save();
        //client.fileManager.getConfig("overlay_config.json").save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }



    public List<PanelComponent> getPanels() {
        return panels;
    }
}
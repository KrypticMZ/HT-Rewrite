package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderTooltipEvent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {
    @Shadow
    protected List<GuiButton> buttonList;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected FontRenderer fontRenderer;

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo info) {
        RenderTooltipEvent renderTooltipEvent = new RenderTooltipEvent(stack, x, y);
        HTRewrite.EVENT_BUS.post(renderTooltipEvent);
        if(renderTooltipEvent.isCancelled())
            info.cancel();
    }
}
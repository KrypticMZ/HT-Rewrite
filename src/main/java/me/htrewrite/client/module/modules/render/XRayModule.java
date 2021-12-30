package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderBlockEvent;
import me.htrewrite.client.event.custom.world.GetAmbientOcclusionLightValueEvent;
import me.htrewrite.client.event.custom.world.SetOpaqueCubeEvent;
import me.htrewrite.client.event.custom.world.ShouldSideBeRenderedEvent;
import me.htrewrite.client.manager.XRayManager;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.event.type.Cancellable;
import me.zero.alpine.fork.listener.Listener;

public class XRayModule extends Module {
    private float lastGamma;
    private XRayManager xRayManager;

    public XRayModule() {
        super("XRay", "XRay", ModuleType.Render, 0);
        endOption();

        this.xRayManager = HTRewrite.INSTANCE.getxRayManager();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.renderGlobal.loadRenderers();
        lastGamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 10000f;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.renderGlobal.loadRenderers();
        mc.gameSettings.gammaSetting = lastGamma;
    }

    private Listener<RenderBlockEvent> renderBlockEventListener = new Listener<>(event -> {
        if(!xRayManager.isBlock(event.state.getBlock()))
            event.cancel();
    });

    private Listener<SetOpaqueCubeEvent> opaqueCubeEventListener = new Listener<>(Cancellable::cancel);
    private Listener<ShouldSideBeRenderedEvent> shouldSideBeRenderedEventListener = new Listener<>(event -> event.callbackInfoReturnable.setReturnValue(xRayManager.isBlock(mc.world.getBlockState(event.pos).getBlock())));
    private Listener<GetAmbientOcclusionLightValueEvent> getAmbientOcclusionLightValueEventListener = new Listener<>(event -> event.callbackInfoReturnable.setReturnValue(1f));
}
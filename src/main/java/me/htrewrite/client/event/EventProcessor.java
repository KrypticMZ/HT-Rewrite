package me.htrewrite.client.event;

import static me.htrewrite.client.HTRewrite.EVENT_BUS;
import static me.htrewrite.client.command.CommandReturnStatus.*;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.audio.AudioEnum;
import me.htrewrite.client.clickgui.ClickGuiScreen;
import me.htrewrite.client.clickgui.StaticScrollOffset;
import me.htrewrite.client.command.CommandManager;
import me.htrewrite.client.command.CommandReturnStatus;
import me.htrewrite.client.customgui.CustomMainMenuGui;
import me.htrewrite.client.event.custom.player.PlayerDisconnectEvent;
import me.htrewrite.client.event.custom.render.RenderEvent;
import me.htrewrite.client.module.Module;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.concurrent.atomic.AtomicBoolean;

public class EventProcessor {
    private AtomicBoolean tickOngoing = new AtomicBoolean(false);
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTickHighest(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            tickOngoing.set(true);
    }
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onTickLowest(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            tickOngoing.set(false);
    }
    public boolean ticksOngoing() { return tickOngoing.get(); }

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent inputEvent) {
        boolean eventKeyState = Keyboard.getEventKeyState();
        int eventKey = Keyboard.getEventKey();
        if(!eventKeyState) return;

        if (eventKey != Keyboard.KEY_NONE && Keyboard.getEventKeyState()) {
            for (Module module : HTRewrite.INSTANCE.getModuleManager().getModules())
                if (module.getKey() == eventKey)
                    module.toggle();
        }
        EVENT_BUS.post(inputEvent);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        EVENT_BUS.post(event);

        CommandReturnStatus returnStatus = HTRewrite.INSTANCE.getCommandManager().gotMessage(event.getMessage());
        if(returnStatus != COMMAND_INVALID_SYNTAX) event.setCanceled(true);
        if(returnStatus == COMMAND_INVALID) Wrapper.getPlayer().sendMessage(new TextComponentString("\u00a7cInvalid command, do " + CommandManager.prefix + "help"));
        if(returnStatus == COMMAND_ERROR) Wrapper.getPlayer().sendMessage(new TextComponentString("\u00a7cThere was an error executing the command."));
    }

    @SubscribeEvent public void onQuitClientServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { EVENT_BUS.post(event); EVENT_BUS.post(new PlayerDisconnectEvent()); }
    @SubscribeEvent public void onQuitServerClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) { EVENT_BUS.post(event); EVENT_BUS.post(new PlayerDisconnectEvent()); }

    @SubscribeEvent public void onClientTickEvent(TickEvent.ClientTickEvent event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (event.isCanceled())
            return;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();

        GlStateManager.glLineWidth(1f);
        HTRewrite.EVENT_BUS.post(new RenderEvent(event.getPartialTicks()));
        GlStateManager.glLineWidth(1f);

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }
    @SubscribeEvent public void onPlace(BlockEvent.PlaceEvent event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void onPlayerTick(TickEvent.PlayerTickEvent event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void onInteractWithItem(PlayerInteractEvent.RightClickItem event) { EVENT_BUS.post(event); }


    @SubscribeEvent public void startInteract(LivingEntityUseItemEvent.Start event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void stopInteract(LivingEntityUseItemEvent.Stop event) { EVENT_BUS.post(event); }
    @SubscribeEvent
    public void getFOVModifier(EntityViewRenderEvent.FOVModifier event) {
        EVENT_BUS.post(event);
        if(event.isCanceled())
            event.setFOV(event.getFOV());
    }

    @SubscribeEvent public void inputUpdateEvent(InputUpdateEvent event) { EVENT_BUS.post(event); }

    CustomMainMenuGui customMainMenuGui = new CustomMainMenuGui();

    @SubscribeEvent
    public void onDimensionChange(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiMainMenu)
            event.setGui(customMainMenuGui);
    }

    long ms = System.currentTimeMillis();
    @SubscribeEvent
    public void onTick(TickEvent event) {
        long currentMS = System.currentTimeMillis();
        if(currentMS > ms) {
            ms = currentMS+50;
            if(!(Wrapper.getMC().currentScreen instanceof ClickGuiScreen))
                return;

            int dWheel = Mouse.getDWheel();
            StaticScrollOffset.offset+=(dWheel<0?-20:dWheel>0?20:0);
        }
    }

    @SubscribeEvent
    public void playerRenderEvent(RenderPlayerEvent.Pre event) {
        EVENT_BUS.post(event);
    }


    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        AudioEnum.Music.MAIN.stop();
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        AudioEnum.Music.MAIN.stop();
    }

    @SubscribeEvent public void fogColors(EntityViewRenderEvent.FogColors event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void fogDensity(EntityViewRenderEvent.FogDensity event) { EVENT_BUS.post(event); }

    @SubscribeEvent public void chatReceive(ClientChatReceivedEvent event) { EVENT_BUS.post(event); }
    @SubscribeEvent public void renderBlockOverlayEvent(RenderBlockOverlayEvent event) { EVENT_BUS.post(event); }

    @SubscribeEvent public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) { EVENT_BUS.post(event); }

    @SubscribeEvent public void onEntityPlaceBlock(BlockEvent.EntityPlaceEvent event) { EVENT_BUS.post(event); }

    @SubscribeEvent public void attackEntity(AttackEntityEvent event) {EVENT_BUS.post(event);}

    @SubscribeEvent public void onRenderBlockOverlay(RenderBlockOverlayEvent event) { EVENT_BUS.post(event); }
}
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

import java.util.Random;

public class DiscordPresenceSSL {
    /* GLOBALS */
    private static final String appId = "845029478790332426";

    private static final String[] largeImage = {"logo1", "logo2", "logo3"};
    private static final String largeImageText = "DumbClient v1.0";

    private static final String[] smallImage = {};
    private static final String smallImageText = "";

    /* ******* */
    public static final DiscordRichPresence richPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;
    private static final Random random = new Random();
    private static Thread thread;

    private static String state = "";
    public static void setState(String nState) { state = nState; }
    public static String getState() { return state; }

    public static void setPresence() {
        richPresence.details = (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu)?"On the Main Menu!":"Playing " + (Minecraft.getMinecraft().getCurrentServerData() == null?"on a singleplayer world!":"on a multiplayer server!");
        richPresence.state = getState();
        richPresence.largeImageKey = largeImage.length==0?"":largeImage.length>1?largeImage[random.nextInt(largeImage.length)]:largeImage[0];
        richPresence.largeImageText = largeImageText;
        richPresence.smallImageKey = smallImage.length==0?"":smallImage.length>1?smallImage[random.nextInt(smallImage.length)]:smallImage[0];
        richPresence.smallImageText = smallImage.length!=0?smallImageText:"";
    }

    public static void boot() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        discordRPC.Discord_Initialize(appId, eventHandlers, true, "");
        richPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        setPresence();
        discordRPC.Discord_UpdatePresence(richPresence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                discordRPC.Discord_RunCallbacks();
                setPresence();
                discordRPC.Discord_UpdatePresence(richPresence);
                try { Thread.sleep(2000); } catch (Exception exception) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void terminate() {
        if(thread != null && !thread.isInterrupted())
            thread.interrupt();

        discordRPC.Discord_Shutdown();
    }
}
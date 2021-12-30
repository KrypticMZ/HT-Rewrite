package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.world.EntityAddedEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StashLoggerModule extends Module {
    public static final ModeSetting setting = new ModeSetting("Settings", 0, BetterMode.construct("LOGGER", "FILE"));
    /* Logger */
    public static final ToggleableSetting chests = new ToggleableSetting("Chests", true);
    public static final ValueSetting<Double> chestAmount = new ValueSetting<>("ChestAmount", 5d, 0d, 50d);
    public static final ToggleableSetting shulkers = new ToggleableSetting("Shulkers", true);
    public static final ToggleableSetting chestedAnimals = new ToggleableSetting("ChestedAnimals", true);
    /* File */
    public static final ToggleableSetting logToFile = new ToggleableSetting("LogToFile", false);

    private final Executor executor;

    public StashLoggerModule() {
        super("StashLogger", "Log donkeys, chests and shulkers around you.", ModuleType.World, 0);
        addOption(setting);
        addOption(chests.setVisibility(v -> setting.getValue().contentEquals("LOGGER")));
        addOption(chestAmount.setVisibility(v -> setting.getValue().contentEquals("LOGGER") && chests.isEnabled()));
        addOption(shulkers.setVisibility(v -> setting.getValue().contentEquals("LOGGER")));
        addOption(chestedAnimals.setVisibility(v -> setting.getValue().contentEquals("LOGGER")));
        addOption(logToFile.setVisibility(v -> setting.getValue().contentEquals("FILE")));
        endOption();

        this.executor = Executors.newFixedThreadPool(1);
    }

    private String[] generateFileName() {
        String server = mc.getCurrentServerData() == null ? "singleplayer" : mc.getCurrentServerData().serverIP;
        String folder = "htRewrite\\stashlogger\\";
        String file = folder + server + "_" + DateTimeFormatter.ofPattern("dd_MM_yyyy").format(LocalDateTime.now()) + ".txt";

        return new String[] {folder, file};
    }
    private String getDimension() { return mc.player.dimension==-1?"NETHER":mc.player.dimension==0?"OVERWORLD":"END"; }

    @Override
    public void sendMessage(String message) {
        this.executor.execute(() -> {
            if(logToFile.isEnabled()) {
                String[] fileName = generateFileName();

                try {
                    new File(fileName[0]).mkdirs();

                    FileWriter fileWriter = new FileWriter(fileName[1], true);
                    fileWriter.write(message + "\n");
                    fileWriter.close();
                } catch (IOException exception) { exception.printStackTrace(); }
            }
        });

        super.sendMessage(message);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if(logToFile.isEnabled())
            super.sendMessage(String.format("File will be stored here: '%s'", generateFileName()[1]));
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;

        if(event.getPacket() instanceof SPacketChunkData) {
            final SPacketChunkData packet = (SPacketChunkData)event.getPacket();
            int chestCount, shulkerCount = chestCount = 0;

            for(NBTTagCompound tag : packet.getTileEntityTags()) {
                String id = tag.getString("id");
                if((id.contentEquals("minecraft:chest") || id.contentEquals("minecraft:trapped_chest")) && chests.isEnabled())
                    ++chestCount;
                else if(id.contentEquals("minecraft:shulker_box") && shulkers.isEnabled())
                    ++shulkerCount;
            }

            if(chestCount >= chestAmount.getValue().intValue())
                sendMessage(String.format("Found %d chests at chunk %s[%d, %d]", chestCount, getDimension(), packet.getChunkX()*16, packet.getChunkZ()*16));
            if(shulkerCount > 0)
                sendMessage(String.format("Found %d shulkers at chunk %s[%d, %d]", shulkerCount, getDimension(), packet.getChunkX()*16, packet.getChunkZ()*16));
        }
    });

    @EventHandler
    private Listener<EntityAddedEvent> entityAddedEventListener = new Listener<>(event -> {
        if(!chestedAnimals.isEnabled())
            return;
        if(event.getEntity() instanceof AbstractChestHorse) {
            AbstractChestHorse entity = (AbstractChestHorse)event.getEntity();
            if(entity.hasChest())
                sendMessage(String.format("Found chested animal with name '%s' at coords %s[%d, %d]", entity.getName(), getDimension(), (int)Math.floor(entity.posX), (int)Math.floor(entity.posZ)));
        } else if(event.getEntity() instanceof EntityLlama) {
            EntityLlama entity = (EntityLlama)event.getEntity();
            if(entity.hasChest())
                sendMessage(String.format("Found chested animal with name '%s' at coords %s[%d, %d]", entity.getName(), getDimension(), (int)Math.floor(entity.posX), (int)Math.floor(entity.posZ)));
        }
    });
}
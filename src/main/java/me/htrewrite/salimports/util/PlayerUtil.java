package me.htrewrite.salimports.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.util.ClientAuthenticator;
import me.htrewrite.client.util.Timer;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static Timer timer;
    private static JsonParser PARSER;
    static {
        timer = new Timer();
        PlayerUtil.PARSER = new JsonParser();
    }

    public static void PacketFacePitchAndYaw(float p_Pitch, float p_Yaw)
    {
        boolean l_IsSprinting = mc.player.isSprinting();

        if (l_IsSprinting != mc.player.serverSprintState)
        {
            if (l_IsSprinting)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
            else
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            mc.player.serverSprintState = l_IsSprinting;
        }

        boolean l_IsSneaking = mc.player.isSneaking();

        if (l_IsSneaking != mc.player.serverSneakState)
        {
            if (l_IsSneaking)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            else
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            mc.player.serverSneakState = l_IsSneaking;
        }

        if (PlayerUtil.isCurrentViewEntity())
        {
            float l_Pitch = p_Pitch;
            float l_Yaw = p_Yaw;

            AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
            double l_PosXDifference = mc.player.posX - mc.player.lastReportedPosX;
            double l_PosYDifference = axisalignedbb.minY - mc.player.lastReportedPosY;
            double l_PosZDifference = mc.player.posZ - mc.player.lastReportedPosZ;
            double l_YawDifference = (double)(l_Yaw - mc.player.lastReportedYaw);
            double l_RotationDifference = (double)(l_Pitch - mc.player.lastReportedPitch);
            ++mc.player.positionUpdateTicks;
            boolean l_MovedXYZ = l_PosXDifference * l_PosXDifference + l_PosYDifference * l_PosYDifference + l_PosZDifference * l_PosZDifference > 9.0E-4D || mc.player.positionUpdateTicks >= 20;
            boolean l_MovedRotation = l_YawDifference != 0.0D || l_RotationDifference != 0.0D;

            if (mc.player.isRiding())
            {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, l_Yaw, l_Pitch, mc.player.onGround));
                l_MovedXYZ = false;
            }
            else if (l_MovedXYZ && l_MovedRotation)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, axisalignedbb.minY, mc.player.posZ, l_Yaw, l_Pitch, mc.player.onGround));
            }
            else if (l_MovedXYZ)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, axisalignedbb.minY, mc.player.posZ, mc.player.onGround));
            }
            else if (l_MovedRotation)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(l_Yaw, l_Pitch, mc.player.onGround));
            }
            else if (mc.player.prevOnGround != mc.player.onGround)
            {
                mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            }

            if (l_MovedXYZ)
            {
                mc.player.lastReportedPosX = mc.player.posX;
                mc.player.lastReportedPosY = axisalignedbb.minY;
                mc.player.lastReportedPosZ = mc.player.posZ;
                mc.player.positionUpdateTicks = 0;
            }

            if (l_MovedRotation)
            {
                mc.player.lastReportedYaw = l_Yaw;
                mc.player.lastReportedPitch = l_Pitch;
            }

            mc.player.prevOnGround = mc.player.onGround;
            mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
        }
    }

    public static boolean isCurrentViewEntity()
    {
        return mc.getRenderViewEntity() == mc.player;
    }

    public static int getItemSlot(Item input)
    {
        if (Wrapper.getMC().player == null)
            return 0;

        for (int i = 0; i < Wrapper.getMC().player.inventoryContainer.getInventory().size(); ++i)
        {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack s = Wrapper.getMC().player.inventoryContainer.getInventory().get(i);

            if (s.isEmpty())
                continue;

            if (s.getItem() == input)
            {
                return i;
            }
        }
        return -1;
    }

    public static int getRecursiveItemSlot(Item input)
    {
        if (Wrapper.getMC().player == null)
            return 0;

        for (int i = Wrapper.getMC().player.inventoryContainer.getInventory().size() - 1; i > 0; --i)
        {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack s = Wrapper.getMC().player.inventoryContainer.getInventory().get(i);

            if (s.isEmpty())
                continue;

            if (s.getItem() == input)
            {
                return i;
            }
        }
        return -1;
    }

    public static BlockPos GetLocalPlayerPosFloored() { return new BlockPos(Math.floor(Wrapper.getMC().player.posX), Math.floor(Wrapper.getMC().player.posY), Math.floor(Wrapper.getMC().player.posZ)); }
    public static EnumFacing GetFacing()
    {
        switch (MathHelper.floor((double) (Wrapper.getMC().player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7)
        {
            case 0:
            case 1:
                return EnumFacing.SOUTH;
            case 2:
            case 3:
                return EnumFacing.WEST;
            case 4:
            case 5:
                return EnumFacing.NORTH;
            case 6:
            case 7:
                return EnumFacing.EAST;
        }
        return EnumFacing.NORTH;
    }

    public static String getNameFromUUID(final UUID uuid) {
        try {
            final lookUpName process = new lookUpName(uuid);
            final Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getNameFromUUID(final String uuid) {
        try {
            final lookUpName process = new lookUpName(uuid);
            final Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static UUID getUUIDFromName(final String name) {
        try {
            final lookUpUUID process = new lookUpUUID(name);
            final Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String requestIDs(final String data) {
        try {
            final String query = "https://api.mojang.com/profiles/minecraft";
            final URL url = new URL(query);
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            final OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            final InputStream in = new BufferedInputStream(conn.getInputStream());
            final String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String convertStreamToString(final InputStream is) {
        @SuppressWarnings("resource")
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

    public static List<String> getHistoryOfNames(final UUID id) {
        try {
            final JsonArray array = getResources(new URL("https://api.mojang.com/user/profiles/" + getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            final List<String> temp = Lists.newArrayList();
            for (final JsonElement e : array) {
                final JsonObject node = e.getAsJsonObject();
                final String name = node.get("name").getAsString();
                final long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                temp.add(name + "§8" + new Date(changedAt).toString());
            }
            Collections.sort(temp);
            return temp;
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static String getIdNoHyphens(final UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(final URL url, final String request) throws Exception {
        return getResources(url, request, null);
    }

    private static JsonElement getResources(final URL url, final String request, final JsonElement element) throws Exception {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.writeBytes(AdvancementManager.GSON.toJson(element));
                output.close();
            }
            final Scanner scanner = new Scanner(connection.getInputStream());
            final StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }
            scanner.close();
            final String json = builder.toString();
            final JsonElement data = PlayerUtil.PARSER.parse(json);
            return data;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static class lookUpUUID implements Runnable
    {
        private volatile UUID uuid;
        private final String name;

        public lookUpUUID(final String name) {
            this.name = name;
        }

        @Override
        public void run() {
            NetworkPlayerInfo profile;
            try {
                final ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<NetworkPlayerInfo>(Objects.requireNonNull(Wrapper.getMC().getConnection()).getPlayerInfoMap());
                profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
                assert profile != null;
                this.uuid = profile.getGameProfile().getId();
            }
            catch (Exception e2) {
                profile = null;
            }
            if (profile == null) {
                final String s = PlayerUtil.requestIDs("[\"" + this.name + "\"]");
                if (s == null || s.isEmpty()) {
                    //Command.sendMessage("Couldn't find player ID. Are you connected to the internet? (0)");
                }
                else {
                    final JsonElement element = new JsonParser().parse(s);
                    if (element.getAsJsonArray().size() == 0) {
                        //Command.sendMessage("Couldn't find player ID. (1)");
                    }
                    else {
                        try {
                            final String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            this.uuid = UUIDTypeAdapter.fromString(id);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            //Command.sendMessage("Couldn't find player ID. (2)");
                        }
                    }
                }
            }
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class lookUpName implements Runnable
    {
        private volatile String name;
        private final String uuid;
        private final UUID uuidID;

        public lookUpName(final String input) {
            this.uuid = input;
            this.uuidID = UUID.fromString(input);
        }

        public lookUpName(final UUID input) {
            this.uuidID = input;
            this.uuid = input.toString();
        }

        @Override
        public void run() {
            this.name = this.lookUpName();
        }

        public String lookUpName() {
            EntityPlayer player = null;
            if (Wrapper.getMC().world != null) {
                player = Wrapper.getMC().world.getPlayerEntityByUUID(this.uuidID);
            }
            if (player == null) {
                final String url = "https://api.mojang.com/user/profiles/" + this.uuid.replace("-", "") + "/names";
                try {
                    @SuppressWarnings("deprecation")
                    final String nameJson = IOUtils.toString(new URL(url));
                    final JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
                    final String playerSlot = nameValue.get(nameValue.size() - 1).toString();
                    final JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
                    return nameObject.get("name").toString();
                }
                catch (IOException | ParseException ex2) {
                    ex2.printStackTrace();
                    return null;
                }
            }
            return player.getName();
        }

        public String getName() {
            return this.name;
        }
    }
}
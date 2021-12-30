package me.htrewrite.client.module.modules.render;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderPlayerNameEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.client.util.font.CFonts;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.salimports.util.EntityUtil;
import me.htrewrite.salimports.util.GLUProjection;
import me.zero.alpine.fork.event.type.Cancellable;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NametagsModule extends Module {
    public static final ToggleableSetting armor = new ToggleableSetting("Armor", true);
    public static final ToggleableSetting ping = new ToggleableSetting("Ping", true);

    private ICamera camera = new Frustum();
    public NametagsModule() {
        super("Nametags", "Display custom nametags. (SalHack)", ModuleType.Render, 0);
        addOption(armor);
        addOption(ping);
        endOption();
    }

    private float[] convertBounds(Entity entity, float partialTicks, int width, int height) {
        if(mc.getRenderViewEntity() == null)
            return null;

        float x, y = x = -1;
        float w = width + 1;
        float h = height + 1;

        final Vec3d pos = MathUtil.interpolateEntity(entity, partialTicks);
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        if(entity instanceof EntityEnderCrystal)
            bb = new AxisAlignedBB(bb.minX + .3f, bb.minY + .2f, bb.minZ + .3f, bb.maxX - .3f, bb.maxY, bb.maxZ - .3f);
        if(entity instanceof EntityItem)
            bb = new AxisAlignedBB(bb.minX, bb.minY + .7f, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        bb = bb.expand(.15f, .1f, .15f);
        camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if(!camera.isBoundingBoxInFrustum(bb))
            return null;

        final Vec3d corners[] = {
                new Vec3d(bb.minX - bb.maxX + entity.width / 2, 0, bb.minZ - bb.maxZ + entity.width / 2),
                new Vec3d(bb.maxX - bb.minX - entity.width / 2, 0, bb.minZ - bb.maxZ + entity.width / 2),
                new Vec3d(bb.minX - bb.maxX + entity.width / 2, 0, bb.maxZ - bb.minZ - entity.width / 2),
                new Vec3d(bb.maxX - bb.minX - entity.width / 2, 0, bb.maxZ - bb.minZ - entity.width / 2),

                new Vec3d(bb.minX - bb.maxX + entity.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + entity.width / 2),
                new Vec3d(bb.maxX - bb.minX - entity.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + entity.width / 2),
                new Vec3d(bb.minX - bb.maxX + entity.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - entity.width / 2),
                new Vec3d(bb.maxX - bb.minX - entity.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - entity.width / 2)};
        for(Vec3d vec3d : corners) {
            final GLUProjection.Projection projection = GLUProjection.getInstance().project(pos.x + vec3d.x - mc.getRenderManager().viewerPosX, pos.y + vec3d.y - mc.getRenderManager().viewerPosY, pos.z + vec3d.z - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, false);
            x = Math.max(x, (float) projection.getX());
            y = Math.max(y, (float) projection.getY());
            w = Math.min(w, (float) projection.getX());
            h = Math.min(h, (float) projection.getY());
        }

        if(x != -1 && y != -1 && w != width + 1 && h != height +1)
            return new float[]{x, y, w, h};
        return null;
    }

    private void renderNametag(EntityPlayer player, RenderGameOverlayEvent event) {
        final float[] bounds = convertBounds(player, event.getPartialTicks(), event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
        if(bounds != null) {
            String name = StringUtils.stripControlCodes(player.getName());
            int color = -1;

            if(HTRewrite.INSTANCE.getFriendManager().isFriend(player.getName())) {
                name = player.getName();
                color = 0x00C3EE;
            } int responseTime = -1;
            if(ping.isEnabled())
                try { responseTime = (int)MathUtil.clamp(mc.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime(), 0, 300); } catch (NullPointerException exception) {}
            String fName = String.format("%s %sms %s", name, responseTime, ChatFormatting.GREEN + String.valueOf(Math.floor(player.getHealth() + player.getAbsorptionAmount())));
            CFonts.roboto22.drawStringWithShadow(fName, bounds[0] + (bounds[2] - bounds[0]) / 2 - CFonts.roboto22.getStringWidth(fName) / 2, bounds[1] + (bounds[3] - bounds[1]) - 8 - 1, color);
            if(armor.isEnabled()) {
                final Iterator<ItemStack> items = player.getArmorInventoryList().iterator();
                final ArrayList<ItemStack> stacks = new ArrayList<>();
                stacks.add(player.getHeldItemOffhand());
                while(items.hasNext()) {
                    final ItemStack itemStack = items.next();
                    if(itemStack != null && itemStack.getItem() != Items.AIR)
                        stacks.add(itemStack);
                } stacks.add(player.getHeldItemMainhand());
                Collections.reverse(stacks);

                int x = 0;
                if(!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().hasDisplayName()) {
                    fName = player.getHeldItemMainhand().getDisplayName();
                    CFonts.roboto15.drawStringWithShadow(fName, bounds[0] + (bounds[2] - bounds[0]) / 2 - CFonts.roboto15.getStringWidth(fName) / 2, bounds[1] + (bounds[3] - bounds[1]) - mc.fontRenderer.FONT_HEIGHT - 35, -1);
                }
                for(ItemStack itemStack : stacks) {
                    if(itemStack == null)
                        continue;
                    final Item item = itemStack.getItem();

                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.translate(bounds[0] + (bounds[2] - bounds[0]) / 2 + x - (16 * stacks.size() / 2), bounds[1] + (bounds[3] - bounds[1]) - mc.fontRenderer.FONT_HEIGHT - 19, 0);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, 0, 0);
                    mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, 0, 0);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.popMatrix();
                    x += 16;

                    final List<String> strings = Lists.newArrayList();
                    if(itemStack.isItemDamaged()) {
                        float armorPct = ((float)(itemStack.getMaxDamage()-itemStack.getItemDamage()) /  (float)itemStack.getMaxDamage())*100.0f;
                        float armorBarPct = Math.min(armorPct, 100.0f);
                        strings.add(String.format("%s", (int)armorBarPct + "%"));
                    } int y = 0;
                    final NBTTagList tags = itemStack.getEnchantmentTagList();
                    for(int i = 0; i < tags.tagCount(); i++) {
                        final NBTTagCompound tagCompound = tags.getCompoundTagAt(i);
                        if(Enchantment.getEnchantmentByID(tagCompound.getByte("id")) != null) {
                            Enchantment enchantment = Enchantment.getEnchantmentByID(tagCompound.getShort("id"));
                            final short level = tagCompound.getShort("lvl");
                            if(enchantment != null) {
                                String ench = "";
                                if(enchantment.isCurse())
                                    ench = ChatFormatting.RED + enchantment.getTranslatedName(level).substring(11).substring(0, 2) + ChatFormatting.GRAY + level;
                                else ench = enchantment.getTranslatedName(level).substring(0, 2) + level;
                                strings.add(ench);
                            }
                        }
                    }
                    if(item == Items.GOLDEN_APPLE && itemStack.getItemDamage() == 1)
                        strings.add(ChatFormatting.DARK_RED + "God");
                    for(String string : strings) {
                        GlStateManager.pushMatrix();
                        GlStateManager.disableDepth();
                        GlStateManager.translate(bounds[0] + (bounds[2] - bounds[0]) / 2 + x - ((16.0f * stacks.size()) / 2.0f) - (16.0f / 2.0f) - (CFonts.roboto22.getStringWidth(string) / 4.0f), bounds[1] + (bounds[3] - bounds[1]) - mc.fontRenderer.FONT_HEIGHT - 23 - y, 0);
                        GlStateManager.scale(.5f, .5f, .5f);
                        CFonts.roboto22.drawStringWithShadow(string, 0, 0, -1);
                        GlStateManager.scale(2, 2, 2);
                        GlStateManager.enableDepth();
                        GlStateManager.popMatrix();
                        y += 4;
                    }
                }
            }
        }
    }

    @EventHandler private Listener<RenderGameOverlayEvent.Text> renderGameOverlayEventListener = new Listener<>(event -> mc.world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter(entity -> !EntityUtil.isFakeLocalPlayer(entity)).filter(entity -> (entity instanceof EntityPlayer && mc.player != entity)).forEach(e -> renderNametag((EntityPlayer)e, event)));
    @EventHandler private Listener<RenderPlayerNameEvent> renderPlayerNameEventListener = new Listener<>(Cancellable::cancel);
}
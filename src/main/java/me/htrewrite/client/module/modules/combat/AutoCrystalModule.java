package me.htrewrite.client.module.modules.combat;

import static me.htrewrite.client.util.ChatColor.*;

import io.netty.util.internal.ConcurrentSet;
import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.player.UpdateWalkingPlayerEvent;
import me.htrewrite.client.event.custom.render.RenderEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.client.util.Timer;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.phobosimports.BlockUtil;
import me.htrewrite.phobosimports.DamageUtil;
import me.htrewrite.phobosimports.EntityUtil;
import me.htrewrite.phobosimports.RenderUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/* WARNING!! This module is unfinished please don't add it. */
public class AutoCrystalModule extends Module {
    public static final ModeSetting setting = new ModeSetting("Setting", null, Settings.PLACE.ordinal(), BetterMode.construct(enumToStringArray(Settings.values())));
    public static final ModeSetting raytrace = new ModeSetting("Raytrace", null, Raytrace.NONE.ordinal(), BetterMode.construct(enumToStringArray(Raytrace.values())));
    public static final ToggleableSetting place = new ToggleableSetting("Place", null, true);
    public static final ValueSetting<Double> placeDelay = new ValueSetting<>("PlaceDelay", null, 25D, 0D, 500D);
    public static final ValueSetting<Double> placeRange = new ValueSetting<>("PlaceRange", null, 6D, 0D, 10D);
    public static final ValueSetting<Double> minDamage = new ValueSetting<>("MinDamage", null, 7D, .1D, 20D);
    public static final ValueSetting<Double> maxSelfPlace = new ValueSetting<>("MaxSelfPlace", null, 10D, .1D, 36D);
    public static final ValueSetting<Double> wasteAmount = new ValueSetting<>("WasteAmount", null, 2D, 1D, 5D);
    public static final ToggleableSetting wasteMinDmgAmount = new ToggleableSetting("CountMinDmg", null, false);
    public static final ValueSetting<Double> facePlace = new ValueSetting<>("FacePlace", null, 8D, .1D, 20D);
    public static final ValueSetting<Double> placetrace = new ValueSetting<>("Placetrace", null, 4.5D, 0D, 10D);
    public static final ToggleableSetting antiSurround = new ToggleableSetting("AntiSurround", null, true);
    public static final ToggleableSetting limitFacePlace = new ToggleableSetting("LimitFacePlace", null, true);
    public static final ToggleableSetting oneDot15 = new ToggleableSetting("1.15", null, false);
    public static final ToggleableSetting doublePop = new ToggleableSetting("AntiTotem", null, false);
    public static final ValueSetting<Double> popHealth = new ValueSetting<>("PopHealth", null, 1D, 0D, 3D);
    public static final ValueSetting<Double> popDamage = new ValueSetting<>("PopDamage", null, 4D, 0D, 6D);
    public static final ValueSetting<Double> popTime = new ValueSetting<>("PopTime", null, 500D, 0D, 1000D);
    public static final ToggleableSetting explode = new ToggleableSetting("Break", null, true);
    public static final ModeSetting switchMode = new ModeSetting("Attack", null, Switch.BREAKSLOT.ordinal(), BetterMode.construct(enumToStringArray(Switch.values())));
    public static final ValueSetting<Double> breakDelay = new ValueSetting<>("BreakDelay", null, 50D, 0D, 500D);
    public static final ValueSetting<Double> breakRange = new ValueSetting<>("BreakRange", null, 6D, 0D, 10D);
    public static final ValueSetting<Double> packets = new ValueSetting<>("Packets", null, 1D, 1D, 6D);
    public static final ValueSetting<Double> maxSelfBreak = new ValueSetting<>("MaxSelfBreak", null, 10D, .1D, 36D);
    public static final ValueSetting<Double> breaktrace = new ValueSetting<>("Breaktrace", null, 4.5D, 0D, 10D);
    public static final ToggleableSetting manual = new ToggleableSetting("Manual", null, true);
    public static final ToggleableSetting manualMinDmg = new ToggleableSetting("ManualMinDmg", null, true);
    public static final ValueSetting<Double> manualBreak = new ValueSetting<>("ManualDelay", null, 500D, 0D, 500D);
    public static final ToggleableSetting sync = new ToggleableSetting("Sync", null, true);
    public static final ToggleableSetting instant = new ToggleableSetting("Predict", null, true);
    public static final ModeSetting instantTimer = new ModeSetting("PredictTimer", null, PredictTimer.NONE.ordinal(), BetterMode.construct(enumToStringArray(PredictTimer.values())));
    public static final ToggleableSetting resetBreakTimer = new ToggleableSetting("ResetBreakTimer", null, true);
    public static final ValueSetting<Double> predictDelay = new ValueSetting<>("PredictDelay", null, 12D, 0D, 500D);
    public static final ToggleableSetting predictCalc = new ToggleableSetting("PredictCalc", null, true);
    public static final ToggleableSetting superSafe = new ToggleableSetting("SuperSafe", null, true);
    public static final ToggleableSetting antiCommit = new ToggleableSetting("AntiOverCommit", null, true);
    public static final ToggleableSetting render = new ToggleableSetting("Render", null, true);
    public static final ToggleableSetting box = new ToggleableSetting("Box", null, true);
    public static final ToggleableSetting outline = new ToggleableSetting("Outline", null, true);
    public static final ToggleableSetting text = new ToggleableSetting("Text", null, false);
    public static final ValueSetting<Double> red = new ValueSetting<>("Red", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> green = new ValueSetting<>("Green", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> blue = new ValueSetting<>("Blue", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> alpha = new ValueSetting<>("Alpha", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> boxAlpha = new ValueSetting<>("BoxAlpha", null, 125D, 0D, 255D);
    public static final ValueSetting<Double> lineWidth = new ValueSetting<>("LineWidth", null, 1.5D, 0.1D, 5D);
    public static final ToggleableSetting customOutline = new ToggleableSetting("CustomOutline", null, false);
    public static final ValueSetting<Double> cRed = new ValueSetting<>("OL-Red", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> cGreen = new ValueSetting<>("OL-Green", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> cBlue = new ValueSetting<>("OL-Blue", null, 255D, 0D, 255D);
    public static final ValueSetting<Double> cAlpha = new ValueSetting<>("OL-Alpha", null, 255D, 0D, 255D);
    public static final ToggleableSetting holdFacePlace = new ToggleableSetting("HoldFacePlace", null, false);
    public static final ToggleableSetting holdFaceBreak = new ToggleableSetting("HoldFaceBreak", null, false);
    public static final ToggleableSetting slowFaceBreak = new ToggleableSetting("SlowFaceBreak", null, false);
    public static final ToggleableSetting actualSlowBreak = new ToggleableSetting("ActuallySlow", null, false);
    public static final ValueSetting<Double> facePlaceSpeed = new ValueSetting<>("FaceSpeed", null, 500D, 0D, 500D);
    public static final ToggleableSetting antiNaked = new ToggleableSetting("AntiNaked", null, true);
    public static final ValueSetting<Double> range = new ValueSetting<>("Range", null, 12D, .1D, 20D);
    public static final ModeSetting targetMode = new ModeSetting("Target", null, Target.CLOSEST.ordinal(), BetterMode.construct(enumToStringArray(Target.values())));
    public static final ValueSetting<Double> minArmor = new ValueSetting<>("MinArmor", null, 5D, 0D, 125D);
    public static final ValueSetting<Double> switchCooldown = new ValueSetting<>("Cooldown", null, 500D, 0D, 1000D);
    public static final ModeSetting autoSwitch = new ModeSetting("Switch", null, AutoSwitch.TOGGLE.ordinal(), BetterMode.construct(enumToStringArray(AutoSwitch.values())));
    public static final StringSetting switchBind = new StringSetting("SwitchBind", null, "NONE");
    public static final ToggleableSetting offhandSwitch = new ToggleableSetting("Offhand", null, true);
    public static final ToggleableSetting switchBack = new ToggleableSetting("Switchback", null, true);
    public static final ToggleableSetting lethalSwitch = new ToggleableSetting("LethalSwitch", null, false);
    public static final ToggleableSetting mineSwitch = new ToggleableSetting("MineSwitch", null, true);
    public static final ModeSetting rotate = new ModeSetting("Rotate", null, Rotate.OFF.ordinal(), BetterMode.construct(enumToStringArray(Rotate.values())));
    public static final ToggleableSetting suicide = new ToggleableSetting("Suicide", null, false);
    public static final ToggleableSetting webAttack = new ToggleableSetting("WebAttack", null, true);
    public static final ToggleableSetting fullCalc = new ToggleableSetting("ExtraCalc", null, false);
    public static final ToggleableSetting sound = new ToggleableSetting("Sound", null, true);
    public static final ValueSetting<Double> soundRange = new ValueSetting<>("SoundRange", null, 12D, 0D, 12D);
    public static final ValueSetting<Double> soundPlayer = new ValueSetting<>("SoundPlayer", null, 6D, 0D, 12D);
    public static final ToggleableSetting soundConfirm = new ToggleableSetting("SoundConfirm", null, true);
    public static final ToggleableSetting extraSelfCalc = new ToggleableSetting("MinSelfDmg", null, false);
    public static final ModeSetting antiFriendPop = new ModeSetting("FriendPop", null, AntiFriendPop.NONE.ordinal(), BetterMode.construct(enumToStringArray(AntiFriendPop.values())));
    public static final ToggleableSetting noCount = new ToggleableSetting("AntiCount", null, false);
    public static final ToggleableSetting calcEvenIfNoDamage = new ToggleableSetting("BigFriendCalc", null, false);
    public static final ToggleableSetting predictFriendDmg = new ToggleableSetting("PredictFriend", null, false);
    public static final ValueSetting<Double> minMinDmg = new ValueSetting<>("MinMinDmg", null, 0D, 0D, 3D);
    public static final ToggleableSetting attackOppositeHand = new ToggleableSetting("OppositeHand", null, false);
    public static final ToggleableSetting removeAfterAttack = new ToggleableSetting("AttackRemove", null, false);
    public static final ToggleableSetting breakSwing = new ToggleableSetting("BreakSwing", null, true);
    public static final ToggleableSetting placeSwing = new ToggleableSetting("PlaceSwing", null, false);
    public static final ToggleableSetting exactHand = new ToggleableSetting("ExactHand", null, false);
    public static final ToggleableSetting justRender = new ToggleableSetting("JustRender", null, false);
    public static final ToggleableSetting fakeSwing = new ToggleableSetting("FakeSwing", null, false);
    public static final ModeSetting logic = new ModeSetting("Logic", null, Logic.BREAKPLACE.ordinal(), BetterMode.construct(enumToStringArray(Logic.values())));
    public static final ModeSetting damageSync = new ModeSetting("DamageSync", null, DamageSync.NONE.ordinal(), BetterMode.construct(enumToStringArray(DamageSync.values())));
    public static final ValueSetting<Double> damageSyncTime = new ValueSetting<>("SyncDelay", null, 500D, 0D, 500D);
    public static final ValueSetting<Double> dropOff = new ValueSetting<>("DropOff", null, 5D, 0D, 10D);
    public static final ValueSetting<Double> confirm = new ValueSetting<>("Confirm", null, 250D, 0D, 1000D);
    public static final ToggleableSetting syncedFeetPlace = new ToggleableSetting("FeetSync", null, false);
    public static final ToggleableSetting fullSync = new ToggleableSetting("FullSync", null, false);
    public static final ToggleableSetting syncCount = new ToggleableSetting("SyncCount", null, true);
    public static final ToggleableSetting hyperSync = new ToggleableSetting("HyperSync", null, false);
    public static final ToggleableSetting gigaSync = new ToggleableSetting("GigaSync", null, false);
    public static final ToggleableSetting syncySync = new ToggleableSetting("SyncySync", null, false);
    public static final ToggleableSetting enormousSync = new ToggleableSetting("EnormousSync", null, false);
    public static final ToggleableSetting holySync = new ToggleableSetting("HolySync", null, false);
    public static final ValueSetting<Double> eventMode = new ValueSetting<>("Updates", null, 3D, 1D, 3D);
    public static final ToggleableSetting rotateFirst = new ToggleableSetting("FirstRotation", null, false);
    public static final ModeSetting threadMode = new ModeSetting("Thread", null, ThreadMode.NONE.ordinal(), BetterMode.construct(enumToStringArray(ThreadMode.values())));
    public static final ValueSetting<Double> threadDelay = new ValueSetting<>("ThreadDelay", null, 50D, 1D, 1000D);
    public static final ToggleableSetting syncThreadBool = new ToggleableSetting("ThreadSync", null, true);
    public static final ValueSetting<Double> syncThreads = new ValueSetting<>("SyncThreads", null, 1000D, 1D, 10000D);
    public static final ToggleableSetting predictPos = new ToggleableSetting("PredictPos", null, false);
    public static final ToggleableSetting renderExtrapolation = new ToggleableSetting("RenderExtrapolation", null, false);
    public static final ValueSetting<Double> predictTicks = new ValueSetting<>("ExtrapolationTicks", null, 2D, 1D, 20D);
    public static final ValueSetting<Double> rotations = new ValueSetting<>("Spoofs", null, 1D, 1D, 20D);
    public static final ToggleableSetting predictRotate = new ToggleableSetting("PredictRotate", null, false);
    public static final ValueSetting<Double> predictOffset = new ValueSetting<>("PredictOffset", null, 0D, 0D, 4D);
    public static final ToggleableSetting doublePopOnDamage = new ToggleableSetting("DamagePop", null, false);

    private Queue<Entity> attackList = new ConcurrentLinkedQueue<Entity>();
    private Map<Entity, Float> crystalMap = new HashMap<>();
    private final Timer switchTimer = new Timer();
    private final Timer manualTimer = new Timer();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer syncTimer = new Timer();
    private final Timer predictTimer = new Timer();
    public static EntityPlayer target = null;
    private Entity efficientTarget = null;
    private double currentDamage = 0.0;
    private double renderDamage = 0.0;
    private double lastDamage = 0.0;
    private boolean didRotation = false;
    private boolean switching = false;
    private BlockPos placePos = null;
    private BlockPos renderPos = null;
    private boolean mainHand = false;
    public boolean rotating = false;
    private boolean offHand = false;
    private int crystalCount = 0;
    private int minDmgCount = 0;
    private int lastSlot = -1;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private BlockPos webPos = null;
    private final Timer renderTimer = new Timer();
    private BlockPos lastPos = null;
    public static Set<BlockPos> lowDmgPos = new ConcurrentSet<>();
    public static Set<BlockPos> placedPos = new HashSet<>();
    public static Set<BlockPos> brokenPos = new HashSet<>();
    private boolean posConfirmed = false;
    private boolean foundDoublePop = false;
    private int rotationPacketsSpoofed = 0;
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private ScheduledExecutorService executor;
    private final Timer syncroTimer = new Timer();
    private Thread thread;
    private EntityPlayer currentSyncTarget;
    private BlockPos syncedPlayerPos;
    private BlockPos syncedCrystalPos;
    private static AutoCrystalModule instance;
    private final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<>();
    private final Queue<CPacketUseEntity> packetUseEntities = new LinkedList<CPacketUseEntity>();
    private PlaceInfo placeInfo;
    private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
    public final Timer threadTimer = new Timer();
    private boolean addTolowDmg;

    public AutoCrystalModule() {
        super("AutoCrystal", "Places and breaks crystals.", ModuleType.Combat, 0);
        addOption(setting);
        addOption(raytrace.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(place.setVisibility(v -> setting.getValue().contentEquals("PLACE")));
        addOption(placeDelay.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(placeRange.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(minDamage.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(maxSelfPlace.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(wasteAmount.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(wasteMinDmgAmount.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(facePlace.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(placetrace.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled() && !raytrace.getValue().contentEquals("NONE") && !raytrace.getValue().contentEquals("BREAK")));
        addOption(antiSurround.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(limitFacePlace.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(oneDot15.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(doublePop.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled()));
        addOption(popHealth.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled() && doublePop.isEnabled()));
        addOption(popDamage.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled() && doublePop.isEnabled()));
        addOption(popTime.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled() && doublePop.isEnabled()));
        addOption(explode.setVisibility(v -> setting.getValue().contentEquals("BREAK")));
        addOption(switchMode.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled()));
        addOption(breakDelay.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled()));
        addOption(breakRange.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled()));
        addOption(packets.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled()));
        addOption(maxSelfBreak.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled()));
        addOption(breaktrace.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && !raytrace.getValue().equalsIgnoreCase("NONE") && !raytrace.getValue().equalsIgnoreCase("PLACE")));
        addOption(manual.setVisibility(v -> setting.getValue().contentEquals("BREAK")));
        addOption(manualMinDmg.setVisibility(v -> setting.getValue().contentEquals("BREAK") && manual.isEnabled()));
        addOption(manualBreak.setVisibility(v -> setting.getValue().contentEquals("BREAK") && manual.isEnabled()));
        addOption(sync.setVisibility(v -> setting.getValue().contentEquals("BREAK") && (explode.isEnabled() || manual.isEnabled())));
        addOption(instant.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled()));
        addOption(instantTimer.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled() && instant.isEnabled()));
        addOption(resetBreakTimer.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled() && instant.isEnabled()));
        addOption(predictDelay.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled() && instant.isEnabled() && instantTimer.getValue().contentEquals("PREDICT")));
        addOption(predictCalc.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled() && instant.isEnabled()));
        addOption(superSafe.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled() && instant.isEnabled()));
        addOption(antiCommit.setVisibility(v -> setting.getValue().contentEquals("BREAK") && explode.isEnabled() && place.isEnabled() && instant.isEnabled()));
        addOption(render.setVisibility(v -> setting.getValue().contentEquals("RENDER")));
        addOption(box.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(outline.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(text.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(red.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(green.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(blue.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(alpha.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled()));
        addOption(boxAlpha.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && box.isEnabled()));
        addOption(lineWidth.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && outline.isEnabled()));
        addOption(customOutline.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && outline.isEnabled()));
        addOption(cRed.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && outline.isEnabled() && customOutline.isEnabled()));
        addOption(cGreen.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && outline.isEnabled() && customOutline.isEnabled()));
        addOption(cBlue.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && outline.isEnabled() && customOutline.isEnabled()));
        addOption(cAlpha.setVisibility(v -> setting.getValue().contentEquals("RENDER") && render.isEnabled() && outline.isEnabled() && customOutline.isEnabled()));
        addOption(holdFacePlace.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(holdFaceBreak.setVisibility(v -> setting.getValue().contentEquals("MISC") && holdFacePlace.isEnabled()));
        addOption(slowFaceBreak.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(actualSlowBreak.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(facePlaceSpeed.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(antiNaked.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(range.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(targetMode.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(minArmor.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(switchCooldown.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(autoSwitch.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(switchBind.setVisibility(v -> setting.getValue().contentEquals("MISC") && autoSwitch.getValue().contentEquals("TOGGLE")));
        addOption(offhandSwitch.setVisibility(v -> setting.getValue().contentEquals("MISC") && !autoSwitch.getValue().contentEquals("NONE")));
        addOption(switchBack.setVisibility(v -> setting.getValue().contentEquals("MISC") && !autoSwitch.getValue().contentEquals("NONE") && offhandSwitch.isEnabled()));
        addOption(lethalSwitch.setVisibility(v -> setting.getValue().contentEquals("MISC") && !autoSwitch.getValue().contentEquals("NONE")));
        addOption(mineSwitch.setVisibility(v -> setting.getValue().contentEquals("MISC") && !autoSwitch.getValue().contentEquals("NONE")));
        addOption(rotate.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(suicide.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(webAttack.setVisibility(v -> setting.getValue().contentEquals("MISC") && !targetMode.getValue().contentEquals("DAMAGE")));
        addOption(fullCalc.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(sound.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(soundRange.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(soundPlayer.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(soundConfirm.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(extraSelfCalc.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(antiFriendPop.setVisibility(v -> setting.getValue().contentEquals("MISC")));
        addOption(noCount.setVisibility(v -> setting.getValue().contentEquals("MISC") && (antiFriendPop.getValue().contentEquals("ALL") || antiFriendPop.getValue().contentEquals("BREAK"))));
        addOption(calcEvenIfNoDamage.setVisibility(v -> setting.getValue().contentEquals("MISC") && (antiFriendPop.getValue().contentEquals("ALL") || antiFriendPop.getValue().contentEquals("BREAK")) && !targetMode.getValue().contentEquals("DAMAGE")));
        addOption(predictFriendDmg.setVisibility(v -> setting.getValue().contentEquals("MISC") && (antiFriendPop.getValue().contentEquals("ALL") || antiFriendPop.getValue().contentEquals("BREAK")) && instant.isEnabled()));
        addOption(minMinDmg.setVisibility(v -> setting.getValue().contentEquals("DEV") && place.isEnabled()));
        addOption(attackOppositeHand.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(removeAfterAttack.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(breakSwing.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(placeSwing.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(exactHand.setVisibility(v -> setting.getValue().contentEquals("DEV") && placeSwing.isEnabled()));
        addOption(justRender.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(fakeSwing.setVisibility(v -> setting.getValue().contentEquals("DEV") && justRender.isEnabled()));
        addOption(logic.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(damageSync.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(damageSyncTime.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE")));
        addOption(dropOff.setVisibility(v -> setting.getValue().contentEquals("DEV") && damageSync.getValue().contentEquals("BREAK")));
        addOption(confirm.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE")));
        addOption(syncedFeetPlace.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE")));
        addOption(fullSync.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(syncCount.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(hyperSync.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(gigaSync.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(syncySync.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(enormousSync.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(holySync.setVisibility(v -> setting.getValue().contentEquals("DEV") && !damageSync.getValue().contentEquals("NONE") && syncedFeetPlace.isEnabled()));
        addOption(eventMode.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(rotateFirst.setVisibility(v -> setting.getValue().contentEquals("DEV") && !rotate.getValue().contentEquals("OFF") && eventMode.getValue().intValue()==2));
        addOption(threadMode.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(threadDelay.setVisibility(v -> setting.getValue().contentEquals("DEV") && !threadMode.getValue().contentEquals("NONE")));
        addOption(syncThreadBool.setVisibility(v -> setting.getValue().contentEquals("DEV") && !threadMode.getValue().contentEquals("NONE")));
        addOption(syncThreads.setVisibility(v -> setting.getValue().contentEquals("DEV") && !threadMode.getValue().contentEquals("NONE") && syncThreadBool.isEnabled()));
        addOption(predictPos.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(renderExtrapolation.setVisibility(v -> setting.getValue().contentEquals("DEV") && predictPos.isEnabled()));
        addOption(predictTicks.setVisibility(v -> setting.getValue().contentEquals("DEV") && predictPos.isEnabled()));
        addOption(rotations.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(predictRotate.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(predictOffset.setVisibility(v -> setting.getValue().contentEquals("DEV")));
        addOption(doublePopOnDamage.setVisibility(v -> setting.getValue().contentEquals("PLACE") && place.isEnabled() && doublePop.isEnabled() && targetMode.getValue().contentEquals("DAMAGE")));

        instance = this;
    }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> eventListener = new Listener<>(event -> {
        if(threadMode.getValue().contentEquals("NONE") && eventMode.getValue().intValue() == 3)
            doAutoCrystal();
    });

    @EventHandler
    private Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = new Listener<>(event -> {
        if(event.getEra() == CustomEvent.Era.PRE) {
            postProcessing();
            return;
        }
        if(!threadMode.getValue().contentEquals("NONE"))
            processMultiThreading();
        else if(eventMode.getValue().intValue() == 2)
            doAutoCrystal();
    });

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;

        if(threadMode.getValue().contentEquals("NONE") && eventMode.getValue().intValue()==1)
            doAutoCrystal();
    });

    @Override
    public void toggle() {
        super.toggle();

        brokenPos.clear();
        placedPos.clear();
        totemPops.clear();
        rotating = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if(thread != null)
            shouldInterrupt.set(true);
        if(executor != null)
            executor.shutdown();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if(!threadMode.getValue().contentEquals("NONE"))
            processMultiThreading();
    }

    @Override
    public String getMeta() {
        if(target != null)
            return target.getName();
        return "";
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(nullCheck())
            return;

        if(!event.reading && event.getEra() == CustomEvent.Era.PRE) {
            if (!rotate.getValue().contentEquals("OFF") && rotating && eventMode.getValue().intValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                packet.yaw = this.yaw;
                packet.pitch = this.pitch;
                ++rotationPacketsSpoofed;
                if (rotationPacketsSpoofed >= rotations.getValue()) {
                    rotating = false;
                    rotationPacketsSpoofed = 0;
                }
            }
            if(event.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
                if(packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                    if(attackOppositeHand.isEnabled())
                        packet.hand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)?EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    if(removeAfterAttack.isEnabled()) {
                        packet.getEntityFromWorld(mc.world).setDead();
                        mc.world.removeEntityFromWorld(packet.entityId);
                    }
                }
            }
        }
        if(event.reading && event.getEra()== CustomEvent.Era.PRE) {
            SPacketSoundEffect packetSound;
            if (!justRender.isEnabled() && explode.isEnabled() && instant.isEnabled() && event.getPacket() instanceof SPacketSpawnObject && (syncedCrystalPos == null || !syncedFeetPlace.isEnabled() || damageSync.getValue().contentEquals("NONE"))) {
                SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();

                BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if (packet.getType() == 51 && mc.player.getDistanceSq(pos) + predictOffset.getValue() <= MathUtil.square(breakRange.getValue()) && (instantTimer.getValue().contentEquals("NONE") || instantTimer.getValue().contentEquals("BREAK") && breakTimer.passed(breakDelay.getValue().intValue()) || instantTimer.getValue().contentEquals("PREDICT") && predictTimer.passed(predictDelay.getValue().intValue()))) {
                    if (predictSlowBreak(pos.down()))
                        return;
                    if (predictFriendDmg.isEnabled() && (antiFriendPop.getValue().contentEquals("BREAK") || antiFriendPop.getValue().contentEquals("ALL")) && isRightThread())
                        for (EntityPlayer friend : mc.world.playerEntities) {
                            if (friend == null || mc.player == friend || friend.getDistanceSq(pos) > MathUtil.square(range.getValue() + placeRange.getValue()) || HTRewrite.INSTANCE.getFriendManager().isFriend(friend.getName()) || !((double) DamageUtil.calculateDamage(pos, (Entity) friend) > (double) EntityUtil.getHealth((Entity) friend) + .5))
                                continue;
                            return;
                        }
                    if (placedPos.contains(pos.down())) {
                        double selfDamage = DamageUtil.calculateDamage(pos, mc.player);
                        if (isRightThread() && superSafe.isEnabled() ? DamageUtil.canTakeDamage(suicide.isEnabled()) && selfDamage - .5 > (double) EntityUtil.getHealth(mc.player) || (selfDamage > maxSelfBreak.getValue()) : superSafe.isEnabled())
                            return;
                        attackCrystalPredict(packet.getEntityID(), pos);
                    } else if (predictCalc.isEnabled() && isRightThread()) {
                        float selfDamage = -1.0f;
                        if (DamageUtil.canTakeDamage(suicide.isEnabled()))
                            selfDamage = DamageUtil.calculateDamage(pos, mc.player);
                        if ((double) selfDamage + .5 < (double) EntityUtil.getHealth(mc.player) && selfDamage <= maxSelfBreak.getValue().floatValue())
                            for (EntityPlayer player : mc.world.playerEntities) {
                                float damage = DamageUtil.calculateDamage(pos, player);
                                if (!(player.getDistanceSq(pos) <= MathUtil.square(range.getValue())) || !EntityUtil.isValid(player, range.getValue().floatValue() + breakRange.getValue().floatValue()) || antiNaked.isEnabled() && DamageUtil.isNaked(player) || !(damage > selfDamage || damage > minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(suicide.isEnabled())) && !(damage > EntityUtil.getHealth(player)))
                                    continue;
                                if (predictRotate.isEnabled() && eventMode.getValue().intValue() != 2 && (rotate.getValue().contentEquals("BREAK") || rotate.getValue().contentEquals("ALL")))
                                    rotateToPos(pos);
                                attackCrystalPredict(packet.getEntityID(), pos);
                                break;
                            }
                    }
                }
            } else if (!soundConfirm.isEnabled() && event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion packetExplosion = (SPacketExplosion) event.getPacket();
                BlockPos pos = new BlockPos(packetExplosion.getX(), packetExplosion.getY(), packetExplosion.getZ()).down();
                removePos(pos);
            } else if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packetDestroy = (SPacketDestroyEntities)event.getPacket();
                for(int id : packetDestroy.getEntityIDs()) {
                    Entity entity = mc.world.getEntityByID(id);
                    if(!(entity instanceof EntityEnderCrystal))
                        continue;
                    brokenPos.remove(new BlockPos(entity.getPositionVector()).down());
                    placedPos.remove(new BlockPos(entity.getPositionVector()).down());
                }
            } else if(event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packetStatus = (SPacketEntityStatus)event.getPacket();
                if(packetStatus.getOpCode() == 35 && packetStatus.getEntity(mc.world) instanceof EntityPlayer) {
                    Timer timer = new Timer();
                    timer.reset();
                    totemPops.put((EntityPlayer)packetStatus.getEntity(mc.world), timer);
                }
            } else if(event.getPacket() instanceof SPacketSoundEffect && (packetSound = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packetSound.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                BlockPos pos = new BlockPos(packetSound.getX(), packetSound.getY(), packetSound.getZ());
                if(sound.isEnabled() || threadMode.getValue().contentEquals("SOUND"))
                    removeEntities(packetSound, soundRange.getValue().floatValue());
                if(soundConfirm.isEnabled())
                    removePos(pos);
                if(threadMode.getValue().contentEquals("SOUND") && isRightThread() && mc.player != null && mc.player.getDistanceSq(pos) < MathUtil.square(soundPlayer.getValue().floatValue()))
                    handlePool(true);
            }
        }
    });

    private Listener<RenderEvent> renderEventListener = new Listener<>(event -> {
        if((offHand || mainHand || switchMode.getValue().contentEquals("CALC")) && renderPos != null && render.isEnabled() && (box.isEnabled() || text.isEnabled() || outline.isEnabled())) {
            RenderUtil.drawBoxESP(renderPos, new Color(red.getValue().intValue(), green.getValue().intValue(), blue.getValue().intValue(), alpha.getValue().intValue()), customOutline.isEnabled(), new Color(cRed.getValue().intValue(), cGreen.getValue().intValue(), cBlue.getValue().intValue(), cAlpha.getValue().intValue()), lineWidth.getValue().floatValue(), outline.isEnabled(), box.isEnabled(), boxAlpha.getValue().intValue(), false);
            if(text.isEnabled())
                RenderUtil.drawText(renderPos, (Math.floor(renderDamage) == renderDamage ? String.valueOf((int)renderDamage) : String.format("%.1f", renderDamage)) + "");
        }
    });



    public void removeEntities(SPacketSoundEffect packet, float range) {
        BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        ArrayList<Entity> toRemove = new ArrayList<Entity>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(pos) <= MathUtil.square(range))) continue;
            toRemove.add(entity);
        }
        for (Entity entity : toRemove) {
            entity.setDead();
        }
    }

    private boolean isRightThread() { return mc.isCallingFromMinecraftThread() || !HTRewrite.INSTANCE.getEventProcessor().ticksOngoing() && !threadOngoing.get(); }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if(!(!predictRotate.isEnabled() || eventMode.getValue().intValue() == 2 && threadMode.getValue().contentEquals("NONE") || !rotate.getValue().contentEquals("BREAK") && !rotate.getValue().contentEquals("ALL")))
            rotateToPos(pos);
        CPacketUseEntity packetUseEntity = new CPacketUseEntity();
        packetUseEntity.entityId = entityID;
        packetUseEntity.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(packetUseEntity);
        if(breakSwing.isEnabled())
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        if(resetBreakTimer.isEnabled())
            breakTimer.reset();
        predictTimer.reset();
    }

    private void removePos(BlockPos pos) {
        if(damageSync.getValue().contentEquals("PLACE")) {
            if(placedPos.remove(pos))
                posConfirmed = true;
        } else if(damageSync.getValue().contentEquals("BREAK") && brokenPos.remove(pos))
            posConfirmed = true;
    }

    private boolean predictSlowBreak(BlockPos pos) {
        if (antiCommit.isEnabled() && lowDmgPos.remove(pos)) {
            return this.shouldSlowBreak(false);
        }
        return false;
    }

    private void postProcessing() {
        if(!threadMode.getValue().contentEquals("NONE") || eventMode.getValue().intValue() != 2 || rotate.getValue().contentEquals("OFF") || !rotateFirst.isEnabled())
            return;

        switch(logic.getValue()) {
            case "BREAKPLACE": {
                postProcessBreak();
                postProcessPlace();
                break;
            }
            case "PLACEBREAK": {
                postProcessPlace();
                postProcessBreak();
                break;
            }
        }
    }

    // TODO: 586

    private void postProcessBreak() {
        while (!packetUseEntities.isEmpty()) {
            CPacketUseEntity packet = packetUseEntities.poll();
            mc.player.connection.sendPacket(packet);
            if(breakSwing.isEnabled())
                mc.player.swingArm(EnumHand.MAIN_HAND);
            breakTimer.reset();
        }
    }

    private void postProcessPlace() {
        if(placeInfo != null) {
            placeInfo.runPlace();
            placeTimer.reset();
            placeInfo = null;
        }
    }

    private void handleWhile() {}

    private void processMultiThreading() {
        if(!isEnabled())
            return;

        if(threadMode.getValue().contentEquals("WHILE"))
            handleWhile();
        else if(!threadMode.getValue().contentEquals("NONE")) handlePool(false);
    }

    private void handlePool(boolean justDoIt) {
        if(justDoIt || executor == null || executor.isTerminated() || executor.isShutdown() || syncroTimer.passed(syncThreads.getValue().intValue()) && syncThreadBool.isEnabled()) {
            if(executor != null)
                executor.shutdown();
            executor = getExecutor();
            syncroTimer.reset();
        }
    }

    private ScheduledExecutorService getExecutor() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoCrystal.getInstance(this), 0L, this.threadDelay.getValue().intValue(), TimeUnit.MILLISECONDS);
        return service;
    }

    public void doAutoCrystal() {}

    private void rotateToPos(BlockPos pos) {}

    private boolean shouldSlowBreak(boolean withManual) { return !withManual; }

    public enum PredictTimer { NONE, BREAK, PREDICT }
    public enum AntiFriendPop { NONE, PLACE, BREAK, ALL }
    public enum ThreadMode { NONE, POOL, SOUND, WHILE }
    public enum AutoSwitch { NONE, TOGGLE, ALWAYS }
    public enum Raytrace { NONE, PLACE, BREAK, FULL }
    public enum Switch { ALWAYS, BREAKSLOT, CALC }
    public enum Logic { BREAKPLACE, PLACEBREAK }
    public enum Target { CLOSEST, UNSAFE, DAMAGE }
    public enum Rotate { OFF, PLACE, BREAK, ALL }
    public enum DamageSync { NONE, PLACE, BREAK }
    public enum Settings { PLACE, BREAK, RENDER, MISC, DEV }
    public static class PlaceInfo {
        private final BlockPos pos;
        private final boolean offhand;
        private final boolean placeSwing;
        private final boolean exactHand;

        public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand) {
            this.pos = pos;
            this.offhand = offhand;
            this.placeSwing = placeSwing;
            this.exactHand = exactHand;
        }

        public void runPlace() {
            BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand);
        }
    }
    private static class RAutoCrystal implements Runnable {
        private static RAutoCrystal instance;
        private AutoCrystalModule autoCrystal;

        public static RAutoCrystal getInstance(AutoCrystalModule autoCrystal) {
            if(instance == null) {
                instance = new RAutoCrystal();
                RAutoCrystal.instance.autoCrystal = autoCrystal;
            }

            return instance;
        }

        @Override
        public void run() {
            if(autoCrystal.threadMode.getValue().contentEquals("WHILE")) {
                while(autoCrystal.isEnabled() && autoCrystal.threadMode.getValue().contentEquals("WHILE")) {
                    while(HTRewrite.INSTANCE.getEventProcessor().ticksOngoing()) {}
                    if(autoCrystal.shouldInterrupt.get()) {
                        autoCrystal.shouldInterrupt.set(false);
                        autoCrystal.syncroTimer.reset();
                        autoCrystal.thread.interrupt();
                        break;
                    }
                    autoCrystal.threadOngoing.set(true);
                    // TODO: Safety check(1301)
                    autoCrystal.doAutoCrystal();
                    autoCrystal.threadOngoing.set(false);
                    try { Thread.sleep(autoCrystal.threadDelay.getValue().intValue()); } catch (Exception exception) {
                        autoCrystal.thread.interrupt();
                        exception.printStackTrace();
                    }
                }
            } else if(!autoCrystal.threadMode.getValue().contentEquals("NONE") && autoCrystal.isEnabled()) {
                while(HTRewrite.INSTANCE.getEventProcessor().ticksOngoing()) {}
                autoCrystal.threadOngoing.set(true);
                // TODO: Safety check(1316)
                autoCrystal.doAutoCrystal();
                autoCrystal.threadOngoing.set(false);
            }
        }
    }
}
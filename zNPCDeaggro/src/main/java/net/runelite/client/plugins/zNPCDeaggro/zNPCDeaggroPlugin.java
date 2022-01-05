package net.runelite.client.plugins.zNPCDeaggro;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.awt.Rectangle;
///api
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.GameObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;

///client
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.*;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.iutils.scripts.ReflectBreakHandler;
import net.runelite.client.ui.overlay.OverlayManager;


///iUtils
import net.runelite.client.plugins.iutils.*;
import net.runelite.client.plugins.iutils.ActionQueue;
import net.runelite.client.plugins.iutils.BankUtils;
import net.runelite.client.plugins.iutils.InventoryUtils;
import net.runelite.client.plugins.iutils.CalculationUtils;
import net.runelite.client.plugins.iutils.MenuUtils;
import net.runelite.client.plugins.iutils.MouseUtils;
import net.runelite.client.plugins.iutils.ObjectUtils;
import net.runelite.client.plugins.iutils.PlayerUtils;


import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.runelite.client.plugins.iutils.iUtils.iterating;
import static net.runelite.client.plugins.iutils.iUtils.sleep;


@Extension
@PluginDependency(iUtils.class)
@PluginDescriptor(
        name = "zNPCDeaggro",
        enabledByDefault = false,
        description = "NPC Deaggro",
        tags = {"z*", "npc", "deagrro", "crabs", "zackaery"}

)
@Slf4j
public class zNPCDeaggroPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private zNPCDeaggroConfiguration config;

    @Inject
    private iUtils utils;

    @Inject
    private ActionQueue action;

    @Inject
    private MouseUtils mouse;

    @Inject
    private PlayerUtils playerUtils;

    @Inject
    private InventoryUtils inventory;

    @Inject
    private InterfaceUtils interfaceUtils;

    @Inject
    private CalculationUtils calc;

    @Inject
    private MenuUtils menu;

    @Inject
    private ObjectUtils object;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private zNPCDeaggroOverlay overlay;

    @Inject
    private BankUtils bank;

    @Inject
    private NPCUtils npc;

    @Inject
    private KeyboardUtils key;

    @Inject
    private WalkUtils walk;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ExecutorService executorService;

    @Inject
    private ReflectBreakHandler chinBreakHandler;

    @Inject
    PluginManager pluginManager;

    NPC targetNPC;
    NPC currentNPC;
    Player player;
    String npcName;
    String NPCToAttack;
    String status;

    List<TileItem> loot = new ArrayList<>();
    List<TileItem> ammoLoot = new ArrayList<>();
    List<String> lootableItems = new ArrayList<>();

    LegacyMenuEntry targetMenu;

    Instant botTimer;
    Instant totalTimer;
    Instant lootTimer;
    Instant newLoot;

    zNPCDeaggroState state;

    LocalPoint beforeLoc = new LocalPoint(0, 0);
    WorldPoint startLoc;
    WorldPoint deathLocation;
    WorldPoint customLocation;
    WorldPoint resetLocation;
    WorldArea playerHopCheck;


    boolean waiting;
    boolean specAllowed;
    boolean goReset;
    boolean walkBack;
    boolean walkToNPC;
    boolean startBot;
    boolean hopWorld;
    boolean menuFight;
    boolean plugStarted = false;

    int randomVariationInTime;
    int randVar;
    int resetTime;
    int timeRan;
    int specCost;
    int timeTillReset;
    int killCount;
    int timeRun;
    int timeout;
    int timedOut = 0;
    int nextItemLootTime;
    int nextAmmoLootTime;
    long sleepLength;

    Set<Integer> BONE_BLACKLIST = Set.of(ItemID.CURVED_BONE, ItemID.LONG_BONE);

    @Provides
    zNPCDeaggroConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(zNPCDeaggroConfiguration.class);
    }
    @Override
    protected void startUp() {
        chinBreakHandler.registerPlugin(this);
    }

    @Override
    protected void shutDown() {
        resetVals();
        chinBreakHandler.unregisterPlugin(this);
    }

    private void resetVals() {
        log.debug("Stopping --- zNPCDeaggro");
        utils.sendGameMessage("Stopping --- zNPCDeaggro");
        overlayManager.remove(overlay);
        chinBreakHandler.stopPlugin(this);
        timedOut = 0;
        killCount = 0;
        startBot = false;
        plugStarted = false;
        walkBack = false;
        botTimer = null;
        currentNPC = null;
    }

    private void start() {
        log.debug("Starting --- zNPCDeaggro");
        System.out.println("Starting --- zNPCDeaggro");
        utils.sendGameMessage("Starting --- zNPCDeaggro");
        if (client == null || client.getLocalPlayer() == null || client.getGameState() != GameState.LOGGED_IN) {
            log.info("Startup failed, log in before starting.");
            utils.sendGameMessage("Please Login Before Starting --- zNPCDeaggro");
            return;
        }
        setLocations();
        botTimer = Instant.now();
        totalTimer = Instant.now();
        randVar = calc.getRandomIntBetweenRange(-5, 6);
        walkToNPC = true;
        startBot = true;
        walkBack = false;
        waiting = false;
        specAllowed = config.enableSpec();
        specCost = config.specCost();
        hopWorld = config.worldHopping();
        goReset = false;
        resetTime = config.resetTime();
        NPCToAttack = config.npcName();
        randomVariationInTime =
                calc.getRandomIntBetweenRange(0, config.resetTimeRandomization());
        plugStarted = true;
        killCount = 0;
        updateConfigValues();
        chinBreakHandler.startPlugin(this);
        overlayManager.add(overlay);
        if (config.safeSpot()) {
            beforeLoc = client.getLocalPlayer().getLocalLocation();
            startLoc = client.getLocalPlayer().getWorldLocation();
            utils.sendGameMessage("Safe spot set: " + startLoc.toString());
        }
        System.out.println("Custom Location:" + customLocation);
        System.out.println("Reset Location:" + resetLocation);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {

        if (!event.getGroup().equalsIgnoreCase("zNPCDeaggro")) {
            return;
        }
        if (event.getKey().equals("startButton")) {
            if (!plugStarted) {
                start();
            } else {
                resetVals();
            }
        }
        if (event.getKey().equals("forceDeaggro")) {
            if (!plugStarted) {
                start();
            } else {
                goReset = true;
            }
        }
    }

    private void updateConfigValues() {
        String[] values = config.lootItemNames().toLowerCase().split("\\s*,\\s*");
        if (config.lootItems() && !config.lootItemNames().isBlank()) {
            lootableItems.clear();
            lootableItems.addAll(Arrays.asList(values));
            utils.sendGameMessage("Lootable items are: " + lootableItems.toString());
            log.debug("Lootable items are: {}", lootableItems.toString());
        }
    }

    private long sleepDelay() {
        sleepLength = calc.randomDelay(config.sleepWeightedDistribution(), config.sleepMin(), config.sleepMax(), config.sleepDeviation(), config.sleepTarget());
        return sleepLength;
    }

    private int tickDelay() {
        int tickLength = (int) calc.randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    private TileItem getNearestTileItem(List<TileItem> tileItems) {
        int currentDistance;
        TileItem closestTileItem = tileItems.get(0);
        int closestDistance = closestTileItem.getTile().getWorldLocation().distanceTo(player.getWorldLocation());
        for (TileItem tileItem : tileItems) {
            currentDistance = tileItem.getTile().getWorldLocation().distanceTo(player.getWorldLocation());
            if (currentDistance < closestDistance) {
                closestTileItem = tileItem;
                closestDistance = currentDistance;
            }
        }
        return closestTileItem;
    }

    private void lootItem(List<TileItem> itemList) {
        TileItem lootItem = getNearestTileItem(itemList);
        if (lootItem != null) {
            targetMenu = new LegacyMenuEntry("", "", lootItem.getId(), MenuAction.GROUND_ITEM_THIRD_OPTION.getId(),
                    lootItem.getTile().getSceneLocation().getX(), lootItem.getTile().getSceneLocation().getY(), false);
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(lootItem.getTile().getItemLayer().getCanvasTilePoly().getBounds(),sleepDelay());
        }
    }

    private boolean lootableItem(TileItem item) {
        String itemName = client.getItemDefinition(item.getId()).getName().toLowerCase();

        int itemTotalValue = utils.getItemPrice(item.getId(), true) * item.getQuantity();

        return config.lootItems() &&
                ((config.lootNPCOnly() && item.getTile().getWorldLocation().equals(deathLocation)) ||
                        (!config.lootNPCOnly() && item.getTile().getWorldLocation().distanceTo(startLoc) < config.lootRadius())) &&
                ((config.lootValue() && itemTotalValue > config.minTotalValue()) ||
                        lootableItems.stream().anyMatch(itemName.toLowerCase()::contains) ||
                        config.buryBones() && itemName.contains("bones") ||
                        config.scatterAshes() && itemName.contains("ashes") ||
                        config.lootClueScrolls() && itemName.contains("scroll"));
    }

    private void buryBones() {
        List<WidgetItem> bones = inventory.getItems("bones");
        executorService.submit(() ->
        {
            iterating = true;
            for (WidgetItem bone : bones) {
                if (BONE_BLACKLIST.contains(bone.getId())) {
                    continue;
                }
                targetMenu = new LegacyMenuEntry("", "", bone.getId(), MenuAction.ITEM_FIRST_OPTION.getId(),
                        bone.getIndex(), WidgetInfo.INVENTORY.getId(), false);
                menu.setEntry(targetMenu);
                mouse.handleMouseClick(bone.getCanvasBounds());
                sleep(calc.getRandomIntBetweenRange(1200, 1400));
                log.debug("Burying Bones --- zNPCDeaggro");
                status = "Burying bones";
            }
            iterating = false;
        });
    }
    private void scatterAshes() {
        List<WidgetItem> ashes = inventory.getItems("ashes");
        executorService.submit(() ->
        {
            iterating = true;
            for (WidgetItem ashe : ashes) {
                if (BONE_BLACKLIST.contains(ashe.getId())) {
                    continue;
                }
                targetMenu = new LegacyMenuEntry("", "", ashe.getId(), MenuAction.ITEM_FIRST_OPTION.getId(),
                        ashe.getIndex(), WidgetInfo.INVENTORY.getId(), false);
                menu.setEntry(targetMenu);
                mouse.handleMouseClick(ashe.getCanvasBounds());
                sleep(calc.getRandomIntBetweenRange(800, 2200));
                log.debug("Scattering Ashes --- zNPCDeaggro");
                status = "Scattering ashes";
            }
            iterating = false;
        });
    }
    private void attackNPC(NPC npc) {
        targetMenu = new LegacyMenuEntry("", "", npc.getIndex(), MenuAction.NPC_SECOND_OPTION.getId(),
                0, 0, false);

        utils.doActionMsTime(targetMenu, currentNPC.getConvexHull().getBounds(), sleepDelay());
        timeout = 2 + tickDelay();
        log.debug("Attacking {} --- zNPCDeaggro", config.npcName());
        status = "Attacking " + config.npcName();
    }

    private NPC findSuitableNPC() {
        npcName = menuFight ? npcName : config.npcName();
        if (config.exactNpcOnly()) {
            NPC npcTarget = npc.findNearestNpcTargetingLocal(npcName, true);
            return (npcTarget != null) ? npcTarget :
                    npc.findNearestAttackableNpcWithin(startLoc, config.searchRadius(), npcName, true);
        } else {
            NPC npcTarget = npc.findNearestNpcTargetingLocal(npcName, false);
            return (npcTarget != null) ? npcTarget :
                    npc.findNearestAttackableNpcWithin(startLoc, config.searchRadius(), npcName, false);
        }

    }

    private combatType getEligibleAttackStyle() {

        int attackLevel = client.getRealSkillLevel(Skill.ATTACK);
        int strengthLevel = client.getRealSkillLevel(Skill.STRENGTH);
        int defenceLevel = client.getRealSkillLevel(Skill.DEFENCE);

        if ((attackLevel >= config.attackLvl() && strengthLevel >= config.strengthLvl() && defenceLevel >= config.defenceLvl())) {
            return config.continueType();
        }
        int highestDiff = config.attackLvl() - attackLevel;
        combatType type = combatType.ATTACK;

        if ((config.strengthLvl() - strengthLevel) > highestDiff ||
                (strengthLevel < config.strengthLvl() && strengthLevel < attackLevel && strengthLevel < defenceLevel)) {
            type = combatType.STRENGTH;
        }
        if ((config.defenceLvl() - defenceLevel) > highestDiff ||
                (defenceLevel < config.defenceLvl() && defenceLevel < attackLevel && defenceLevel < strengthLevel)) {
            type = combatType.DEFENCE;
        }
        return type;
    }

    private int getCombatStyle() {
        if (!config.combatLevels()) {
            return -1;
        }
        combatType attackStyle = getEligibleAttackStyle();
        if (attackStyle.equals(combatType.STOP)) {
            resetVals();
        } else {
            switch (client.getVarpValue(VarPlayer.ATTACK_STYLE.getId())) {
                case 0:
                    return (attackStyle.equals(combatType.ATTACK)) ? -1 : attackStyle.index;
                case 1:
                case 2:
                    return (attackStyle.equals(combatType.STRENGTH)) ? -1 : attackStyle.index;
                case 3:
                    return (attackStyle.equals(combatType.DEFENCE)) ? -1 : attackStyle.index;
            }
        }
        return -1;
    }

    private zNPCDeaggroState getState() {
        System.out.println("We are here 1");
        if (walkToNPC) {
            return zNPCDeaggroState.GOTONPC;
        }
        System.out.println("We are here 2");
        if (walkBack) {
            return zNPCDeaggroState.WANDERED;
        }
        System.out.println("We are here 3");
        if (goReset) {
            System.out.println("Time till reset: " + timeTillReset);
            return zNPCDeaggroState.RESETTING;
        }
        System.out.println("We are here 4");
        if (timeout > 0) {
            playerUtils.handleRun(20, 20);
            return zNPCDeaggroState.TIMEOUT;
        }
        System.out.println("We are here 5");
        if (iterating) {
            return zNPCDeaggroState.ITERATING;
        }
        System.out.println("We are here 6");
        if (playerUtils.isMoving(beforeLoc)) {
            log.debug("Moving --- zNPCDeaggro");
            status = "Moving";
            return zNPCDeaggroState.MOVING;
        }
        System.out.println("We are here 7");
        int combatStyle = getCombatStyle();
        if (config.combatLevels() && combatStyle != -1) {
            log.debug("Changing combat style to: {}", combatStyle);
            utils.sendGameMessage("Changing combat styles --- zNPCDeaggro");
            status = "Changing combat styles";
            utils.setCombatStyle(combatStyle);
            return zNPCDeaggroState.TIMEOUT;
        }
        System.out.println("We are here 8");
        if (config.lootAmmo() && !playerUtils.isItemEquipped(List.of(config.ammoID()))) {
            if (inventory.containsItem(config.ammoID())) {
                return zNPCDeaggroState.EQUIP_AMMO;
            } else if (config.stopAmmo()) {
                if (config.safeSpot() && startLoc.distanceTo(player.getWorldLocation()) > (config.safeSpotRadius())) {
                    return zNPCDeaggroState.RETURN_SAFE_SPOT;
                }
                return (config.logout()) ? zNPCDeaggroState.LOG_OUT : zNPCDeaggroState.MISSING_ITEMS;
            }
        }
        System.out.println("We are here 9");
        if (config.stopFood() && !inventory.containsItem(config.foodID())) {
            if (config.safeSpot() && startLoc.distanceTo(player.getWorldLocation()) > (config.safeSpotRadius())) {
                return zNPCDeaggroState.RETURN_SAFE_SPOT;
            }
            return (config.logout()) ? zNPCDeaggroState.LOG_OUT : zNPCDeaggroState.MISSING_ITEMS;
        }
        System.out.println("We are here 10");
        if (config.forceLoot() && config.lootItems() && !inventory.isFull() && !loot.isEmpty()) {
            if (newLoot != null) {
                Duration duration = Duration.between(newLoot, Instant.now());
                nextItemLootTime = (nextItemLootTime == 0) ? calc.getRandomIntBetweenRange(10, 50) : nextItemLootTime;
                if (duration.toSeconds() > nextItemLootTime) {
                    nextItemLootTime = calc.getRandomIntBetweenRange(10, 50);
                    log.debug("Forced Looting --- zNPCDeaggro");
                    return zNPCDeaggroState.FORCE_LOOT;
                }
            }
        }
        System.out.println("We are here 11");
        if (config.safeSpot() && npc.findNearestNpcTargetingLocal("", false) != null &&
                startLoc.distanceTo(player.getWorldLocation()) > (config.safeSpotRadius())) {
            log.debug("Returning to safespot --- zNPCDeaggro");
            status = "Returning to safespot";
            return zNPCDeaggroState.RETURN_SAFE_SPOT;
        }
        System.out.println("We are here 12");
        if (player.getInteracting() != null) {
            currentNPC = (NPC) player.getInteracting();
            if (currentNPC != null && currentNPC.getHealthRatio() == -1) //NPC has noHealthBar, NPC ran away and we are stuck with a target we can't attack
            {
                log.debug("Finding New NPC --- zNPCDeaggro");
                System.out.println("Finding New NPC --- zNPCDeaggro");
                currentNPC = findSuitableNPC();
                if (currentNPC != null) {
                    return zNPCDeaggroState.ATTACK_NPC;
                } else {
                    log.debug("Clicking randomly to try get unstuck");
                    targetMenu = null;
                    mouse.clickRandomPointCenter(-100, 100);
                    return zNPCDeaggroState.TIMEOUT;
                }
            }
        }
        System.out.println("We are here 13");
        npcName = menuFight ? npcName : config.npcName();
        if (config.exactNpcOnly()) {
            currentNPC = npc.findNearestNpcTargetingLocal(npcName, true);
        } else {
            currentNPC = npc.findNearestNpcTargetingLocal(npcName, false);
        }
        System.out.println("We are here 14");
        if (config.buryBones() && inventory.containsItem("bones") && (inventory.isFull() || config.buryOne())) {
            return zNPCDeaggroState.BURY_BONES;
        }
        System.out.println("We are here 15");
        if (config.scatterAshes() && inventory.containsItem("ashes") && (inventory.isFull() || config.buryOne())) {
            return zNPCDeaggroState.SCATTER_ASHES;
        }
        System.out.println("We are here 16");
        if (config.lootItems()){
            System.out.println("lootItems return = true");
        }
        System.out.println("We are here 17");
        if (!inventory.isFull() && !loot.isEmpty()){
            System.out.println("lootItems met condition");
        }
        System.out.println("We are here 18");
        if (config.lootItems() && !inventory.isFull() && !loot.isEmpty()) {
            status = "Looting items";
            log.debug("Looting Items --- zNPCDeaggro");
            System.out.println("Looting Items --- zNPCDeaggro");
            return zNPCDeaggroState.LOOT_ITEMS;
        }
        System.out.println("We are here 19");
        if (config.lootAmmo() && (!inventory.isFull() || inventory.containsItem(config.ammoID()))) {
            if (ammoLoot.isEmpty() || nextAmmoLootTime == 0) {
                nextAmmoLootTime = calc.getRandomIntBetweenRange(config.minAmmoLootTime(),
                        (config.minAmmoLootTime() + config.randAmmoLootTime()));
            }
            if (!ammoLoot.isEmpty()) {
                if (lootTimer != null) {
                    Duration duration = Duration.between(lootTimer, Instant.now());
                    if (duration.toSeconds() > nextAmmoLootTime) {
                        return zNPCDeaggroState.LOOT_AMMO;
                    }
                } else {
                    lootTimer = Instant.now();
                }
            }
        }
        System.out.println("We are here 20");
        if (player.getInteracting() != null) {
            currentNPC = (NPC) player.getInteracting();
            if (currentNPC != null && currentNPC.getHealthRatio() == -1) {
                log.debug("interacting and npc has not health bar. Finding new NPC");
                currentNPC = findSuitableNPC();
                if (currentNPC != null) {
                    return zNPCDeaggroState.ATTACK_NPC;
                } else {
                    log.debug("Clicking randomly to try get unstuck");
                    targetMenu = null;
                    mouse.clickRandomPointCenter(-100, 100);
                    return zNPCDeaggroState.TIMEOUT;
                }
            }
        }
        System.out.println("We are here 21");
        if (chinBreakHandler.shouldBreak(this)) {
            log.debug("Taking break --- zNPCDeaggro");
            utils.sendGameMessage("Taking break --- zNPCDeaggro");
            status = "Taking a break";
            return zNPCDeaggroState.HANDLE_BREAK;
        }
        System.out.println("We are here 22");
        if (currentNPC != null) {
            int chance = calc.getRandomIntBetweenRange(0, 1);
            log.debug("Chance result: {}", chance);
            return (chance == 0) ? zNPCDeaggroState.ATTACK_NPC : zNPCDeaggroState.WAIT_COMBAT;
        }
        System.out.println("We are here 23");
        if (waiting) {
            return zNPCDeaggroState.CHECKTIME;
        }
        System.out.println("We are here 24");
        return zNPCDeaggroState.IN_COMBAT;
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if (!startBot || chinBreakHandler.isBreakActive(this)) {
            return;
        }
        player = client.getLocalPlayer();
        if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN) {
            if (!client.isResized()) {
                utils.sendGameMessage("Client must be set to resizable.");
                startBot = false;
                return;
            }
            state = getState();
            switch (state) {
                case GOTONPC:
                    if (player.getWorldLocation().distanceTo(customLocation) > 0 && !player.isMoving()) {
                        System.out.println("Walking to NPC");
                        walk.sceneWalk(customLocation, 0, sleepDelay());
                        status = "Walking to NPC";
                        timeout = tickDelay();
                        break;
                    } else if (player.getWorldLocation().distanceTo(customLocation) > 0 && player.isMoving()) {
                        System.out.println("Player Moving");
                        log.info("Player is still moving so we wait until we reach NPC");
                        timeout = tickDelay();
                        return;
                    } else {
                        System.out.println("Waiting");
                        walkToNPC = false;
                        waiting = true;
                        botTimer = Instant.now();
                    }
                case CHECKTIME:
//                    System.out.println("CHECKTIME");
//                    System.out.println("Time till reset: " + timeTillReset);
                    Duration duration = Duration.between(botTimer, Instant.now());
                    timeRan = (int) duration.getSeconds();
                    status = "Waiting until time runs out";
                    timeTillReset = (resetTime) - timeRan;
                    if (timeRan > resetTime + randomVariationInTime) {
                        if(isInCombat()) {
                            System.out.println(timeTillReset);
                            timeRun -= 15;
                            timeout = tickDelay();
                            return;
                        } else {
//                            System.out.println("Time till reset: " + timeTillReset);
//                            System.out.println("Random Variation In Time: " + randomVariationInTime);
                            waiting = false;
                            goReset = true;
                            randomVariationInTime = calc.getRandomIntBetweenRange(0, config.resetTimeRandomization());
                            return;
                        }
                    } else {
                        checkIfLeftMainTile();
                        timeout = tickDelay();
                        return;
                    }
                case WANDERED:
                    walk.sceneWalk(customLocation, 0, sleepDelay());
                    status = "Walking to NPC";
                    walkBack = false;
                    timeout = tickDelay() + 3;
                    return;
                case RESETTING:
                    if (player.getWorldLocation().distanceTo(resetLocation) > 4 && !player.isMoving()) {
                        utils.sendGameMessage("Resetting Aggression --- zNPCDeaggro");
                        walk.sceneWalk(resetLocation, 0, sleepDelay());
                        timeout = tickDelay();
                        status = "Resetting Aggression";
                        return;
                    } else if (player.getWorldLocation().distanceTo(resetLocation) > 4 && player.isMoving()) {
                        log.debug("Player is still moving so we wait until we reach");
                        timeout = tickDelay();
                        return;
                    } else {
                        walkToNPC = true;
                        goReset = false;
                        return;
                    }
                case TIMEOUT:
                    timeout--;
                    break;
                case ITERATING:
                    break;
                case ATTACK_NPC:
                    timeout = tickDelay();
                    sleep(600,1200);
                    attackNPC(currentNPC);
                    break;
                case BURY_BONES:
                    buryBones();
                    timeout = tickDelay();
                    break;
                case SCATTER_ASHES:
                    scatterAshes();
                    timeout = tickDelay();
                    break;
                case EQUIP_AMMO:
                    WidgetItem ammoItem = inventory.getWidgetItem(config.ammoID());
                    if (ammoItem != null) {
                        targetMenu = new LegacyMenuEntry("", "", ammoItem.getId(), MenuAction.ITEM_SECOND_OPTION.getId(), ammoItem.getIndex(),
                                WidgetInfo.INVENTORY.getId(), false);
                        menu.setEntry(targetMenu);
                        mouse.delayMouseClick(ammoItem.getCanvasBounds(), sleepDelay());
                    }
                    break;
                case FORCE_LOOT:
                case LOOT_ITEMS:
                    lootItem(loot);
                    timeout = tickDelay();
                    sleep(600,1200);
                    walk.sceneWalk(customLocation, 0, sleepDelay());
                    break;
                case LOOT_AMMO:
                    lootItem(ammoLoot);
                    break;
                case WAIT_COMBAT:
                    if (config.safeSpot()) {
                        new TimeoutUntil(
                                () -> startLoc.distanceTo(player.getWorldLocation()) > (config.safeSpotRadius()),
                                () -> playerUtils.isMoving(),
                                3);
                    } else {
                        new TimeoutUntil(
                                () -> playerUtils.isAnimating(),
                                () -> playerUtils.isMoving(),
                                3);
                    }
                    break;
                case IN_COMBAT:
                    timeout = tickDelay();
                    break;
                case HANDLE_BREAK:
                    chinBreakHandler.startBreak(this);
                    timeout = 10;
                    break;
                case RETURN_SAFE_SPOT:
                    if (!player.isMoving() && !player.getWorldLocation().equals(customLocation) && !player.getWorldLocation().equals(startLoc))
                        walk.sceneWalk(startLoc, config.safeSpotRadius(), sleepDelay());
                    timeout = 2 + tickDelay();
                    break;
                case LOG_OUT:
                    if (player.getInteracting() == null) {
                        interfaceUtils.logout();
                    } else {
                        timeout = 5;
                    }
                    shutDown();
                    break;
            }
            beforeLoc = player.getLocalLocation();
            }
        }

    @Subscribe
    private void onActorDeath(ActorDeath event) {
        if (!startBot) {
            return;
        }
        if (event.getActor() == currentNPC) {
            deathLocation = event.getActor().getWorldLocation();
            log.debug("Our npc died, updating deathLocation: {}", deathLocation.toString());
            killCount++;
        }
    }

    private boolean isInCombat() {
        targetNPC = npc.findNearestNpcTargetingLocal(NPCToAttack, true);
        if (targetNPC == null) {
            return false;
        } else {
            log.info("The NPC you are still fighting is " + targetNPC.getName());
            return true;
        }
    }

    @Subscribe
    private void onItemSpawned(ItemSpawned event) {
        if (!startBot) {
            return;
        }

        if (lootableItem(event.getItem())) {
            log.debug("Adding loot item: {}", client.getItemDefinition(event.getItem().getId()).getName());
            if (loot.isEmpty()) {
                log.debug("Starting force loot timer");
                newLoot = Instant.now();
            }
            loot.add(event.getItem());
        }
        if (config.lootAmmo() && event.getItem().getId() == config.ammoID()) {
            for (TileItem item : ammoLoot) {
                if (item.getTile() == event.getTile()) //Don't add if we already have ammo at that tile, as they are stackable
                {
                    return;
                }
            }
            log.debug("adding ammo loot item: {}", event.getItem().getId());
            ammoLoot.add(event.getItem());
        }
    }

    @Subscribe
    private void onItemDespawned(ItemDespawned event) {
        if (!startBot) {
            return;
        }
        loot.remove(event.getItem());
        if (loot.isEmpty()) {
            newLoot = null;
        }
        if (ammoLoot.isEmpty()) {
            lootTimer = null;
        }
        ammoLoot.remove(event.getItem());
    }

    private void setLocations() {
        customLocation = convertStringToWorldPoint(config.customNPCLocation());
        resetLocation = convertStringToWorldPoint(config.customResetLocation());
        //playerHopCheck = convertStringToWorldPoint(config.customNPCLocation());
        System.out.println(customLocation + "hello + " + resetLocation);
    }

    private void checkIfLeftMainTile() {
        int plX = player.getWorldLocation().getX();
        int plY = player.getWorldLocation().getY();
        int clX = customLocation.getX();
        int clY = customLocation.getY();
        if (plX == clX && plY == clY) {
            //System.out.println(plX + " "+ plY + " +is the world location and custom location is  " + clX + " " + clY);
            //System.out.println("We are still on the correct tile");
            checkForSpec();
            return;
        } else {
            utils.sendGameMessage("Detected that player has wandered off, returning to main");
            //System.out.println(plX + " "+ plY + " +is the world location and custom location is  " + clX + " " + clY);
            //System.out.println("PLayer is not in the correct spot");
            walkBack = true;
            return;
        }
    }


    private WorldPoint convertStringToWorldPoint(String location) {
        int[] userString = utils.stringToIntArray(location);
        if (userString.length != 3) {
            utils.sendGameMessage("You did not enter WorldPoints in correct format");
            return null;
        } else {
            return new WorldPoint(userString[0], userString[1], userString[2]);
        }
    }

    private void checkForSpec() {
        if (!specAllowed) {
            return;
        }
        int spec_percent = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);
        if (spec_percent >= specCost * 10) {
            boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

            if (spec_enabled) {
                return;
            }
            Widget specialOrb = client.getWidget(160, 30);
            if (specialOrb != null) {
                targetMenu = new LegacyMenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuAction.CC_OP.getId(), -1, 38862884, false);
                utils.doInvokeMsTime(targetMenu, sleepDelay());

            } else {
                utils.sendGameMessage("Spec orb is null");
            }

        } else {
            return;
        }
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted event) {
        if (event.getCommand().equalsIgnoreCase("checkAg")) utils.sendGameMessage(String.valueOf(isInCombat()));

    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (!startBot || event.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        log.debug("GameState changed to logged in, clearing loot and npc");
        loot.clear();
        ammoLoot.clear();
        currentNPC = null;
        state = zNPCDeaggroState.TIMEOUT;
        timeout = 2;
    }

    private void addMenuEntry(MenuEntryAdded event, String option) { //TODO: Update to new menu entry
        client.createMenuEntry(-1).setOption(option)
                .setTarget(event.getTarget())
                .setIdentifier(0)
                .setParam1(0)
                .setParam1(0)
                .setType(MenuAction.RUNELITE);
//        MenuEntry entry = new MenuEntry();
//        entry.setOption(option);
//        entry.setTarget(event.getTarget());
//        entry.setOpcode(MenuAction.RUNELITE.getId());
//        entries.add(0, entry);
//        client.setMenuEntries(entries.toArray(new MenuEntry[0]));
    }
}
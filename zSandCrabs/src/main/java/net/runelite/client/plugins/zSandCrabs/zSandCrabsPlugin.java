package net.runelite.client.plugins.zSandCrabs;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Rectangle;
///api
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuAction;
import net.runelite.api.GameObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;

import java.awt.Robot;

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


import org.pf4j.Extension;

import javax.inject.Inject;
import java.sql.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static net.runelite.api.MenuAction.*;


@Extension
@PluginDependency(iUtils.class)
@PluginDescriptor(
	name = "zSandCrabs",
	enabledByDefault = false,
	description = "Kills sand crabs on Hosidius beach.",
	tags = {"z, sand, crabs, zackaery"}

)
@Slf4j
public class zSandCrabsPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private zSandCrabsConfiguration config;

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
	private zSandCrabsOverlay overlay;

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
	PluginManager pluginManager;

	Instant botTimer;
	LegacyMenuEntry targetMenu;
	WorldPoint skillLocation;
	NPC targetNPC;
	Player player;
	boolean walkBack;


	int timeout;


	int timedOut = 0;
	long sleepLength;
	boolean startChaosAltar;
	boolean plugStarted = false;
	int timeRun;
	String NPCToAttack;
	int randomVariationInTime;

	Instant totalTimer;
	WorldPoint customLocation;
	WorldPoint resetLocation;


	boolean startBot;
	boolean walkToCrab;

	int timeRan;
	int specCost;
	int timeTillReset;
	boolean waiting;
	boolean specAllowed;
	boolean goReset;
	String status;

	int randVar;
	int resetTime;

	@Provides
	zSandCrabsConfiguration provideConfig(ConfigManager configManager) {
		return configManager.getConfig(zSandCrabsConfiguration.class);
	}

	@Override
	protected void startUp()
	{


	}


	@Override
	protected void shutDown() {

		log.info("zSandCrabs --- Has been stopped.");
		overlayManager.remove(overlay);

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

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked event) {

		if (!event.getGroup().equalsIgnoreCase("zSandCrabs")) {
			return;
		}

		if (event.getKey().equals("startButton")){
			if(!plugStarted){
				log.info("zSandCrabs --- Has been started.");
				setLocations();
				botTimer = Instant.now();
				totalTimer = Instant.now();
				randVar = calc.getRandomIntBetweenRange(-5, 6);
				walkToCrab = true;
				startBot = true;
				walkBack = false;
				waiting = false;
				specAllowed = config.enableSpec();
				specCost = config.specCost();
				goReset = false;
				resetTime= config.resetTime();
				NPCToAttack = config.npcName();
				randomVariationInTime =
						calc.getRandomIntBetweenRange(0,config.resetTimeRandomization());
				plugStarted = true;
				overlayManager.add(overlay);

			}else if(plugStarted){
				utils.sendGameMessage("zSandCrabs --- Stopping.");
				overlayManager.remove(overlay);
				startBot = false;
				plugStarted = false;
				//timeRuns = config.customTime();
			}else{
				utils.sendGameMessage("zSandCrabs --- Encountered unknown error");
			}
		}
	}


	@Subscribe
	private void onGameTick(GameTick tick) {
		if (!startBot) {
			return;
		}
		player = client.getLocalPlayer();
		if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN){
			if (timeout>0){
				timeout--;
				return;
			}
			String state = getState();
			switch (state){
			case "GOTOCRAB":
				if (player.getWorldLocation().distanceTo(customLocation) > 0 && !player.isMoving()) {
					walk.sceneWalk(customLocation,0,sleepDelay());
					status = "Walking to --- Starter Tile Location.";
					timeout = tickDelay();
					break;
				}else if(player.getWorldLocation().distanceTo(customLocation) > 0 && player.isMoving()){
					log.info("Player is currently moving, waiting until player has reached Starter Tile Location.");
					timeout = tickDelay();
					return;
				} else{
					walkToCrab = false;
					waiting = true;
					botTimer = Instant.now();

				}
				case "CHECKTIME":
					Duration duration = Duration.between(botTimer, Instant.now());
					timeRan = (int) duration.getSeconds();
					status = "Waiting for Reset Timer to end.";
					timeTillReset = (resetTime) - timeRan;
					if (timeRan > resetTime + randomVariationInTime) {
						if(isInCombat()){
							timeRun -= 15;
							timeout = tickDelay();
							return;
						}else{
							waiting = false;
							goReset = true;
							randomVariationInTime = calc.getRandomIntBetweenRange(0,config.resetTimeRandomization());
							return;
						}



					}else{
						checkIfLeftMainTile();
						timeout = tickDelay();
						return;
					}

				case "WANDERED":
					walk.sceneWalk(customLocation,0,sleepDelay());
					status = "Walking back to Sand Crabs. ";
					walkBack = false;
					timeout = tickDelay()+ 3;
					return;

				case "RESETTING":
					if (player.getWorldLocation().distanceTo(resetLocation) > 4 && !player.isMoving()) {
						utils.sendGameMessage("Walking to --- Reset Tile Location");
						walk.sceneWalk(resetLocation,0,sleepDelay());
						timeout = tickDelay();
						status = "Resetting";
					}else if(player.getWorldLocation().distanceTo(resetLocation) > 4 && player.isMoving()){
						log.debug("Player is currently moving, , waiting until player has reached Reset Tile Location.");
						timeout = tickDelay();
					} else{
						walkToCrab = true;
						goReset = false;
					}


			}


		}

	}

	private String getState() {
		if (walkToCrab) {
			return "GOTOCRAB";
		}
		if(walkBack){
			return "WANDERED";
		}
		if (waiting) {
			return "CHECKTIME";
		}
		if (goReset) {
			return "RESETTING";
		} else {
			return "IDLE";
		}
	}

	private boolean isInCombat(){
		targetNPC = npc.findNearestNpcTargetingLocal(NPCToAttack,true);
		if(targetNPC == null){
			return false;
		}else{
			log.info("The NPC you are still fighting is " + targetNPC.getName());
			return  true;
		}
	}

	private void setLocations() {
	customLocation = converStringToWorldPoint(config.customNPCLocation());

	resetLocation = converStringToWorldPoint(config.customResetLocation());
	System.out.println(customLocation + "hello + " + resetLocation);


	}

	private void checkIfLeftMainTile(){
		int plX = player.getWorldLocation().getX();
		int plY = player.getWorldLocation().getY();
		int clX = customLocation.getX();
		int clY = customLocation.getY();

		if(plX == clX && plY==clY){
			//System.out.println(plX + " "+ plY + " +is the world location and custom location is  " + clX + " " + clY);
			System.out.println("We are still on the correct tile");
			checkForSpec();
		}else{
			utils.sendGameMessage("Walking back to Sand Crabs.");
			//System.out.println(plX + " "+ plY + " +is the world location and custom location is  " + clX + " " + clY);
			//System.out.println("PLayer is not in the correct spot");
			walkBack = true;
		}
	}




	private WorldPoint converStringToWorldPoint(String location){
		int [] userString = utils.stringToIntArray(location);
		if(userString.length != 3){
			utils.sendGameMessage("You did not enter WorldPoints in correct format");
			return null;
		}else{
			return new WorldPoint(userString[0], userString[1], userString[2]);
		}
	}

	private void checkForSpec(){
		if(!specAllowed){
			return;
		}
		int spec_percent = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);
		if(spec_percent>=specCost*10){
			boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

			if (spec_enabled)
			{
				return;
			}
			Widget specialOrb = client.getWidget(160, 30);
			if(specialOrb != null){
				targetMenu = new LegacyMenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuAction.CC_OP.getId(), -1, 38862884, false);
				utils.doInvokeMsTime(targetMenu,sleepDelay());

			}else{
				utils.sendGameMessage("Spec orb is null");
			}
		}
	}



	private Point getRandomNullPoint()
	{
		if(client.getWidget(161,34)!=null){

			Rectangle nullArea = Objects.requireNonNull(client.getWidget(161, 34)).getBounds();
			return new Point ((int)nullArea.getX()+calc.getRandomIntBetweenRange(0,nullArea.width), (int)nullArea.getY()+calc.getRandomIntBetweenRange(0,nullArea.height));
		}

		return new Point(client.getCanvasWidth()-calc.getRandomIntBetweenRange(0,2),client.getCanvasHeight()-calc.getRandomIntBetweenRange(0,2));
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted event){
		if (event.getCommand().equalsIgnoreCase("checkAg"))  utils.sendGameMessage(String.valueOf(isInCombat()));

	}



}
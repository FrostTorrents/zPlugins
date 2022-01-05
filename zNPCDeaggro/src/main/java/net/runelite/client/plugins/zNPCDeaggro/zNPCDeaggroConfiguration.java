/*
 * Copyright (c) 2018, SomeoneWithAnInternetConnection
 * Copyright (c) 2018, oplosthee <https://github.com/oplosthee>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.zNPCDeaggro;

import net.runelite.client.config.*;
import net.runelite.client.config.ConfigTitle;


@ConfigGroup("zNPCDeaggro")
public interface zNPCDeaggroConfiguration extends Config {

    @ConfigSection(
            keyName = "delayConfig",
            name = "Sleep Delay Configuration",
            description = "Configure how the bot handles sleep delays",
            closedByDefault = true,
            position = 0
    )
    String delayConfig = "delayConfig";

    @Range(
            min = 0,
            max = 550
    )
    @ConfigItem(
            keyName = "sleepMin",
            name = "Sleep Min",
            description = "",
            position = 1,
            section = "delayConfig"
    )
    default int sleepMin() {
        return 60;
    }

    @Range(
            min = 0,
            max = 550
    )
    @ConfigItem(
            keyName = "sleepMax",
            name = "Sleep Max",
            description = "",
            position = 2,
            section = "delayConfig"
    )
    default int sleepMax() {
        return 350;
    }

    @Range(
            min = 0,
            max = 550
    )
    @ConfigItem(
            keyName = "sleepTarget",
            name = "Sleep Target",
            description = "",
            position = 3,
            section = "delayConfig"
    )
    default int sleepTarget() {
        return 100;
    }

    @Range(
            min = 0,
            max = 550
    )
    @ConfigItem(
            keyName = "sleepDeviation",
            name = "Sleep Deviation",
            description = "",
            position = 4,
            section = "delayConfig"
    )
    default int sleepDeviation() {
        return 10;
    }

    @ConfigItem(
            keyName = "sleepWeightedDistribution",
            name = "Sleep Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 5,
            section = "delayConfig"
    )
    default boolean sleepWeightedDistribution() {
        return false;
    }

    @ConfigSection(
            keyName = "delayTickConfig",
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            closedByDefault = true,
            position = 10
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 11,
            section = "delayTickConfig"
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            min = 0,
            max = 30
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 12,
            section = "delayTickConfig"
    )
    default int tickDelayMax() {
        return 3;
    }

    @Range(
            min = 0,
            max = 30
    )
    @ConfigItem(
            keyName = "tickDelayTarget",
            name = "Game Tick Target",
            description = "",
            position = 13,
            section = "delayTickConfig"
    )
    default int tickDelayTarget() {
        return 2;
    }

    @Range(
            min = 0,
            max = 30
    )
    @ConfigItem(
            keyName = "tickDelayDeviation",
            name = "Game Tick Deviation",
            description = "",
            position = 14,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 15,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

    @ConfigTitle(
            keyName = "instructionsTitle",
            name = "Setup and Instructions",
            description = "",
            position = 16
    )
    String instructionsTitle = "instructionsTitle";

    @ConfigItem(
            keyName = "instruction",
            name = "",
            description = "Instructions. Don't enter anything into this field",
            position = 20,
            title = "instructionsTitle"
    )
    default String instruction() {
        return "Auto fights NPC's & Resets Aggro! Enable iQuickEater Plugin for eating.";
    }

    @ConfigItem(
            keyName = "customNPCLocation",
            name = "Custom NPC Location",
            description = "Enter the location you want to attack NPC --- (x,y,z)",
            position = 31,
            title = "Custom Locations"
    )
    default String customNPCLocation() {
        return "3657,3875,0";
    }

    @ConfigItem(
            keyName = "customResetLocation",
            name = "Custom Reset Location",
            description = "Enter the location you want to reset aggression --- (x,y,z)",
            position = 32,
            title = "Custom Locations"
    )
    default String customResetLocation() {
        return "3669,3841,0";
    }

    @ConfigItem(
            keyName = "customHopLocation",
            name = "Custom Hop Location",
            description = "Enter the location you want to reset aggression --- (x,y,z)",
            position = 33,
            title = "Custom Locations",
            hidden = true,
            unhide = "worldHopping"
    )
    default String customHopLocation() {
        return "3669,3872,0";
    }

    @ConfigItem(
            keyName = "worldHopping",
            name = "Enable World Hopping (Disabled)",
            description = "Enable World Hopping",
            position = 34,
            title = "Custom Locations"
    )
    default boolean worldHopping() {
        return false;
    }

    @ConfigSection(
            keyName = "generalTitle",
            name = "General Config",
            description = "",
            position = 35
    )
    String generalTitle = "generalTitle";

    @ConfigItem(
            keyName = "exactNpcOnly",
            name = "Exact NPC only mode",
            description = "Exact NPC only mode, will fight exact NPC names only",
            position = 36,
            title = "Custom Locations",
            section = "generalTitle"
    )
    default boolean exactNpcOnly() {
        return false;
    }

    @ConfigItem(
            keyName = "npcName",
            name = "NPC Name",
            description = "Name of NPC. Will attack any NPC containing given name.",
            position = 37,
            hide = "dropInventory",
            section = "generalTitle"
    )
    default String npcName() {
        return "Ammonite Crab";
    }

    @Range(
            min = 1,
            max = 64
    )
    @ConfigItem(
            keyName = "searchRadius",
            name = "Search radius NPC",
            description = "The distance (in tiles) to search for target NPC. Center search point is set when you click start.",
            position = 38,
            hide = "dropInventory",
            section = "generalTitle"
    )
    default int searchRadius() {
        return 20;
    }

    @ConfigItem(
            keyName = "safeSpot",
            name = "Safe spot",
            description = "Safe spot will force your character to always return to the tile you started the plugin on",
            position = 39,
            hide = "dropInventory",
            section = "generalTitle"
    )
    default boolean safeSpot() {
        return false;
    }

    @ConfigItem(
            keyName = "safeSpotRadius",
            name = "Safe spot radius",
            description = "Radius of the safe spot to return to. 0 will always return to the same tile, 1 will return to a 1 tile radius of safespot",
            position = 40,
            hidden = true,
            unhide = "safeSpot",
            section = "generalTitle"
    )
    default int safeSpotRadius() {
        return 1;
    }

    @ConfigItem(
            keyName = "resetTime",
            name = "Reset Time",
            description = "How long to wait before resetting",
            position = 41,
            section = "generalTitle"
    )
    default int resetTime() {
        return 620;
    }

    @ConfigItem(
            keyName = "resetTimeRandomization",
            name = "Reset Time Randomizer",
            description = "Random deviation time in seconds.",
            position = 42,
            section = "generalTitle"
    )
    default int resetTimeRandomization() {
        return 5;
    }

    @ConfigSection(
            keyName = "combatTitle",
            name = "Combat Settings",
            description = "",
            position = 43
    )
    String combatTitle = "combatTitle";

    @ConfigItem(
            keyName = "combatLevels",
            name = "Combat Levels",
            description = "Enable to set levels for the bot to reach and change between",
            position = 45,
            section = "combatTitle"
    )
    default boolean combatLevels() {
        return false;
    }

    @ConfigItem(
            keyName = "attackLvl",
            name = "Attack level",
            description = "Attack level to level to",
            position = 46,
            hidden = true,
            unhide = "combatLevels",
            section = "combatTitle"
    )
    default int attackLvl() {
        return 60;
    }

    @ConfigItem(
            keyName = "strengthLvl",
            name = "Strength level",
            description = "Strength level to level to",
            position = 47,
            hidden = true,
            unhide = "combatLevels",
            section = "combatTitle"
    )
    default int strengthLvl() {
        return 60;
    }

    @ConfigItem(
            keyName = "defenceLvl",
            name = "Defence level",
            description = "Defence level to level to",
            position = 48,
            hidden = true,
            unhide = "combatLevels",
            section = "combatTitle"
    )
    default int defenceLvl() {
        return 60;
    }

    @ConfigItem(
            keyName = "continueType",
            name = "Continue after levels",
            description = "Select action ",
            position = 49,
            hidden = true,
            unhide = "combatLevels",
            section = "combatTitle"
    )
    default combatType continueType() {
        return combatType.STOP;
    }

    @ConfigItem(
            keyName = "enableSpec",
            name = "Enable Using Special Attack",
            description = "Enable to turn on using weapon special attack",
            position = 50,
            section = "combatTitle"
    )
    default boolean enableSpec() {
        return false;
    }

    @ConfigItem(
            keyName = "specCost",
            name = "Spec Cost",
            description = "Enter the amount of Spec energy it uses",
            position = 51,
            hidden = true,
            unhide = "enableSpec",
            section = "combatTitle"
    )
    default int specCost() {
        return 50;
    }


    @ConfigSection(
            keyName = "ammoTitle",
            name = "Ammo Settings",
            description = "",
            closedByDefault = true,
            position = 60
    )
    String ammoTitle = "ammoTitle";

    @ConfigItem(
            keyName = "lootAmmo",
            name = "Loot Ammo",
            description = "Enable to loot ammo",
            position = 61,
            section = "ammoTitle"
    )
    default boolean lootAmmo() {
        return false;
    }

    @ConfigItem(
            keyName = "ammoID",
            name = "Ammo ID",
            description = "Enable to stop when out of Ammo",
            position = 62,
            hidden = true,
            unhide = "lootAmmo",
            section = "ammoTitle"
    )
    default int ammoID() {
        return 809;
    }

    @ConfigItem(
            keyName = "minAmmoLootTime",
            name = "Ammo loot min wait (seconds)",
            description = "Minimum time (in seconds) to wait before collecting ammo",
            position = 63,
            hidden = true,
            unhide = "lootAmmo",
            section = "ammoTitle"
    )
    default int minAmmoLootTime() {
        return 20;
    }

    @ConfigItem(
            keyName = "randAmmoLootTime",
            name = "Additional random ammo wait time",
            description = "Maximum random value that will be added to the minimum ammo wait time",
            position = 64,
            hidden = true,
            unhide = "lootAmmo",
            section = "ammoTitle"
    )
    default int randAmmoLootTime() {
        return 30;
    }

    @ConfigSection(
            keyName = "lootTitle",
            name = "Loot Settings",
            description = "",
            closedByDefault = true,
            position = 65
    )
    String lootTitle = "lootTitle";

    @ConfigItem(
            keyName = "lootItems",
            name = "Loot Items",
            description = "Enable to loot items",
            position = 66,
            section = "lootTitle"
    )
    default boolean lootItems() {
        return false;
    }

    @Range(
            min = 1,
            max = 64
    )
    @ConfigItem(
            keyName = "lootRadius",
            name = "Loot radius",
            description = "The distance (in tiles) to search for target loot. Center search point is set when you click start.",
            position = 67,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default int lootRadius() {
        return 20;
    }

    @ConfigItem(
            keyName = "lootValue",
            name = "Loot all items above a set value",
            description = "Enable to loot all items above the set GP value. Uses actively traded Wiki price.",
            position = 68,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default boolean lootValue() {
        return false;
    }

    @ConfigItem(
            keyName = "minTotalValue",
            name = "",
            description = "The minimum value for loot, including total stack value. Uses Wiki actively traded price.",
            position = 69,
            hidden = true,
            unhide = "lootValue",
            section = "lootTitle"
    )
    default int minTotalValue() {
        return 500;
    }

    @ConfigItem(
            keyName = "lootItemNames",
            name = "Item Names to loot (separate with comma)",
            description = "Provide part or all of the item name to loot. Separate each item with a comma. Not case sensitive.",
            position = 70,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default String lootItemNames() {
        return "rune,head,seed,herb,root,grimy,key,cut";
    }

    @ConfigItem(
            keyName = "lootClueScrolls",
            name = "Pick-up Clue Scrolls",
            description = "Enable to loot CLue Scrolls",
            position = 71,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default boolean lootClueScrolls() {
        return false;
    }

    @ConfigItem(
            keyName = "buryBones",
            name = "Loot and Bury Bones",
            description = "Enable to loot and bury Bones",
            position = 72,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default boolean buryBones() {
        return false;
    }

    @ConfigItem(
            keyName = "scatterAshes",
            name = "Loot and Scatter Ashes",
            description = "Enable to loot and scatter Ashes",
            position = 73,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default boolean scatterAshes() {
        return false;
    }

    @ConfigItem(
            keyName = "buryOne",
            name = "Get 1 Bury 1",
            description = "Enable to bury bones as they are picked up. Disable to bury bones once inventory is full.",
            position = 74,
            hidden = true,
            unhide = "buryBones",
            section = "lootTitle"
    )
    default boolean buryOne() {
        return false;
    }

    @ConfigItem(
            keyName = "lootNPCOnly",
            name = "Loot your NPC Only",
            description = "Enable to only loot NPC's you have killed",
            position = 75,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"

    )
    default boolean lootNPCOnly() {
        return true;
    }

    @ConfigItem(
            keyName = "forceLoot",
            name = "Force loot",
            description = "Enable to force loot if loot has been on the ground for a while",
            position = 76,
            hidden = true,
            unhide = "lootItems",
            section = "lootTitle"
    )
    default boolean forceLoot() {
        return true;
    }

    @ConfigItem(
            keyName = "stopAmmo",
            name = "Stop when out of ammo",
            description = "Enable to stop when out of ammo",
            position = 50,
            section = "ammoTitle"
    )
    default boolean stopAmmo() {
        return false;
    }

    @ConfigItem(
            keyName = "stopFood",
            name = "Stop if out of food",
            description = "Enable to stop when out of food",
            position = 51,
            section = "combatTitle"
    )
    default boolean stopFood() {
        return false;
    }

    @ConfigItem(
            keyName = "foodID",
            name = "Food ID",
            description = "Enter the ID of your food so bot knows when to stop",
            position = 52,
            section = "combatTitle",
            hidden = true,
            unhide = "stopFood"
    )
    default int foodID() {
        return 0;
    }

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable to turn on in game UI",
            position = 300
    )
    default boolean enableUI() {
        return true;
    }

    @ConfigItem(
            keyName = "logout",
            name = "Logout When No Food/Ammo",
            description = "Enable to logout when out of food or ammo",
            position = 305,
            title = "combatTitle"
    )
    default boolean logout() {
        return true;
    }

    @ConfigItem(
            keyName = "forceDeaggro",
            name = "Force Reset",
            description = "Will force a reset run.",
            position = 310
    )
    default Button forceDeaggro() {
        return new Button();
    }


    @ConfigItem(
            keyName = "startButton",
            name = "Start/Stop",
            description = "on/off plugin",
            position = 320
    )
    default Button startButton() {
        return new Button();
    }


}

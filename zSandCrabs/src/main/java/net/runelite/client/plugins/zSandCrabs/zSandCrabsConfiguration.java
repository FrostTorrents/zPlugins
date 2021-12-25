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
package net.runelite.client.plugins.zSandCrabs;

import net.runelite.client.config.*;
import net.runelite.client.config.ConfigTitle;


@ConfigGroup("zSandCrabs")
public interface zSandCrabsConfiguration extends Config
{

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
			name = "Instructions",
			description = "",
			position = 16
	)
	String instructionsTitle = "instructionsTitle";

	@ConfigItem(
			keyName = "instruction",
			name = "",
			description = "Instructions. Don't enter anything into this field",
			position = 17,
			title = "instructionsTitle"
	)
	default String instruction()
	{
		return "Please enter in your Starter Tile Location, and Reset Tile Location coordinates in below.";
	}


	@ConfigItem(
			keyName = "customNPCLocation",
			name = "Starter Tile Location",
			description = "Enter the location you want to kill Sand Crabs at. (x,y,z).",
			position = 40,
			title = "Custom Locations"

	)
	default String customNPCLocation()
	{
		return "1776,3468,0";
	}

	@ConfigItem(
			keyName = "customResetLocation",
			name = "Reset Tile Location",
			description = "Enter the location you want to Reset Aggro at. (x,y,z).",
			position = 50,
			title = "Custom Locations"

	)
	default String customResetLocation()
	{
		return "1778,3500,0";
	}


	@ConfigItem(
			keyName = "resetTime",
			name = "Reset Time",
			description = "How long to wait before resetting",
			position = 55,
			hidden = true
	)
	default int resetTime()
	{
		return 620;
	}

	@ConfigItem(
			keyName = "resetTimeRandomization",
			name = "Reset Time Randomization",
			description = "Random Deviation - Timer Reset (in Seconds)",
			position = 56
	)
	default int resetTimeRandomization()
	{
		return 10;
	}

	@ConfigItem(
			keyName = "npcName",
			name = "NPC Name",
			description = "Enter the exact name of the NPC",
			position = 57,
			hidden = true


	)
	default String npcName()
	{
		return "Sand Crab";
	}

	@ConfigItem(
			keyName = "enableSpec",
			name = "Enable Using Special Attack",
			description = "Enable to turn on using weapon special attack",
			position = 95

	)
	default boolean enableSpec()
	{
		return false;
	}

	@ConfigItem(
			keyName = "specCost",
			name = "Spec Cost",
			description = "Enter the amount of Spec energy it uses",
			position = 96,
			hidden = true,
			unhide = "enableSpec"

	)
	default int specCost()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "forceDeaggro",
			name = "Force Reset",
			description = "Will force a reset run",
			position = 99
	)
	default Button forceDeaggro()
	{
		return new Button();
	}


	@ConfigItem(
			keyName = "startButton",
			name = "Start/Stop",
			description = "on/off plugin",
			position = 100
	)
	default Button startButton()
	{
		return new Button();
	}


}

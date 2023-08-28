package com.dpscounterimproved;

import com.dpscounterimproved.config.MeterDisplayMode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("dpscounterimproved")
public interface DpsCounterImprovedConfig extends Config
{

	@ConfigSection(
			name = "Display",
			description = "All options that configure the meter display",
			position = 0
	)
	String meterDisplay = "displaySection";

	@ConfigItem(
			keyName = "meterDisplayMode",
			name = "Meter Display Mode",
			description = "The information type that is displayed on the meter",
			section = meterDisplay
	)
	default MeterDisplayMode meterDisplayMode() {
		return MeterDisplayMode.DPS;
	}

	@ConfigItem(
			position = 1,
			keyName = "autopause",
			name = "Auto pause",
			description = "Pause the DPS tracker when a boss dies"
	)
	default boolean autopause()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "autoreset",
			name = "Auto reset",
			description = "Reset the DPS tracker when a boss dies"
	)
	default boolean autoreset()
	{
		return false;
	}

	@ConfigItem(
			position = 3,
			keyName = "bossDamage",
			name = "Only boss damage",
			description = "Only count damage done to the boss, and not to other NPCs"
	)
	default boolean bossDamage()
	{
		return false;
	}
}
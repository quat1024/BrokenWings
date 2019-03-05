package quaternary.brokenwings.config;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quaternary.brokenwings.BrokenWings;
import quaternary.brokenwings.countermeasures.Countermeasures;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = BrokenWings.MODID)
public class WingConfig {
	
	//General
	public static int[] DIMENSION_LIST;
	public static ListMode MODE;
	public static List<Item> WHITELIST_ARMOR_ITEMS;
	public static List<Item> WHITELIST_INVENTORY_ITEMS;
	
	//Effects
	public static boolean PRINT_TO_LOG;
	public static boolean SEND_STATUS_MESSAGE;
	public static boolean SHOW_PARTICLES;
	public static int EFFECT_INTERVAL;
	public static String FIXED_MESSAGE;
	
	//Client
	public static boolean SHOW_WHITELIST_TOOLTIP; 
	
	/////
	public static Configuration config;
	
	public static void preinit(FMLPreInitializationEvent e) {
		//split off, just b/c getSuggestedConfigurationFile is awesome
		//the main config is read in init, so i have access to items
		config = new Configuration(e.getSuggestedConfigurationFile(), "3");
		
		if(!"3".equals(config.getLoadedConfigVersion())) {
			FMLLog.bigWarning("[Broken Wings] You should delete and regenerate your config! I made some big changes, sorry!");
		}
	}
	
	public static void init(FMLInitializationEvent e) {
		config.load();
		
		readConfig();
	}
	
	public static void readConfig() {
		//General
		//TODO maybe ask TF for its config option.
		int[] defaultBanned = Loader.isModLoaded("twilightforest") ? new int[]{7} : new int[0];
		
		DIMENSION_LIST = ConfigHelpers.getIntArray(config, "dimensionIdList", "general", defaultBanned, "The list of dimension IDs, used as a whitelist or blacklist, depending on your other config settings.");
		
		MODE = ConfigHelpers.getEnum(config, "mode", "general", ListMode.BLACKLIST, "What mode should Broken Wings operate under?", (mode) -> {
			switch (mode) {
				case BLACKLIST: return "Flying is disabled in only the dimensions listed in \"dimensionList\".";
				case WHITELIST: return "Flying is disabled in all dimensions, except the ones listed in \"dimensionList\".";
				case ALWAYS_DENY: return "Flying is always disabled, regardless of dimension ID.";
				case ALWAYS_ALLOW: return "Flying is never disabled (it's like the mod isn't even installed)";
				default: return "h";
			}
		}, ListMode.class);
		
		WHITELIST_ARMOR_ITEMS = ConfigHelpers.getRegistryItems(ForgeRegistries.ITEMS, config, "whitelistArmor", "general", Collections.emptyList(), "A player wearing one of these armor pieces will be immune to the no-flight rule.");
		
		WHITELIST_INVENTORY_ITEMS = ConfigHelpers.getRegistryItems(ForgeRegistries.ITEMS, config, "whitelistInventory", "general", Collections.emptyList(), "A player with one of these items in their inventory will be immune to the no-flight rule.");
		
		//Countermeasures
		Countermeasures.readConfig(config);
		
		//Effects
		PRINT_TO_LOG = config.getBoolean("printToLog", "effects", true, "Should a message be printed to the server console when a player is dropped from the sky?");
		
		SEND_STATUS_MESSAGE = config.getBoolean("sendStatusMessage", "effects", true, "Should players receive a status message when they are dropped from the sky?");
		
		SHOW_PARTICLES = config.getBoolean("showParticles", "effects", true, "Should players create particle effects when they are dropped from the sky?");
		
		EFFECT_INTERVAL = config.getInt("effectInterval", "effects", 3, 0, Integer.MAX_VALUE, "To prevent spamming players and the server console, how many seconds will need to pass before performing another effect? (Players will still drop out of the sky if they try to fly faster than this interval.)");
		
		FIXED_MESSAGE = config.getString("fixedStatusMessage", "effects", "", "Whatever you enter here will be sent to players when they are dropped out of the sky if 'effects.sendStatusMessage' is enabled. If this is empty, I'll choose from my own internal list of messages.");
		FIXED_MESSAGE = FIXED_MESSAGE.trim();
		
		//Client
		SHOW_WHITELIST_TOOLTIP = config.getBoolean("showWhitelistTooltip", "client", true, "Show a tooltip on whitelisted items informing the player that they can use this item to bypass the rule.");
		
		if(config.hasChanged()) config.save();
	}
	
	@SubscribeEvent
	public static void configChange(ConfigChangedEvent e) {
		if(e.getModID().equals(BrokenWings.MODID)) {
			readConfig();
		}
	}
}

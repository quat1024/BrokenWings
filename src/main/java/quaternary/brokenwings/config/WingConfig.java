package quaternary.brokenwings.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quaternary.brokenwings.BrokenWings;
import quaternary.brokenwings.anticompat.AbstractAntiCompat;
import quaternary.brokenwings.anticompat.AntiCompats;

import java.io.File;

public class WingConfig {
	
	public static int[] DIMENSION_LIST;
	public static ListMode MODE;
	
	public static boolean PRINT_TO_LOG;
	public static boolean SEND_STATUS_MESSAGE;
	public static boolean SHOW_PARTICLES;
	public static int EFFECT_INTERVAL;
	
	private static Configuration config;
		
	public static void initConfig() {
		config = new Configuration(new File(Loader.instance().getConfigDir(), "brokenwings.cfg"), "2");
		config.load();
		
		readConfig();
	}
	
	public static void readConfig() {
		//TODO maybe ask TF for its config option.
		int[] defaultBanned = Loader.isModLoaded("twilightforest") ? new int[]{7} : new int[0];
		
		DIMENSION_LIST = ConfigHelpers.getIntArray(config, "dimensionList", "", defaultBanned, "The list of dimension IDs, used as a whitelist or blacklist, depending on your other config settings.\nPass this in as a comma-separated string; whitespace is OK. For example: \"4, 8, -35\"");
		
		MODE = ConfigHelpers.getEnum(config, "mode", "", ListMode.BLACKLIST, "What mode should Broken Wings operate under?", (mode) -> {
			switch (mode) {
				case BLACKLIST: return "Flying is disabled in only the dimensions listed in \"dimensionList\".";
				case WHITELIST: return "Flying is disabled in all dimensions, except the ones listed in \"dimensionList\".";
				case ALWAYS_DENY: return "Flying is always disabled, regardless of dimension ID.";
				case ALWAYS_ALLOW: return "Flying is never disabled.";
				default: return "h";
			}
		}, ListMode.class);
		
		//Countermeasures
		for(AbstractAntiCompat anti : AntiCompats.get()) {
			String configName = anti.getName();
			String friendlyName = anti.getFriendlyName();
			anti.setEnabled(config.getBoolean(configName, "countermeasures", true, "Is the \"" + friendlyName + "\" countermeasure enabled?"));
		}
		
		//Effects
		PRINT_TO_LOG = config.getBoolean("printToLog", "effects", true, "Should a message be printed to the server console when a player is dropped from the sky?");
		
		SEND_STATUS_MESSAGE = config.getBoolean("sendStatusMessage", "effects", true, "Should players receive a status message when they are dropped from the sky?");
		
		SHOW_PARTICLES = config.getBoolean("showParticles", "effects", true, "Should players create particle effects when they are dropped from the sky?");
		
		EFFECT_INTERVAL = config.getInt("effectInterval", "effects", 3, 0, Integer.MAX_VALUE, "To prevent spamming players and the server console, how many seconds will need to pass before performing another effect?");
		
		if(config.hasChanged()) config.save();
	}
	
	@SubscribeEvent
	public static void configChange(ConfigChangedEvent e) {
		if(e.getModID().equals(BrokenWings.MODID)) {
			readConfig();
		}
	}
}

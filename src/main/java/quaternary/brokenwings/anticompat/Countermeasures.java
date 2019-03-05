package quaternary.brokenwings.anticompat;

import net.minecraftforge.common.config.Configuration;

import java.util.*;

public class Countermeasures {
	public static final Set<ICountermeasure> ALL = new HashSet<>();
	public static final Set<ICountermeasure> ENABLED = new HashSet<>();
	
	public static void createAll() {
		ALL.add(new ElytraCountermeasure());
		ALL.add(new CreativeStyleFlightCountermeasure());
		
		ENABLED.addAll(ALL);
	}
	
	public static void readConfig(Configuration config) {
		ENABLED.clear();
		
		for(ICountermeasure anti : ALL) {
			String configName = anti.getName();
			String friendlyName = anti.getFriendlyName();
			boolean enabled = config.getBoolean(configName, "countermeasures", true, "Is the \"" + friendlyName + "\" countermeasure enabled?");
			
			if(enabled) {
				ENABLED.add(anti);
			}
		}
	}
}

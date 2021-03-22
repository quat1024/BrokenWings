package quaternary.brokenwings.countermeasures;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import quaternary.brokenwings.countermeasures.compat.WingsCountermeasure;

import java.util.HashSet;
import java.util.Set;

public class Countermeasures {
	public static final Set<ICountermeasure> ALL = new HashSet<>();
	public static final Set<ICountermeasure> ENABLED = new HashSet<>();
	
	public static void createAll() {
		ALL.add(new ElytraCountermeasure());
		ALL.add(new CreativeStyleFlightCountermeasure());
		ALL.add(new ButterfingersCountermeasure());
		ALL.add(new GreasyArmorCountermeasure());
		
		if(Loader.isModLoaded("wings")) ALL.add(new WingsCountermeasure());
		
		ENABLED.addAll(ALL);
	}
	
	public static void readConfig(Configuration config) {
		ENABLED.clear();
		
		for(ICountermeasure measure : ALL) {
			String configName = measure.getName();
			String friendlyName = measure.getFriendlyName();
			
			String description = "Is the \"" + friendlyName + "\" countermeasure enabled?";
			if(!measure.getDescription().isEmpty()) {
				description += " ";
				description += measure.getDescription();
			}
			
			boolean enabled = config.getBoolean(configName, "countermeasures", true, description);
			measure.readConfig(config);
			
			if(enabled) {
				ENABLED.add(measure);
			}
		}
	}
}

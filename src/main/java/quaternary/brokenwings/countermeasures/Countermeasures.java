package quaternary.brokenwings.countermeasures;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import quaternary.brokenwings.countermeasures.compat.SimplyJetpacksCountermeasure;
import quaternary.brokenwings.countermeasures.compat.WingsCountermeasure;

import java.util.HashSet;
import java.util.Set;

public class Countermeasures {
	public static final Set<ICountermeasure> ALL = new HashSet<>();
	public static final Set<ICountermeasure> ENABLED = new HashSet<>();
	
	public static void createAll() {
		ALL.add(new ElytraCountermeasure());
		ALL.add(new CreativeStyleFlightCountermeasure());
		
		if(Loader.isModLoaded("wings")) ALL.add(new WingsCountermeasure());
		//if(Loader.isModLoaded("simplyjetpacks")) ALL.add(new SimplyJetpacksCountermeasure());
		
		ENABLED.addAll(ALL);
	}
	
	public static void readConfig(Configuration config) {
		ENABLED.clear();
		
		for(ICountermeasure measure : ALL) {
			String configName = measure.getName();
			String friendlyName = measure.getFriendlyName();
			boolean enabled = config.getBoolean(configName, "countermeasures", true, "Is the \"" + friendlyName + "\" countermeasure enabled?");
			
			if(enabled) {
				ENABLED.add(measure);
			}
		}
	}
}

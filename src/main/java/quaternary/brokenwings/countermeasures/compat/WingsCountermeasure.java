package quaternary.brokenwings.countermeasures.compat;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import quaternary.brokenwings.countermeasures.ICountermeasure;

import java.util.HashSet;
import java.util.Set;

public class WingsCountermeasure implements ICountermeasure {
	@Override
	public String getName() {
		return "wings";
	}
	
	@Override
	public String getFriendlyName() {
		return "Wings mod compat";
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		Flight f = Flights.get(playerMP);
		if(f == null) return false;
		else return f.isFlying();
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		Flight f = Flights.get(playerMP);
		if(f != null) {
			f.setIsFlying(false, Flight.PlayerSet.ofPlayer(playerMP));
		}
	}
}

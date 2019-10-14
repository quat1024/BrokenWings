package quaternary.brokenwings.countermeasures.compat;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import net.minecraft.entity.player.EntityPlayerMP;
import quaternary.brokenwings.countermeasures.ICountermeasure;

public class WingsCountermeasure implements ICountermeasure {
	@Override
	public String getName() {
		return "wings";
	}
	
	@Override
	public String getFriendlyName() {
		return "Wings mod";
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
			f.setIsFlying(false, Flight.PlayerSet.ofAll());
		}
	}
}

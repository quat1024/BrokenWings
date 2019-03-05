package quaternary.brokenwings.countermeasures;

import net.minecraft.entity.player.EntityPlayerMP;

public class CreativeStyleFlightCountermeasure implements ICountermeasure {
	@Override
	public String getName() {
		return "creativeStyle";
	}
	
	@Override
	public String getFriendlyName() {
		return "Creative-style flight";
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		return playerMP.capabilities.isFlying;
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		playerMP.capabilities.isFlying = false;
		playerMP.sendPlayerAbilities();
	}
}

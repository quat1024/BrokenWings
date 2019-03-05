package quaternary.brokenwings.countermeasures;

import net.minecraft.entity.player.EntityPlayerMP;

public class ElytraCountermeasure implements ICountermeasure {
	@Override
	public String getName() {
		return "elytra";
	}
	
	@Override
	public String getFriendlyName() {
		return "Elytra flight";
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		return playerMP.isElytraFlying();
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		playerMP.onGround = true;
	}
}

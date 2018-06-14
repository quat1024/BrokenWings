package quaternary.brokenwings.anticompat;

import net.minecraft.entity.player.EntityPlayerMP;

public class CapIsFlyingAntiCompat extends AbstractAntiCompat {
	public CapIsFlyingAntiCompat() {
		super("isFlying", "isFlying player capability");
	}
	
	@Override
	public boolean isFlyingImpl(EntityPlayerMP playerMP) {
		return playerMP.capabilities.isFlying;
	}
	
	@Override
	public boolean tryStopFlying(EntityPlayerMP playerMP) {
		playerMP.capabilities.isFlying = false;
		playerMP.sendPlayerAbilities();
		
		return true;
	}
}

package quaternary.brokenwings.anticompat;

import net.minecraft.entity.player.EntityPlayerMP;

public class ElytraAntiCompat extends AbstractAntiCompat {
	public ElytraAntiCompat() {
		super("elytra", "Elytra flight");
	}
	
	@Override
	public boolean isFlyingImpl(EntityPlayerMP playerMP) {
		return playerMP.isElytraFlying();
	}
	
	@Override
	public boolean tryStopFlying(EntityPlayerMP playerMP) {
		playerMP.onGround = true;
		
		return true;
	}
}

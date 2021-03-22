package quaternary.brokenwings.compat;

import net.minecraft.entity.player.EntityPlayerMP;

public class NayBubbles implements BubblesProxy {
	@Override
	public boolean isPlayerImmune(EntityPlayerMP playerMP) {
		return false;
	}
}

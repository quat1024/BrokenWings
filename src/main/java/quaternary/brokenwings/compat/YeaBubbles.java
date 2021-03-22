package quaternary.brokenwings.compat;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import quaternary.brokenwings.config.WingConfig;

public class YeaBubbles implements BubblesProxy {
	@Override
	public boolean isPlayerImmune(EntityPlayerMP playerMP) {
		IBaublesItemHandler bubbles = BaublesApi.getBaublesHandler(playerMP);
		
		for(int i = 0; i < bubbles.getSlots(); i++) {
			if(WingConfig.BUBBLE_BYPASS_KEYS.contains(bubbles.getStackInSlot(i), playerMP.dimension)) return true;
		}
		
		return false;
	}
}

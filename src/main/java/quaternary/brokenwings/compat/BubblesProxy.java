package quaternary.brokenwings.compat;

import net.minecraft.entity.player.EntityPlayerMP;

public interface BubblesProxy {
	/**
	 * Is this player wearing a Bauble that allows them to bypass the no-flight rule?
	 */
	boolean isPlayerImmune(EntityPlayerMP playerMP);
}

package quaternary.brokenwings.anticompat;

import net.minecraft.entity.player.EntityPlayerMP;

/** 
 * Represents a possible method players might use to fly.
 * The name? It's because the whole point of this mod is to break other mods - "anti-compatibilty"
 * */
public interface ICountermeasure {
	/**
	 * The internal-ish name for this anticompat.
	 * Will be used as the config key.
	 */
	String getName();
	
	/**
	 * A nicer name for this anticompat.
	 * Will be used in log messages and in the config comment.
	 */
	String getFriendlyName();
	
	/**
	 * @return If this player is flying using this method.
	 */
	boolean isFlying(EntityPlayerMP playerMP);
	
	/**
	 * Try to stop this player from flying using this method.
	 * Don't worry about cancelling velocity or playing effects; those happen outside.
	 */
	void stopFlying(EntityPlayerMP playerMP);
}

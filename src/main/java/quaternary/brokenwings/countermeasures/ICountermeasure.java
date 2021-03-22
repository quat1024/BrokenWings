package quaternary.brokenwings.countermeasures;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

/** 
 * Represents a possible method players might use to fly.
 * */
public interface ICountermeasure {
	/**
	 * The internal-ish name for this countermeasure.
	 * Will be used as the config key.
	 */
	String getName();
	
	/**
	 * A nicer name for this countermeasure.
	 * Will be used in log messages and in the config comment.
	 */
	String getFriendlyName();
	
	/**
	 * Return a nonempty string if your countermeasure needs some explanation, it'll get appended to the "should the 'x' countermeasure be enabled" string in the config file.
	 */
	default String getDescription() {
		return "";
	}
	
	/**
	 * @return If this player is flying using this method.
	 */
	boolean isFlying(EntityPlayerMP playerMP);
	
	/**
	 * Try to stop this player from flying using this method.
	 * Don't worry about cancelling velocity or playing effects; those happen outside.
	 */
	void stopFlying(EntityPlayerMP playerMP);
	
	/**
	 * Read special configuration values. Called at startup and when loading the config file.
	 * Please put config options in the "countermeasures.[name]" category, where [name] is the value of getName().
	 */
	default void readConfig(Configuration config) {
		//No-op
	}
}

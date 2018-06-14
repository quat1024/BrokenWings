package quaternary.brokenwings.anticompat;

import net.minecraft.entity.player.EntityPlayerMP;

/** 
 * Represents a possible method players might use to fly.
 * The name? It's because the whole point of this mod is to break other mods - "anti-compatibilty"
 * */
public abstract class AbstractAntiCompat {
	public AbstractAntiCompat(String name, String friendlyName) {
		this.name = name;
		this.friendlyName = friendlyName;
	}
	
	private String name;
	private String friendlyName;
	private boolean enabled;
	
	public String getName() {
		return name;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isFlying(EntityPlayerMP playerMP) {
		return enabled && isFlyingImpl(playerMP); 
	}
	
	/**
	 * Is this player flying using this method?
	 * */
	protected abstract boolean isFlyingImpl(EntityPlayerMP playerMP);
	
	/**
	 * Tries to stop the player from flying via this method.
	 * @return if you think the attempt was successful
	 * */
	public abstract boolean tryStopFlying(EntityPlayerMP playerMP);
}

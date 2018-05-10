package quaternary.brokenwings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Mod(modid = BrokenWings.MODID, name = BrokenWings.NAME, version = BrokenWings.VERSION)
public class BrokenWings {
	public static final String MODID = "brokenwings";
	public static final String NAME = "Broken Wings";
	public static final String VERSION = "1.0.0";
	
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	
	public static final Map<String, Long> lastMessageTimes = new HashMap<>();
	
	public static void dropPlayer(EntityPlayer player) {
		EntityPlayerMP pmp = (EntityPlayerMP) player;
		
		pmp.capabilities.isFlying = false;
		pmp.sendPlayerAbilities();
		pmp.motionX = 0;
		pmp.motionY -= 0.3;
		pmp.motionZ = 0;
		pmp.onGround = true; //forces elytra flight to stop
		
		//mark player dirty
		pmp./*isDirty*/isAirBorne = true;
		pmp.getServerWorld().getEntityTracker().sendToTrackingAndSelf(pmp, new SPacketEntityVelocity(player));
		
		long now = pmp.getServerWorld().getTotalWorldTime();
		
		//Avoid spamming the player/log
		if(now - lastMessageTimes.getOrDefault(pmp.getName(), 0L) > 20 * 3) {
			lastMessageTimes.put(pmp.getName(), now);
			
			if(WingConfig.PRINT_TO_LOG) {
				LOGGER.info("Dropped " + pmp.getName() + " out of the sky. Dimension: " + pmp.dimension + " Position: " + pmp.getPosition().toString().replace("BlockPos", ""));
			}
			
			if(WingConfig.TELL_PLAYER) {
				final int totalMessages = 9;
				int message = player.world.rand.nextInt(totalMessages);
				
				pmp.sendStatusMessage(new TextComponentTranslation(MODID + ".dropstatus." + message), true);
				
				pmp.getServerWorld().spawnParticle(EnumParticleTypes.TOTEM, pmp.posX, pmp.posY, pmp.posZ, 45, 0, 0, 0, .2);
			}
		}
	}
	
	@Mod.EventBusSubscriber(modid = MODID)
	public static class Events {
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static void playerTick(TickEvent.PlayerTickEvent e) {
			if(WingConfig.MODE == ConfigMode.WHITELIST_ALL) return;
			if(e.player.world.isRemote) return;
			if(e.player.isSpectator() || e.player.isCreative()) return;
			
			if((WingConfig.BAN_ELYTRA && e.player.isElytraFlying()) || e.player.capabilities.isFlying) {
				switch(WingConfig.MODE) {
					case BLACKLIST_ALL:
						dropPlayer(e.player);
						return;
					case WHITELIST: {
						if(IntStream.of(WingConfig.LIST).noneMatch(x -> x == e.player.dimension)) {
							dropPlayer(e.player);
						}
					}
					break;
					case BLACKLIST: {
						if(IntStream.of(WingConfig.LIST).anyMatch(x -> x == e.player.dimension)) {
							dropPlayer(e.player);
						}
					}
				}
			}
		}
	}
	
	@Config(modid = MODID)
	@Mod.EventBusSubscriber(modid = MODID)
	public static class WingConfig {
		@Config.Name("Mode")
		@Config.Comment({
			"The rule to determine whether flight is allowed in a particular dimension.",
			"Available options:",
			"WHITELIST_ALL - flight is allowed in all dimensions (this mod has no effect)",
			"BLACKLIST_ALL - flight is banned in all dimensions",
			"WHITELIST - the \"Dimension List\" config will determine which dimensions flight is allowed in; it will be banned in all others",
			"BLACKLIST - the \"Dimension List\" config will determine which dimensions flight is banned in; it will be allowed in all others"
		})
		public static ConfigMode MODE = ConfigMode.BLACKLIST;
		
		@Config.Name("Dimension List")
		@Config.Comment("The dimension list, used in WHITELIST and BLACKLIST modes.")
		public static int[] LIST = new int[]{7};
		
		@Config.Name("Print to Log")
		@Config.Comment("When Broken Wings drops a player from the sky, should a message be printed to the server log?")
		public static boolean PRINT_TO_LOG = true;
		
		@Config.Name("Tell Player")
		@Config.Comment("When Broken Wings drops a player from the sky, should they receive a status message?")
		public static boolean TELL_PLAYER = true;
		
		@Config.Name("Ban Elytra")
		@Config.Comment("Will Elytra flight also be blocked?")
		public static boolean BAN_ELYTRA = false;
		
		@SubscribeEvent
		public static void configChange(ConfigChangedEvent e) {
			if(e.getModID().equals(MODID)) {
				ConfigManager.sync(MODID, Config.Type.INSTANCE);
			}
		}
	}
	
	public enum ConfigMode {
		BLACKLIST, WHITELIST, BLACKLIST_ALL, WHITELIST_ALL
	}
}

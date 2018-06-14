package quaternary.brokenwings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quaternary.brokenwings.anticompat.*;
import quaternary.brokenwings.config.ListMode;
import quaternary.brokenwings.config.WingConfig;

import java.util.*;

@Mod(modid = BrokenWings.MODID, name = BrokenWings.NAME, version = BrokenWings.VERSION)
public class BrokenWings {
	public static final String MODID = "brokenwings";
	public static final String NAME = "Broken Wings";
	public static final String VERSION = "2.0.0";
	
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	
	public static final Map<String, Long> lastMessageTimes = new HashMap<>();
	public static final Random messageRandom = new Random();
	
	@Mod.EventHandler
	public static void init(FMLInitializationEvent e) {
		AntiCompats.init();
		
		WingConfig.initConfig();
		
		MinecraftForge.EVENT_BUS.register(BrokenWings.class);
	}
	
	static final List<String> usedMethods = new ArrayList<>();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void playerTick(TickEvent.PlayerTickEvent e) {
		if(WingConfig.MODE == ListMode.ALWAYS_ALLOW) return;
		EntityPlayer player = e.player;
		if(player.world.isRemote) return;
		if(player.isCreative() || player.isSpectator()) return;
		if(!WingConfig.MODE.isDimensionBanned(player.dimension)) return;
		
		EntityPlayerMP playerMP = (EntityPlayerMP) player;
		
		boolean wasFlying = false;
		usedMethods.clear();
		for(AbstractAntiCompat anti : AntiCompats.get()) {
			if(anti.isFlying(playerMP)) {
				wasFlying |= anti.tryStopFlying(playerMP);
				usedMethods.add(anti.getFriendlyName());
			}
		}
		
		if(wasFlying) {
			playerMP.motionX = 0;
			playerMP.motionY -= 0.3;
			playerMP.motionZ = 0;
			playerMP./*isDirty*/isAirBorne = true;
			
			playerMP.getServerWorld().getEntityTracker().sendToTrackingAndSelf(playerMP, new SPacketEntityVelocity(playerMP));
			
			if(WingConfig.SEND_STATUS_MESSAGE || WingConfig.SHOW_PARTICLES || WingConfig.PRINT_TO_LOG) {
				long now = playerMP.getServerWorld().getTotalWorldTime();
				
				if(now - lastMessageTimes.getOrDefault(playerMP.getName(), 0L) > 20 * WingConfig.EFFECT_INTERVAL) {
					lastMessageTimes.put(playerMP.getName(), now);
					
					if(WingConfig.SEND_STATUS_MESSAGE) {
						int totalMessages = 9;
						int messageIndex = messageRandom.nextInt(totalMessages);
						
						playerMP.sendStatusMessage(new TextComponentTranslation("brokenwings.dropstatus." + messageIndex), true);
					}
					
					if(WingConfig.SHOW_PARTICLES) {
						playerMP.getServerWorld().spawnParticle(EnumParticleTypes.TOTEM, playerMP.posX, playerMP.posY, playerMP.posZ, 45, 0, 0, 0, .2);
					}
					
					if(WingConfig.PRINT_TO_LOG) {
						LOGGER.info("Dropped " + playerMP.getName() + " out of the sky.");
						LOGGER.info("Dimension: " + playerMP.dimension);
						LOGGER.info("Position: " + niceBlockPosToString(playerMP.getPosition()));
						for(String method : usedMethods) {
							LOGGER.info("Method: " + method);
						}
					}
				}
			}
		}
	}
	
	private static String niceBlockPosToString(BlockPos pos) {
		return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
	}
	
}

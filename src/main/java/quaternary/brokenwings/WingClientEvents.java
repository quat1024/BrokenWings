package quaternary.brokenwings;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.brokenwings.config.WingConfig;

@Mod.EventBusSubscriber(modid = BrokenWings.MODID, value = Side.CLIENT)
public class WingClientEvents {
	@SubscribeEvent
	public static void tooltip(ItemTooltipEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		if(player == null || player.world == null || !WingConfig.SHOW_BYPASS_KEY_TOOLTIP) return;
		
		ItemStack i = e.getItemStack();
		boolean isArmor = WingConfig.ARMOR_BYPASS_KEYS.contains(i, player.dimension);
		boolean isInv = WingConfig.INVENTORY_BYPASS_KEYS.contains(i, player.dimension);
		if(!isArmor && !isInv) return;
		
		TextFormatting color = WingConfig.MODE.isFlightInDimensionBanned(player.dimension) ? TextFormatting.GREEN : TextFormatting.RED;
		
		e.getToolTip().add(color + I18n.format(isArmor ? "brokenwings.tooltip.armorBypassKey" : "brokenwings.tooltip.inventoryBypassKey"));
	}
}

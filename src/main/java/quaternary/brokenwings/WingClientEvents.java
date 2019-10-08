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
		if(player == null || player.world == null || !WingConfig.SHOW_WHITELIST_TOOLTIP) return;
		
		ItemStack i = e.getItemStack();
		boolean isArmor = WingConfig.WHITELIST_ARMOR_ITEMS.contains(i);
		boolean isInv = WingConfig.WHITELIST_INVENTORY_ITEMS.contains(i);
		if(!isArmor && !isInv) return;
		
		TextFormatting color = WingConfig.MODE.isFlightInDimensionBanned(player.dimension) ? TextFormatting.GREEN : TextFormatting.RED;
		
		e.getToolTip().add(color + I18n.format(isArmor ? "brokenwings.tooltip.armorWhitelist" : "brokenwings.tooltip.invWhitelist"));
	}
}

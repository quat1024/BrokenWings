package quaternary.brokenwings.countermeasures;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;
import quaternary.brokenwings.config.ConfigHelpers;
import quaternary.brokenwings.config.ItemList;

public class ButterfingersCountermeasure implements ICountermeasure {
	private int interval;
	private ItemList items;
	
	@Override
	public String getName() {
		return "butterfingers";
	}
	
	@Override
	public String getFriendlyName() {
		return "Butterfingers";
	}
	
	@Override
	public String getDescription() {
		return "This measure causes players to periodically drop items matching a configurable list. Use for 'angel-ring'-like items.";
	}
	
	@Override
	public void readConfig(Configuration config) {
		String cat = "countermeasures." + getName();
		
		interval = config.getInt("interval", cat, 70, 1, Integer.MAX_VALUE, "How often should players automatically drop the banned items? (The lower you set this, the more annoying it is if people accidentally visit a dimension carrying one of these)");
		items = ConfigHelpers.getItemList(config, "items", cat, new ItemList(), "The banned list of items.");
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		if(playerMP.world.getTotalWorldTime() % interval != 0) return false;
		
		if(hav(playerMP, playerMP.inventory.mainInventory)) return true;
		if(hav(playerMP, playerMP.inventory.offHandInventory)) return true;
		
		return false;
	}
	
	private boolean hav(EntityPlayerMP playerMP, NonNullList<ItemStack> yea) {
		for(ItemStack itemStack : yea) {
			if(items.contains(itemStack, playerMP.dimension)) return true;
		}
		
		return false;
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		noh(playerMP, playerMP.inventory.mainInventory);
		noh(playerMP, playerMP.inventory.offHandInventory);
	}
	
	private void noh(EntityPlayerMP playerMP, NonNullList<ItemStack> yea) {
		for(int i = 0; i < yea.size(); i++) {
			if(items.contains(yea.get(i), playerMP.dimension)) {
				ItemStack there = yea.get(i).copy();
				yea.set(i, ItemStack.EMPTY);
				playerMP.dropItem(there, false);
			}
		}
	}
}

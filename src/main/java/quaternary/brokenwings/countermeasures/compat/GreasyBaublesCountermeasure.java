package quaternary.brokenwings.countermeasures.compat;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import quaternary.brokenwings.config.ConfigHelpers;
import quaternary.brokenwings.config.ItemList;
import quaternary.brokenwings.countermeasures.ICountermeasure;

public class GreasyBaublesCountermeasure implements ICountermeasure {
	private ItemList items;
	
	@Override
	public String getName() {
		return "greasybaubles";
	}
	
	@Override
	public String getFriendlyName() {
		return "Greasy Baubles";
	}
	
	@Override
	public String getDescription() {
		return "Unequips Baubles matching a list. If it can't be unequipped (full inventory, maybe), it gets thrown on the ground.";
	}
	
	@Override
	public void readConfig(Configuration config) {
		String cat = "countermeasures." + getName();
		items = ConfigHelpers.getItemList(config, "items", cat, new ItemList(), "Items to automatically unequip when worn in the Bauble slots.");
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		IBaublesItemHandler bubbles = BaublesApi.getBaublesHandler(playerMP);
		
		for(int i = 0; i < bubbles.getSlots(); i++) {
			if(items.contains(bubbles.getStackInSlot(i), playerMP.dimension)) return true;
		}
		
		return false;
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		IBaublesItemHandler bubbles = BaublesApi.getBaublesHandler(playerMP);
		
		for(int i = 0; i < bubbles.getSlots(); i++) {
			if(items.contains(bubbles.getStackInSlot(i), playerMP.dimension)) {
				ItemStack bubble = bubbles.getStackInSlot(i).copy();
				bubbles.setStackInSlot(i, ItemStack.EMPTY);
				
				//Try to add it to the main inventory, if it fails, drop it. (See ItemBucket)
				if(playerMP.inventory.addItemStackToInventory(bubble)) {
					playerMP.dropItem(bubble, false);
				}
			}
		}
	}
}

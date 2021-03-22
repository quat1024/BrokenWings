package quaternary.brokenwings.countermeasures;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;
import quaternary.brokenwings.config.ConfigHelpers;
import quaternary.brokenwings.config.ItemList;

public class GreasyArmorCountermeasure implements ICountermeasure {
	private ItemList items;
	
	@Override
	public String getName() {
		return "greasyarmor";
	}
	
	@Override
	public String getFriendlyName() {
		return "Greasy Armor";
	}
	
	@Override
	public String getDescription() {
		return "Unequips armor matching a list. If it can't be unequipped (full inventory, maybe), it gets thrown on the ground.";
	}
	
	@Override
	public void readConfig(Configuration config) {
		String cat = "countermeasures." + getName();
		items = ConfigHelpers.getItemList(config, "items", cat, new ItemList(), "Items to automatically unequip when worn as armor.");
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		for(ItemStack s : playerMP.inventory.armorInventory) {
			if(items.contains(s, playerMP.dimension)) return true;
		}
		
		return false;
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		NonNullList<ItemStack> armor = playerMP.inventory.armorInventory;
		
		for(int i = 0; i < armor.size(); i++) {
			if(items.contains(armor.get(i), playerMP.dimension)) {
				ItemStack gottem = armor.get(i).copy();
				armor.set(i, ItemStack.EMPTY);
				
				//Try to add it to the main inventory, if it fails, drop it. (See ItemBucket)
				if(playerMP.inventory.addItemStackToInventory(gottem)) {
					playerMP.dropItem(gottem, false);
				}
			}
		}
	}
}

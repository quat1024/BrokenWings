package quaternary.brokenwings.countermeasures.compat;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import quaternary.brokenwings.countermeasures.ICountermeasure;


public class SimplyJetpacksCountermeasure implements ICountermeasure {
	@Override
	public String getName() {
		return "simplyjetpacks2";
	}
	
	@Override
	public String getFriendlyName() {
		return "Simply Jetpacks 2 mod";
	}
	
	@Override
	public boolean isFlying(EntityPlayerMP playerMP) {
		ItemStack wornStack = playerMP.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex());
		Item wornItem = wornStack.getItem();
		
		if(wornItem.getRegistryName().getNamespace().equals("simplyjetpacks")) {
			NBTTagCompound tag = wornStack.getTagCompound();
			if(tag == null) return false;
			else return tag.getBoolean("PackOn") || tag.getBoolean("JetpackHoverModeOn");
		} else return false;
	}
	
	@Override
	public void stopFlying(EntityPlayerMP playerMP) {
		ItemStack wornStack = playerMP.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex());
		wornStack.getTagCompound().setBoolean("PackOn", false);
		wornStack.getTagCompound().setBoolean("JetpackHoverModeOn", false);
	}
}

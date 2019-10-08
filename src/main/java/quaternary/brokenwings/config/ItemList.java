package quaternary.brokenwings.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quaternary.brokenwings.BrokenWings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class ItemList implements Iterable<Map.Entry<Item, Predicate<ItemStack>>> {
	public ItemList() {
		this(HashMultimap.create());
	}
	
	public ItemList(Multimap<Item, Predicate<ItemStack>> entries) {
		this.entries = entries;
	}
	
	private final Multimap<Item, Predicate<ItemStack>> entries;
	private static final Predicate<ItemStack> TRUE = (stack) -> true;
	
	public static final ItemList EMPTY = new ItemList();
	
	public void addItem(Item item) {
		addItemWithPredicate(item, TRUE);
	}
	
	public void addItemWithExactMeta(Item item, int exactMeta) {
		addItemWithPredicate(item, new StackMetaPredicate(exactMeta));
	}
	
	public void addItemWithPredicate(Item item, Predicate<ItemStack> test) {
		entries.put(item, test);
	}
	
	@Override
	@Nonnull
	public Iterator<Map.Entry<Item, Predicate<ItemStack>>> iterator() {
		return entries.entries().iterator();
	}
	
	public boolean contains(ItemStack stack) {
		Collection<Predicate<ItemStack>> tests = entries.get(stack.getItem());
		if(tests == null || tests.isEmpty()) return false;
		else for(Predicate<ItemStack> test : tests) {
			if(test.test(stack)) return true;
		}
		
		return false;
	}
	
	public ItemList copy() {
		return new ItemList(entries);
	}
	
	public String[] toStringArray() {
		if(entries.isEmpty()) return new String[0];
		
		List<String> xd = new ArrayList<>();
		
		entries.forEach((item, test) -> {
			String s = Objects.requireNonNull(item.getRegistryName()).toString();
			if(test == TRUE) {
				//seems good
			} else if(test instanceof StackMetaPredicate) {
				s = s.concat("@" + ((StackMetaPredicate) test).exactMeta);
			} else {
				throw new IllegalStateException(test + " isn't a predicate I know how to serialize");
			}
			
			xd.add(s);
		});
		
		return xd.toArray(new String[0]);
	}
	
	public ItemList addFromStringArray(String[] arr) {
		if(arr.length == 0) return this;
		
		for(int i = 0; i < arr.length; i++) {
			String s = arr[i];
			
			String unparsedItemRes = s;
			Predicate<ItemStack> test = TRUE;
			
			//handle metadata cases
			if(s.contains("@")) {
				String[] split = unparsedItemRes.split("@");
				
				unparsedItemRes = split[0];
				String unparsedMeta = split[1];
				
				try {
					test = new StackMetaPredicate(Integer.parseInt(unparsedMeta));
				} catch(NumberFormatException e) {
					throw new IllegalStateException("Problem parsing metadata value on line " + i);
				}
			}
			
			//parse item
			ResourceLocation itemRes;
			
			try {
				itemRes = new ResourceLocation(unparsedItemRes);
			} catch(RuntimeException e) {
				throw new IllegalStateException("Problem parsing item identifier on line " + i);
			}
			
			if(ForgeRegistries.ITEMS.containsKey(itemRes)) {
				addItemWithPredicate(ForgeRegistries.ITEMS.getValue(itemRes), test);
			} else {
				BrokenWings.LOGGER.error("Unknown item " + unparsedItemRes + " in config file!");
			}
		}
		
		return this;
	}
	
	private static class StackMetaPredicate implements Predicate<ItemStack> {
		public StackMetaPredicate(int exactMeta) {
			this.exactMeta = exactMeta;
		}
		
		public final int exactMeta;
		
		@Override
		public boolean test(ItemStack stack) {
			return stack.getMetadata() == exactMeta;
		}
	}
}

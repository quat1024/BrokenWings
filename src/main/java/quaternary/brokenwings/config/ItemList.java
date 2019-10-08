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
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemList implements Iterable<Map.Entry<Item, ItemList.ExtraConditions>> {
	public ItemList() {
		this(HashMultimap.create());
	}
	
	public ItemList(Multimap<Item, ItemList.ExtraConditions> entries) {
		this.entries = entries;
	}
	
	private final Multimap<Item, ItemList.ExtraConditions> entries;
	private static final ItemList.ExtraConditions TRUE = new ExtraConditions();
	
	public static final ItemList EMPTY = new ItemList();
	
	//Parsing stuff~
	public static final String CONDITIONS_SEPARATOR = " ";
	public static final String META_CONDITION = "meta:";
	public static final String DIMENSION_CONDITION = "dimension:";
	private static final Matcher CONDITION_REGEX = Pattern.compile(
		String.format("(%s|%s)(-?[0-9]+(\\~-?[0-9]+)?)", META_CONDITION, DIMENSION_CONDITION)
	).matcher("");
	
	public void addItem(Item item) {
		addItemWithPredicate(item, TRUE);
	}
	
	public void addItemWithPredicate(Item item, ItemList.ExtraConditions test) {
		entries.put(item, test);
	}
	
	@Override
	@Nonnull
	public Iterator<Map.Entry<Item, ItemList.ExtraConditions>> iterator() {
		return entries.entries().iterator();
	}
	
	public boolean contains(ItemStack stack, int dimension) {
		Collection<ExtraConditions> tests = entries.get(stack.getItem());
		if(tests == null || tests.isEmpty()) return false;
		else for(BiPredicate<ItemStack, Integer> test : tests) {
			if(test.test(stack, dimension)) return true;
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
			if(test == TRUE) {
				xd.add(Objects.requireNonNull(item.getRegistryName()).toString());
			} else {
				xd.add(Objects.requireNonNull(item.getRegistryName()).toString() + CONDITIONS_SEPARATOR + test.toString());
			}
		});
		
		return xd.toArray(new String[0]);
	}
	
	public ItemList addFromStringArray(String[] arr) {
		if(arr.length == 0) return this;
		
		for(int line = 0; line < arr.length; line++) {
			String s = arr[line];
			
			String unparsedItemRes = s;
			ExtraConditions test = TRUE;
			
			//handle metadata cases
			if(s.contains(CONDITIONS_SEPARATOR)) {
				String[] split = unparsedItemRes.split(CONDITIONS_SEPARATOR);
				
				unparsedItemRes = split[0];
				
				try {
					test = new ExtraConditions();
					for(int i = 1; i < split.length; i++) {
						test.readSettingString(split[i]);
					}
				} catch(RuntimeException e) {
					throw new IllegalStateException("Problem parsing conditions on entry " + line, e);
				}
			}
			
			//parse item
			ResourceLocation itemRes;
			
			try {
				itemRes = new ResourceLocation(unparsedItemRes);
			} catch(RuntimeException e) {
				throw new IllegalStateException("Problem parsing item identifier on entry " + line, e);
			}
			
			if(ForgeRegistries.ITEMS.containsKey(itemRes)) {
				addItemWithPredicate(ForgeRegistries.ITEMS.getValue(itemRes), test);
			} else {
				BrokenWings.LOGGER.error("Unknown item " + unparsedItemRes + " in entry " + line + " in config file!");
			}
		}
		
		return this;
	}
	
	public static class ExtraConditions implements BiPredicate<ItemStack, Integer> {
		private boolean forceMeta = false;
		private InclusiveRange metaRange;
		
		private boolean forceDimension = false;
		private InclusiveRange dimensionRange;
		
		public ExtraConditions forceMeta(InclusiveRange metaRange) {
			this.forceMeta = true;
			this.metaRange = metaRange;
			return this;
		}
		
		public ExtraConditions forceDimension(InclusiveRange dimensionRange) {
			this.forceDimension = true;
			this.dimensionRange = dimensionRange;
			return this;
		}
		
		@Override
		public String toString() {
			if(!forceMeta && !forceDimension) return "";
			
			StringBuilder s = new StringBuilder();
			if(forceMeta) {
				s.append(META_CONDITION);
				s.append(metaRange.toString());
			}
			if(forceDimension) {
				if(forceMeta) s.append(CONDITIONS_SEPARATOR);
				s.append(DIMENSION_CONDITION);
				s.append(dimensionRange.toString());
			}
			return s.toString();
		}
		
		public void readSettingString(String setting) {
			CONDITION_REGEX.reset(setting);
			while(CONDITION_REGEX.find()) {
				String key = CONDITION_REGEX.group(1);
				InclusiveRange range = InclusiveRange.fromString(CONDITION_REGEX.group(2));
				
				if(META_CONDITION.equals(key)) forceMeta(range);
				else if(DIMENSION_CONDITION.equals(key)) forceDimension(range);
			}
		}
		
		@Override
		public boolean test(ItemStack stack, Integer dimension) {
			if(forceMeta && !metaRange.test(stack.getMetadata())) return false;
			if(forceDimension && !dimensionRange.test(dimension)) return false;
			return true;
		}
	}
	
	public static class InclusiveRange implements Predicate<Integer> {
		public InclusiveRange(int exact) {
			this(exact, exact);
		}
		
		public InclusiveRange(int bottom, int top) {
			this.bottom = bottom;
			this.top = top;
		}
		
		public static InclusiveRange fromString(String s) {
			try {
				if(s.contains("~")) {
					String[] split = s.split("~");
					return new InclusiveRange(Integer.valueOf(split[0]), Integer.valueOf(split[1])).fixOrder();
				} else {
					return new InclusiveRange(Integer.valueOf(s));
				}
			} catch(RuntimeException e) {
				throw new RuntimeException("Problem parsing range", e);
			}
		}
		
		private InclusiveRange fixOrder() {
			if(bottom > top) {
				int asdfghjkl = top;
				top = bottom;
				bottom = asdfghjkl;
			}
			return this;
		}
		
		public int bottom;
		public int top;
		
		@Override
		public String toString() {
			if(bottom == top) return String.valueOf(bottom);
			else return String.valueOf(bottom) + "~" + String.valueOf(top);
		}
		
		@Override
		public boolean test(Integer integer) {
			return (integer >= bottom) && (integer <= top);
		}
	}
}

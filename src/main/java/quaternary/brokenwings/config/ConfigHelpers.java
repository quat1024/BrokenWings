package quaternary.brokenwings.config;

import com.google.common.primitives.Ints;
import net.minecraftforge.common.config.Configuration;
import quaternary.brokenwings.BrokenWings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ConfigHelpers {
	static <T extends Enum> T getEnum(Configuration config, String configName, String configCategory, T defaultValue, String configDescription, Function<T, String> describerFunction, Class<T> enumClass) {
		T[] enumConstants = enumClass.getEnumConstants();
		
		//Do not touch
		String userProvidedString = config.getString(configName, configCategory, defaultValue.toString(), configDescription + "\n" + Arrays.stream(enumConstants).map(t -> "\"" + t.toString() + "\": " + describerFunction.apply(t)).reduce((one, two) -> one + '\n' + two).get() + '\n', Arrays.stream(enumConstants).map(T::toString).toArray(String[]::new));
		String userProvidedStringPatch = WingConfig.patchEnumLol(userProvidedString);
		
		Optional<T> userEnum = Arrays.stream(enumConstants).filter(t -> t.toString().equals(userProvidedStringPatch)).findAny();
		
		if(userEnum.isPresent()) return userEnum.get();
		else throw new IllegalArgumentException("\"" + userProvidedStringPatch + "\" is not a valid value for config option " + configName + "! See the config file for details");
	}
	
	static int[] getIntArray(Configuration config, String configName, String configCategory, int[] defaultsAsIntArray, String configComment) {
		//Wow I love shuffling around types!!!
		List<String> defaultsAsStringList = new ArrayList<>(defaultsAsIntArray.length);
		for(int d : defaultsAsIntArray) {
			defaultsAsStringList.add(String.valueOf(d));
		}
		
		String[] defaultsAsStringArray = defaultsAsStringList.toArray(new String[0]);
		
		String[] valuesAsStringArray = config.getStringList(configName, configCategory, defaultsAsStringArray, configComment);
		List<Integer> valuesAsIntList = new ArrayList<>();
		
		for(String value : valuesAsStringArray) {
			String valueT = value.trim();
			
			try {
				valuesAsIntList.add(Integer.valueOf(valueT));
			} catch (NumberFormatException e) {
				BrokenWings.LOGGER.error("Can't make sense of '{}' as an integer", valueT);
				BrokenWings.LOGGER.error("Please fix the config value named {}", configName);
			}
		}
		
		//Thank you guava for saving me from writing an ass loop
		return Ints.toArray(valuesAsIntList);
	}
	
	static ItemList getItemList(Configuration config, String configName, String configCategory, ItemList defaultEntries, String configComment) {
		String[] configEntry = config.getStringList(configName, configCategory, defaultEntries.toStringArray(), configComment + "\nSee readme on GitHub for examples of how to add extra metadata or dimension conditions");
		try {
			return new ItemList().addFromStringArray(configEntry);
		} catch(RuntimeException e) {
			throw new RuntimeException("Problem parsing config entry '" + configName + "':", e);
		}
	}
}

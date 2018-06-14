package quaternary.brokenwings.config;

import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ConfigHelpers {
	static <T extends Enum> T getEnum(Configuration config, String configName, String configCategory, T defaultValue, String configDescription, Function<T, String> describerFunction, Class<T> enumClass) {
		//FEAR MY TERRIBLE FUNCTIONAL BULLSHIT, HAHAHAAA
		//just pretend the inside of this method doesn't exist, because it's otherwise a great utility function
		
		T[] enumConstants = enumClass.getEnumConstants();
		
		String[] enumNames = Arrays.stream(enumConstants).map(T::toString).toArray(String[]::new);
		
		String configAndValueDescription = configDescription + "\n" + Arrays.stream(enumConstants).map(t -> "\"" + t.toString() + "\": " + describerFunction.apply(t)).reduce((one, two) -> one + '\n' + two).get() + '\n';
		
		String userProvidedString = config.getString(configName, configCategory, defaultValue.toString(), configAndValueDescription, enumNames);
		
		Optional<T> userEnum = Arrays.stream(enumConstants).filter(t -> t.toString().equals(userProvidedString)).findAny();
		
		if(userEnum.isPresent()) return userEnum.get();
		else throw new IllegalArgumentException("\"" + userProvidedString + "\" is not a valid value for config option " + configName + "! See the config file for details");
	}
	
	static int[] getIntArray(Configuration config, String configName, String configCategory, int[] defaultValue, String configComment) {
		String defaultValueString = intArrayToString(defaultValue);
		return parseIntArray(config.getString(configName, configCategory, defaultValueString, configComment), configName);
	}
	
	private static String intArrayToString(int[] arr) {
		//Slightly better than calling toString (no spaces or [ ] characters)
		StringBuilder bob = new StringBuilder();
		
		for(int i = 0; i < arr.length; i++) {
			bob.append(arr[i]);
			if(i != arr.length - 1) bob.append(',');
		}
		
		return bob.toString();
	}
	
	private static int[] parseIntArray(String arrString, @Nullable String configName) {
		try {
			String[] splitInput = arrString.split(",");
			List<Integer> intList = new ArrayList<>();
			
			for(String s : splitInput) {
				String trimmedString = s.trim();
				if(trimmedString.isEmpty()) continue;
				intList.add(Integer.parseInt(trimmedString));
			}
			
			//shoutouts to java primitive types being Fuckin garbage as usual
			int[] ret = new int[intList.size()];
			for(int i = 0; i < intList.size(); i++) {
				ret[i] = intList.get(i);
			}
			
			return ret;
			
		} catch (Exception e) {
			String message = "Can't make sense of \"" + arrString + "\" as an integer array";
			if(configName != null) message += ", please fix the config value named " + configName + "!";
			throw new IllegalArgumentException(message, e);
		}
	}
}

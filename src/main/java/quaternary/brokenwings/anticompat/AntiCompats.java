package quaternary.brokenwings.anticompat;

import java.util.*;

public class AntiCompats {
	private static final Set<AbstractAntiCompat> antiCompats = new HashSet<>();
	
	public static void init() {
		antiCompats.add(new ElytraAntiCompat());
		antiCompats.add(new CapIsFlyingAntiCompat());
	}
	
	public static Set<AbstractAntiCompat> get() {
		return Collections.unmodifiableSet(antiCompats);
	}
}

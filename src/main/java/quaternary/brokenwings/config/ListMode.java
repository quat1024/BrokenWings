package quaternary.brokenwings.config;

public enum ListMode {
	ALLOW_LIST,
	DENY_LIST,
	ALWAYS_DENY,
	ALWAYS_ALLOW;
	
	public boolean isFlightInDimensionBanned(int dimID) {
		if(this == ALWAYS_ALLOW) return false;
		if(this == ALWAYS_DENY) return true;
		
		boolean found = false;
		for(int i = 0; i < WingConfig.DIMENSION_LIST.length; i++) {
			if(dimID == WingConfig.DIMENSION_LIST[i]) {
				found = true;
				break;
			}
		}
		
		if(this == DENY_LIST) return found;
		else return !found; //whitelist		
	}
}

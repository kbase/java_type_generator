package us.kbase.kidl;

/**
 * Class defines details which is necessary in order to reproduce behavior of original perl parser.
 */
public class ObjectUsageInfo {
	private boolean stringScalarsUsedInTypedefs = false;
	
	public boolean isStringScalarsUsedInTypedefs() {
		return stringScalarsUsedInTypedefs;
	}
	
	public void setStringScalarsUsedInTypedefs(
			boolean stringScalarsUsedInTypedefs) {
		this.stringScalarsUsedInTypedefs = stringScalarsUsedInTypedefs;
	}
}

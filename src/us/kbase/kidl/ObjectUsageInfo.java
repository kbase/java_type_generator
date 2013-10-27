package us.kbase.kidl;

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

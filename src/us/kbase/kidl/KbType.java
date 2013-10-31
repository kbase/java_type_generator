package us.kbase.kidl;

/**
 * Predefined of artificial type. This could be either KbBasicType successor (unnamed type) 
 * or KbTypedef (named type). 
 * @author rsutormin
 */
public interface KbType {
	public String getName();
	public Object toJson(ObjectUsageInfo oui);
}

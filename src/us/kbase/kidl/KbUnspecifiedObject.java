package us.kbase.kidl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents unspecified object in spec-file.
 */
public class KbUnspecifiedObject extends KbBasicType {
	@Override
	public String getJavaStyleName() {
		return "UObject";
	}
	
	public String getSpecName() {
		return "UnspecifiedObject";
	}
	
	@Override
	public Object toJson(ObjectUsageInfo oui) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::UnspecifiedObject");
		return ret;
	}

	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "object");
		ret.put("original-type", "kidl-" + getSpecName());
		return ret;
	}
}

package us.kbase.kidl;

import java.util.Map;
import java.util.TreeMap;

public class KbUnspecifiedObject extends KbBasicType {
	@Override
	public String getJavaStyleName() {
		return "UObject";
	}
	
	@Override
	public String getName() {
		return "UnspecifiedObject";
	}
	
	@Override
	public Object toJson(ObjectUsageInfo oui) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::UnspecifiedObject");
		return ret;
	}
}

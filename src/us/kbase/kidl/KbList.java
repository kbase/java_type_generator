package us.kbase.kidl;

import java.util.Map;
import java.util.TreeMap;

public class KbList extends KbBasicType {
	private KbType elementType;
	
	public KbList() {}
	
	public KbList(KbType elementType) {
		this.elementType = elementType;
	}
	
	public KbList loadFromMap(Map<?,?> data) throws KidlParseException {
		elementType = Utils.createTypeFromMap(Utils.propMap(data, "element_type"));
		return this;
	}
	
	public KbType getElementType() {
		return elementType;
	}
	
	@Override
	public String getJavaStyleName() {
		return "List";
	}
	
	@Override
	public String getName() {
		throw new IllegalStateException("Method getName() is not supported for list");
	}
	
	@Override
	public Object toJson(ObjectUsageInfo oui) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::List");
		ret.put("element_type", elementType.toJson(oui));
		return ret;
	}
}

package us.kbase.kidl;

import java.util.Map;

public class KbList extends KbBasicType {
	private KbType elementType;
	
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
}

package us.kbase.scripts;

import java.util.Map;

public class KbList extends KbBasicType {
	private KbType elementType;
	
	public KbList loadFromMap(Map<?,?> data, JSyncProcessor subst) {
		elementType = Utils.createTypeFromMap(Utils.propMap(data, "element_type", subst), subst);
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

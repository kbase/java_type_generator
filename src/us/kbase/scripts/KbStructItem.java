package us.kbase.scripts;

import java.util.Map;

public class KbStructItem {
	private String name;
	private boolean nullable;
	private KbType itemType;
	
	public KbStructItem loadFromMap(Map<?,?> data, JSyncProcessor subst) {
		name = Utils.prop(data, "name");
		nullable = (0 != Utils.intPropFromString(data, "nullable"));
		itemType = Utils.createTypeFromMap(Utils.propMap(data, "item_type", subst), subst);
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public KbType getItemType() {
		return itemType;
	}
}

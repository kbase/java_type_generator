package us.kbase.kidl;

import java.util.Map;
import java.util.Set;

public class KbStructItem {
	private String name;
	private boolean nullable;
	private KbType itemType;
	private boolean optional;
	
	public KbStructItem loadFromMap(Map<?,?> data, Set<String> optionalFields) throws KidlParseException {
		name = Utils.prop(data, "name");
		nullable = (0 != Utils.intPropFromString(data, "nullable"));
		itemType = Utils.createTypeFromMap(Utils.propMap(data, "item_type"));
		optional = optionalFields.contains(name);
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
	
	public boolean isOptional() {
		return optional;
	}
}

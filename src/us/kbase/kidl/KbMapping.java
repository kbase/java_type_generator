package us.kbase.kidl;

import java.util.Map;

public class KbMapping extends KbBasicType {
	private KbType keyType;
	private KbType valueType;
	
	public KbMapping loadFromMap(Map<?,?> data) {
		keyType = Utils.createTypeFromMap(Utils.propMap(data, "key_type"));
		valueType = Utils.createTypeFromMap(Utils.propMap(data, "value_type"));
		return this;
	}
	
	public KbType getKeyType() {
		return keyType;
	}
	
	public KbType getValueType() {
		return valueType;
	}
	
	@Override
	public String getJavaStyleName() {
		return "Map";
	}
}

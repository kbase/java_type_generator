package gov.doe.kbase.scripts;

import java.util.Map;

public class KbMapping extends KbBasicType {
	private KbType keyType;
	private KbType valueType;
	
	public KbMapping loadFromMap(Map<?,?> data, JSyncProcessor subst) {
		keyType = Utils.createTypeFromMap(Utils.propMap(data, "key_type", subst), subst);
		valueType = Utils.createTypeFromMap(Utils.propMap(data, "value_type", subst), subst);
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

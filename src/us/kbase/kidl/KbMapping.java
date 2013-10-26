package us.kbase.kidl;

import java.util.Map;
import java.util.TreeMap;

public class KbMapping extends KbBasicType {
	private KbType keyType;
	private KbType valueType;
	
	public KbMapping() {}
	
	public KbMapping(KbType keyType, KbType valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}
	
	public KbMapping loadFromMap(Map<?,?> data) throws KidlParseException {
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
	
	@Override
	public String getName() {
		throw new IllegalStateException("Method getName() is not supported for mapping");
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Mapping");
		ret.put("key_type", keyType.toJson());
		ret.put("value_type", valueType.toJson());
		return ret;
	}
}

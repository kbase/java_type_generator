package us.kbase.kidl;

import java.util.Map;
import java.util.TreeMap;

/**
 * Input or output part of KbFuncdef.
 * @author rsutormin
 */
public class KbParameter {
	private String name;
	private KbType type;
	
	public KbParameter() {}
	
	public KbParameter(KbType type, String name) {
		this.name = name;
		this.type = type;
	}
	
	public KbParameter loadFromMap(Map<?,?> data, boolean isReturn, int paramNum) throws KidlParseException {
		name = Utils.propOrNull(data, "name"); // Utils.prop(data, "name");
		if (name == null && !isReturn) {
			name = "arg" + paramNum;
		}
		type = Utils.createTypeFromMap(Utils.propMap(data, "type"));
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public KbType getType() {
		return type;
	}
	
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (name != null)
			ret.put("name", name);
		ret.put("type", type.toJson());
		return ret;
	}
}

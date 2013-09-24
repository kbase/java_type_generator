package us.kbase.kidl;

import java.util.Map;

/**
 * Input or output part of KbFuncdef.
 * @author rsutormin
 */
public class KbParameter {
	private String name;
	private KbType type;
	
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
}

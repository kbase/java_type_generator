package us.kbase.kidl;

import java.util.LinkedHashMap;
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

    public Map<String, Object> forTemplates() {
        Map<String, Object> ret = new LinkedHashMap<String, Object>();
        ret.put("name", name);
        String validator = null;
        KbType t = type;
        while (t instanceof KbTypedef) {
            t = ((KbTypedef)t).getAliasType();
        }
        if (t instanceof KbMapping || t instanceof KbStruct) {
            validator = "ref($" + name + ") eq 'HASH'";
        } else if (t instanceof KbList || t instanceof KbTuple) {
            validator = "ref($" + name + ") eq 'ARRAY'";
        } else if (t instanceof KbUnspecifiedObject) {
            validator = "defined $" + name;
        } else {
            validator = "!ref($" + name + ")";
        }
        ret.put("validator", validator);
        ret.put("perl_var", "$" + name);
        return ret;
    }
}

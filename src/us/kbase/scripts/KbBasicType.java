package us.kbase.scripts;

import java.util.Map;

/**
 * Unnamed standard type. This could be one of {KbScalar, KbStruct, KbList, KbTuple, KbMapping or KbUnspecifiedObject}.
 * @author rsutormin
 */
public abstract class KbBasicType implements KbType {
	
	public static KbBasicType createFromMap(Map<?,?> data, JSyncProcessor subst) {
		String typeName = Utils.getPerlSimpleType(data);
		if (typeName.equals("Scalar")) {
			return new KbScalar().loadFromMap(data);
		} else if (typeName.equals("List")) {
			return new KbList().loadFromMap(data, subst);
		} else if (typeName.equals("Struct")) {
			return new KbStruct().loadFromMap(data, subst);
		} else if (typeName.equals("Tuple")) {
			return new KbTuple().loadFromMap(data, subst);
		} else if (typeName.equals("Mapping")) {
			return new KbMapping().loadFromMap(data, subst);
		} else if (typeName.equals("UnspecifiedObject")) {
			return new KbUnspecifiedObject();
		}
		throw new IllegalStateException("Unsupported type: " + typeName);
	}
	
	public abstract String getJavaStyleName();
}

package gov.doe.kbase.scripts;

import java.util.Map;

public class KbScalar extends KbBasicType {
	public enum Type {
		intType, stringType, floatType;
	}
	
	private Type scalarType;
	
	public KbScalar loadFromMap(Map<?,?> data) {
		scalarType = Type.valueOf(Utils.prop(data, "scalar_type") + "Type");
		return this;
	}
	
	public Type getScalarType() {
		return scalarType;
	}
	
	@Override
	public String getJavaStyleName() {
		switch (scalarType) {
			case stringType: return "String";
			case intType: return "Integer";
			case floatType: return "Double";
			default: throw new IllegalStateException("Unknown scalar type: " + scalarType);
		}
	}

	public String getFullJavaStyleName() {
		return "java.lang." + getJavaStyleName();
	}

	public String getJsonStyleName() {
		switch (scalarType) {
			case stringType: return "string";
			case intType: return "integer";
			case floatType: return "number";
			default: throw new IllegalStateException("Unknown scalar type: " + scalarType);
		}
	}
}

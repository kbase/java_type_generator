package us.kbase.kidl;

import java.util.Map;
import java.util.Set;

public class KbScalar extends KbBasicType {
	public enum Type {
		intType, stringType, floatType, boolType;
	}
	
	private Type scalarType;
	private String javaStyleType;
	private String jsonStyleType;
	private Set<String> idReferences;
	
	public KbScalar loadFromMap(Map<?,?> data) throws KidlParseException {
		scalarType = Type.valueOf(Utils.prop(data, "scalar_type") + "Type");
		javaStyleType = getJavaStyleType();
		jsonStyleType = getJsonStyleType();
		if (data.containsKey("annotations"))
			idReferences = new KbAnnotations().loadFromMap(
					Utils.propMap(data, "annotations")).getIdReferences();
		return this;
	}
	
	public Type getScalarType() {
		return scalarType;
	}
	
	@Override
	public String getJavaStyleName() {
		return javaStyleType;
	}
	
	private String getJavaStyleType() throws KidlParseException {
		switch (scalarType) {
			case stringType: return "String";
			case intType: return "Integer";
			case floatType: return "Double";
			case boolType : return "Boolean";
			default: throw new KidlParseException("Unknown scalar type: " + scalarType);
		}
	}

	public String getFullJavaStyleName() throws KidlParseException {
		return "java.lang." + getJavaStyleName();
	}

	public String getJsonStyleName() {
		return jsonStyleType;
	}
	
	private String getJsonStyleType() throws KidlParseException {
		switch (scalarType) {
			case stringType: return "string";
			case intType: return "integer";
			case floatType: return "number";
			case boolType: return "boolean";
			default: throw new KidlParseException("Unknown scalar type: " + scalarType);
		}
	}
	
	public Set<String> getIdReferences() {
		return idReferences;
	}
}

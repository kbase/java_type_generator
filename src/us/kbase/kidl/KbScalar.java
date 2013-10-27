package us.kbase.kidl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class KbScalar extends KbBasicType {
	public enum Type {
		intType, stringType, floatType, boolType;
	}
	
	private Type scalarType;
	private String javaStyleType;
	private String jsonStyleType;
	private Set<String> idReferences;
	
	public KbScalar() {}
	
	public KbScalar(String scalarType) {
		this.scalarType = Type.valueOf(scalarType + "Type");
		javaStyleType = getJavaStyleType();
		jsonStyleType = getJsonStyleType();
	}
	
	public KbScalar loadFromMap(Map<?,?> data, KbAnnotations annFromTypeDef) throws KidlParseException {
		scalarType = Type.valueOf(Utils.prop(data, "scalar_type") + "Type");
		javaStyleType = getJavaStyleType();
		jsonStyleType = getJsonStyleType();
		KbAnnotations ann = null;
		if (data.containsKey("annotations")) 
			ann = new KbAnnotations().loadFromMap(Utils.propMap(data, "annotations"));
		if (ann == null)
			ann = annFromTypeDef;
		utilizeAnnotations(ann);
		return this;
	}

	void utilizeAnnotations(KbAnnotations ann) {
		if (ann != null)
			idReferences = ann.getIdReferences() == null ? null : 
				new LinkedHashSet<String>(ann.getIdReferences());
	}
	
	public Type getScalarType() {
		return scalarType;
	}
	
	@Override
	public String getName() {
		String ret = scalarType.toString();
		return ret.substring(0, ret.length() - 4);
	}
	
	@Override
	public String getJavaStyleName() {
		return javaStyleType;
	}
	
	private String getJavaStyleType() {
		switch (scalarType) {
			case stringType: return "String";
			case intType: return "Long";
			case floatType: return "Double";
			case boolType : return "Boolean";
			default: throw new IllegalStateException("Unknown scalar type: " + scalarType);
		}
	}

	public String getFullJavaStyleName() throws KidlParseException {
		return "java.lang." + getJavaStyleName();
	}

	public String getJsonStyleName() {
		return jsonStyleType;
	}
	
	private String getJsonStyleType() {
		switch (scalarType) {
			case stringType: return "string";
			case intType: return "integer";
			case floatType: return "number";
			case boolType: return "boolean";
			default: throw new IllegalStateException("Unknown scalar type: " + scalarType);
		}
	}
	
	public Set<String> getIdReferences() {
		return idReferences;
	}
	
	@Override
	public Object toJson(ObjectUsageInfo oui) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Scalar");
		if (scalarType == Type.stringType && oui.isStringScalarsUsedInTypedefs())
			ret.put("annotations", new HashMap<String, Object>());
		ret.put("scalar_type", getName());
		return ret;
	}
}

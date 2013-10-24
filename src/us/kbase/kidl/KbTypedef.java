package us.kbase.kidl;

import java.util.Map;

/**
 * Named (artificial) type.
 * @author rsutormin
 */
public class KbTypedef implements KbModuleComp, KbType {
	private String name;
	private String module;
	private KbType aliasType;
	private String comment;
	private Map<?,?> data = null;
	private KbAnnotations annotations;
	
	public KbTypedef loadFromMap(Map<?,?> data) throws KidlParseException {
		name = Utils.prop(data, "name");
		module = Utils.prop(data, "module");
		comment = Utils.prop(data, "comment");
		annotations = new KbAnnotations();
		if (data.containsKey("annotations"))
			annotations.loadFromMap(Utils.propMap(data, "annotations"));
		aliasType = Utils.createTypeFromMap(Utils.propMap(data, "alias_type"), annotations);
		this.data = data;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getModule() {
		return module;
	}
	
	public String getComment() {
		return comment;
	}
	
	public KbType getAliasType() {
		return aliasType;
	}
	
	public Map<?, ?> getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return module + "." + name;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public KbAnnotations getAnnotations() {
		return annotations;
	}
}

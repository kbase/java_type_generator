package us.kbase.kidl;

import java.util.Map;
import java.util.TreeMap;

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
	
	public KbTypedef() {}
	
	public KbTypedef(String module, String name, KbType aliasType, String comment) throws KidlParseException {
		this.module = module;
		this.name = name;
		this.aliasType = aliasType;
		this.comment = comment == null ? "" : comment;
		this.annotations = new KbAnnotations().loadFromComment(this.comment, this);
		if (aliasType instanceof KbScalar) {
			((KbScalar)aliasType).utilizeAnnotations(annotations);
		} else if (aliasType instanceof KbTuple) {
			((KbTuple)aliasType).setComment(this.comment);
			((KbTuple)aliasType).setName(this.name);
		} else if (aliasType instanceof KbStruct) {
			((KbStruct)aliasType).setComment(this.comment);
			((KbStruct)aliasType).setModule(this.module);
			((KbStruct)aliasType).setName(this.name);
			((KbStruct)aliasType).utilizeAnnotations(annotations);
		}
	}
	
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
	
	@Override
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
	
	@Override
	public Object toJson(ObjectUsageInfo oui) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Typedef");
		ret.put("alias_type", aliasType.toJson(oui));
		ret.put("annotations", annotations.toJson(true));
		ret.put("comment", comment);
		ret.put("module", module);
		ret.put("name", name);
		return ret;
	}
}

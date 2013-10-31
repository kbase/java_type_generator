package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class KbStruct extends KbBasicType {
	private String name;
	private KbAnnotations annotations;
	private List<KbStructItem> items;
	private String comment;
	private String module;
	
	public KbStruct() {
		items = new ArrayList<KbStructItem>();
	}
	
	public KbStruct loadFromMap(Map<?,?> data, KbAnnotations annFromTypeDef) throws KidlParseException {
		name = Utils.prop(data, "name");
		items = new ArrayList<KbStructItem>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "items"))
			items.add(new KbStructItem().loadFromMap(itemProps));
		utilizeAnnotations(annFromTypeDef);
		items = Collections.unmodifiableList(items);
		return this;
	}
	
	void utilizeAnnotations(KbAnnotations ann) throws KidlParseException {
		annotations = ann;
		Set<String> optional = ann == null || ann.getOptional() == null ? null : 
			new LinkedHashSet<String>(ann.getOptional());
		for (KbStructItem item : items)
			item.utilizeAnnotation(optional);
		if (optional != null && optional.size() > 0)
			throw new KidlParseException("Can not find field(s) from optional annotation in structure: " + 
					optional);
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<KbStructItem> getItems() {
		return items;
	}
	
	@Override
	public String getJavaStyleName() {
		return "Struct";
	}
	
	public KbAnnotations getAnnotations() {
		return annotations;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getModule() {
		return module;
	}
	
	public void setModule(String module) {
		this.module = module;
	}
	
	@Override
	public Object toJson(ObjectUsageInfo oui) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Struct");
		if (annotations == null || annotations.getSearchable() == null) {
			Map<String, Object> ann = new HashMap<String, Object>();
			ann.put("searchable_ws_subset", new HashMap<String, Object>());
			ret.put("annotations", ann);
		}
		if (comment != null && comment.length() > 0)
			ret.put("comment", comment);
		List<Object> itemList = new ArrayList<Object>();
		for (KbStructItem item : items)
			itemList.add(item.toJson(oui));
		ret.put("items", itemList);
		if (module != null)
			ret.put("module", module);
		if (name != null)
			ret.put("name", name);
		return ret;
	}
}

package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
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
		annotations = annFromTypeDef;
		Set<String> optional = annotations == null ? null : annotations.getOptional();
		items = new ArrayList<KbStructItem>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "items")) {
			items.add(new KbStructItem().loadFromMap(itemProps, optional));
		}
		items = Collections.unmodifiableList(items);
		return this;
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
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Struct");
		if (comment != null)
			ret.put("comment", comment);
		List<Object> itemList = new ArrayList<Object>();
		for (KbStructItem item : items)
			itemList.add(item.toJson());
		ret.put("items", itemList);
		if (module != null)
			ret.put("module", module);
		if (name != null)
			ret.put("name", name);
		return ret;
	}
}

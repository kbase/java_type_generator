package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KbStruct extends KbBasicType {
	private String name;
	private KbAnnotations annotations;
	private List<KbStructItem> items;
	
	public KbStruct loadFromMap(Map<?,?> data) throws KidlParseException {
		name = Utils.prop(data, "name");
		annotations = new KbAnnotations();
		if (data.containsKey("annotations"))
			annotations.loadFromMap(Utils.propMap(data, "annotations"));
		Set<String> optional = annotations.getOptional();
		items = new ArrayList<KbStructItem>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "items")) {
			items.add(new KbStructItem().loadFromMap(itemProps, optional));
		}
		items = Collections.unmodifiableList(items);
		return this;
	}
	
	public String getName() {
		return name;
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
}

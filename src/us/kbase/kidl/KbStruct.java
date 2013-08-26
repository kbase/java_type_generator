package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KbStruct extends KbBasicType {
	private String name;
	private List<KbStructItem> items;
	
	public KbStruct loadFromMap(Map<?,?> data) {
		name = Utils.prop(data, "name");
		items = new ArrayList<KbStructItem>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "items")) {
			items.add(new KbStructItem().loadFromMap(itemProps));
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
}

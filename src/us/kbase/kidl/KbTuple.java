package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents tuple in spec-file.
 */
public class KbTuple extends KbBasicType {
	private List<String> elementNames = new ArrayList<String>();
	private List<KbType> elementTypes = new ArrayList<KbType>();
	private String name = null;
	private String comment = null;
	
	public KbTuple() {}
	
	public KbTuple(List<KbType> types) {
		elementNames = new ArrayList<String>();
		for (KbType type : types)
			elementNames.add(null);
		elementNames = Collections.unmodifiableList(elementNames);
		elementTypes = Collections.unmodifiableList(types);
	}

	public void addElement(KbParameter elem) {
		addElement(elem.getType(), elem.getName());
	}
	
	public void addElement(KbType type, String name) {
		if (name == null)
			name = "e_" + (elementNames.size() + 1);
		elementTypes.add(type);
		elementNames.add(name);
	}
	
	public KbTuple loadFromMap(Map<?,?> data) throws KidlParseException {
		List<?> optionList = Utils.propList(data, "element_names");
		elementNames = Collections.unmodifiableList(Utils.repareTypingString(optionList));
		elementTypes = new ArrayList<KbType>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "element_types")) {
			elementTypes.add(Utils.createTypeFromMap(itemProps));
		}
		elementTypes = Collections.unmodifiableList(elementTypes);
		return this;
	}
		
	public List<String> getElementNames() {
		return elementNames;
	}
	
	public List<KbType> getElementTypes() {
		return elementTypes;
	}
	
	@Override
	public String getJavaStyleName() {
		return "Tuple";
	}

	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getName() {
		if (name == null)
			throw new IllegalStateException("Property name was not set for tuple");
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Tuple");
		if (comment != null && comment.length() > 0)
			ret.put("comment", comment);
		ret.put("element_names", elementNames);
		List<Object> elementTypeList = new ArrayList<Object>();
		for (KbType type : elementTypes)
			elementTypeList.add(type.toJson());
		ret.put("element_types", elementTypeList);
		if (name != null)
			ret.put("name", name);
		return ret;
	}

	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "array");
		ret.put("original-type", "kidl-tuple");
		ret.put("maxItems", getElementTypes().size());
		ret.put("minItems", getElementTypes().size());
		List<Object> items = new ArrayList<Object>();
		for (KbType iType : getElementTypes())
			items.add(iType.toJsonSchema(true));
		ret.put("items", items);
		return ret;
	}
}

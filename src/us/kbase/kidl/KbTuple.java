package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KbTuple extends KbBasicType {
	private List<String> elementNames = null;
	private List<KbType> elementTypes = null;
	
	public KbTuple() {}
	
	public KbTuple(List<KbType> types) {
		elementNames = new ArrayList<String>();
		for (KbType type : types)
			elementNames.add(null);
		elementNames = Collections.unmodifiableList(elementNames);
		elementTypes = Collections.unmodifiableList(types);
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
}

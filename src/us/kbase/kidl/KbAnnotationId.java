package us.kbase.kidl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents kind of comment annotation called 'id'.
 */
public class KbAnnotationId {
	private String type;
	private List<String> sources;
	private List<String> validTypedefNames;
	
	public static final String TYPE_WS = "ws";
	public static final String TYPE_KB = "kb";
	public static final String TYPE_EXTERNAL = "external";
	public static final String TYPE_SHOCK = "shock";

	void loadFromComment(List<String> words) throws KidlParseException {
		if (words.size() == 0)
			throw new KidlParseException("Id annotations without type are not supported");
		type = words.get(0);
		words = words.subList(1, words.size());
		if (type.equals(TYPE_WS)) {
			validTypedefNames = words;
		} else if (type.equals(TYPE_EXTERNAL)) {
			sources = words;
		}		
	}
	
	void loadFromMap(Map<String,Object> data) throws KidlParseException {
		type = Utils.prop(data, "type");
		if (type.equals(TYPE_WS)) {
			validTypedefNames = Collections.unmodifiableList(Utils.repareTypingString(
					Utils.propList(data, "valid_typedef_names")));
		} else if (type.equals(TYPE_EXTERNAL)) {
			sources = Collections.unmodifiableList(Utils.repareTypingString(
					Utils.propList(data, "sources")));
		}
	}
	
	public String getType() {
		return type;
	}
	
	public List<String> getSourcesForExternal() {
		return sources;
	}
	
	public List<String> getValidTypedefNamesForWs() {
		return validTypedefNames;
	}
	
	Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("type", type);
		if (type.equals(TYPE_WS)) {
			ret.put("valid_typedef_names", validTypedefNames);
		} else if (type.equals(TYPE_EXTERNAL)) {
			ret.put("sources", sources);
		}
		return ret;
	}

	Object toJsonSchema() {
		Map<String, Object> idMap = new TreeMap<String, Object>();
		idMap.put("id-type", type);
		if (type.equals(TYPE_WS)) {
			idMap.put("valid-typedef-names", getValidTypedefNamesForWs());
		} else if (type.equals(TYPE_EXTERNAL)) {
			idMap.put("sources", getSourcesForExternal());
		}
		return idMap;
	}
}

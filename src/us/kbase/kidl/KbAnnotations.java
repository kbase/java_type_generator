package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class KbAnnotations {
	private List<String> optional = null;
	private List<String> idReferences = null;
	private Map<String, Object> unknown = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	KbAnnotations loadFromMap(Map<?,?> data) throws KidlParseException {
		for (Map.Entry<?, ?> enrty : data.entrySet()) {
			String key = enrty.getKey().toString();
			if (key.equals("optional")) {
				optional = (List<String>)enrty.getValue();
			} else if (key.equals("id_reference")) {
				idReferences = (List<String>)enrty.getValue();
			} else if (key.equals("unknown_annotations")) {
				unknown.putAll((Map<String, Object>)enrty.getValue());
			} else {
				//throw new KidlParseException("Unknown type of annotation: " + key);
				if (!unknown.containsKey(key))
					unknown.put(key, enrty.getValue());
			}
		}
		if (optional != null)
			optional = Collections.unmodifiableList(optional);
		if (idReferences != null)
			idReferences = Collections.unmodifiableList(idReferences);
		unknown = Collections.unmodifiableMap(unknown);
		return this;
	}
	
	public KbAnnotations loadFromComment(String comment) {
		List<List<String>> lines = new ArrayList<List<String>>();
		StringTokenizer st = new StringTokenizer(comment, "\r\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(line, " \t");
			List<String> words = new ArrayList<String>();
			while (st2.hasMoreTokens())
				words.add(st2.nextToken());
			lines.add(words);
		}
		for (int pos = 0; pos < lines.size(); pos++) {
			if (lines.get(pos).size() == 0)
				continue;
			List<String> words = lines.get(pos);
			String annType = words.get(0);
			if (!annType.startsWith("@"))
				continue;
			annType = annType.substring(1);
			List<String> value = words.subList(1, words.size());
			if (annType.equals("optional")) {
				optional = value;
			} else if (annType.equals("id_reference")) {
				idReferences = value;
			} else {
				unknown.put(annType, value);
			}
		}
		return this;
	}
	
	public List<String> getOptional() {
		return optional;
	}
	
	public List<String> getIdReferences() {
		return idReferences;
	}
	
	public Map<String, Object> getUnknown() {
		return unknown;
	}
	
	public Object toJson(boolean searchable) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (optional != null)
			ret.put("optional", new ArrayList<String>(optional));
		if (idReferences != null)
			ret.put("id_reference", new ArrayList<String>(idReferences));
		if (searchable)
			ret.put("searchable_ws_subset", new HashMap<String, Object>());
		ret.put("unknown_annotations", unknown);
		return ret;
	}
}

package us.kbase.kidl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KbAnnotations {
	private Set<String> optional = new LinkedHashSet<String>();
	private Set<String> idReferences = null;
	private Map<String, Object> unknown = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	KbAnnotations loadFromMap(Map<?,?> data) throws KidlParseException {
		for (Map.Entry<?, ?> enrty : data.entrySet()) {
			String key = enrty.getKey().toString();
			if (key.equals("optional")) {
				optional.addAll((List<String>)enrty.getValue());
			} else if (key.equals("id_reference")) {
				idReferences = new LinkedHashSet<String>();
				idReferences.addAll((List<String>)enrty.getValue());
			} else if (key.equals("unknown_annotations")) {
				unknown.putAll((Map<String, Object>)enrty.getValue());
			} else {
				//throw new KidlParseException("Unknown type of annotation: " + key);
				if (!unknown.containsKey(key))
					unknown.put(key, enrty.getValue());
			}
		}
		optional = Collections.unmodifiableSet(optional);
		if (idReferences != null)
			idReferences = Collections.unmodifiableSet(idReferences);
		unknown = Collections.unmodifiableMap(unknown);
		return this;
	}
	
	public Set<String> getOptional() {
		return optional;
	}
	
	public Set<String> getIdReferences() {
		return idReferences;
	}
	
	public Map<String, Object> getUnknown() {
		return unknown;
	}
}

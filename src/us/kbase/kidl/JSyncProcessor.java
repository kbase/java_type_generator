package us.kbase.kidl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JSyncProcessor {
	private Map<Integer, Object> refs = new TreeMap<Integer, Object>();

	public JSyncProcessor(Object obj) {
		process(obj);
	}
	
	private void process(Object obj) {
		if (obj instanceof List) {
			List<?> list = (List<?>)obj;
			for (Object item : list) {
				process(item);
			}
		} else if (obj instanceof Map) {
			Map<Object,Object> map = (Map)obj;
			if (map.containsKey("&")) {
				String address = map.get("&").toString();
				refs.put(Integer.parseInt(address), obj);
				map.remove("&");
			}
			Set<?> keys = map.keySet();
			for (Object key : keys) {
				Object value = map.get(key);
				process(value);
			}
		}
	}
	
	public void checkMapProp(Map map, String prop) {
		Object ref = findRef(map.get(prop));
		if (ref != null)
			map.put(prop, ref);
	}

	private Object findRef(Object value) {
		if (value != null && value instanceof String && ((String)value).startsWith("*")) {
			try {
				int refAddress = Integer.parseInt(((String)value).substring(1));
				if (refs.containsKey(refAddress)) {
					return refs.get(refAddress);
				}
			} catch (NumberFormatException ignore) {}
		}
		return null;
	}
	
	public void checkListItems(List list) {
		for (int i = 0; i < list.size(); i++) {
			Object ref = findRef(list.get(i));
			if (ref != null)
				list.set(i, ref);
		}
	}
}

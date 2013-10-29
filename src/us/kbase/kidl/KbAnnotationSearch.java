package us.kbase.kidl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KbAnnotationSearch {
	private Subset fields;  //= new Subset();
	private Subset keys;  //= new Subset();
	
	public KbAnnotationSearch() {}
	
	@SuppressWarnings("unchecked")
	void loadFromMap(Map<String, Object> data) {
		Map<String, Object> map1 = (Map<String, Object>)data.get("fields");
		if (map1 != null)
			fields = new Subset().loadFromMap(map1);
		Map<String, Object> map2 = (Map<String, Object>)data.get("keys");
		if (map2 != null)
			keys = new Subset().loadFromMap(map2);
	}
	
	KbAnnotationSearch loadFromComment(List<String> words, KbTypedef caller) {
		if (words.size() == 0 || !words.get(0).equals("ws_subset"))
			return this;
		if (!(caller.getAliasType() instanceof KbStruct))
			return this;
		KbStruct type = (KbStruct)caller.getAliasType();
		words = new ArrayList<String>(words.subList(1, words.size()));
		for (int i = 1; i < words.size();) {
			String w1 = words.get(i - 1);
			String w2 = words.get(i);
			boolean glue = (w1.equals("keys_of") && w2.startsWith("(")) || w1.endsWith("(") || 
					w2.equals(")") || w1.endsWith(".") || w1.endsWith(",") || w2.startsWith(".") || 
					w2.startsWith(",");
			if (glue) {
				String w = words.remove(i);
				words.set(i - 1, words.get(i - 1) + w);
			} else {
				i++;
			}
		}
		for (String word : words) {
			if (word.startsWith("keys_of(") && word.endsWith(")")) {
				if (keys == null)
					keys = new Subset();
				keys.loadFromComment(word.substring(8, word.length() - 1), type, true);
			} else {
				if (fields == null)
					fields = new Subset();
				fields.loadFromComment(word, type, false);
			}
		}
		return this;
	}
	
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (fields != null)
			ret.put("fields", fields.toJson());
		if (keys != null)
			ret.put("keys", keys.toJson());
		return ret;
	}

	public Object toJsonSchema() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (fields != null)
			ret.put("fields", fields.toJsonSchema());
		if (keys != null)
			ret.put("keys", keys.toJsonSchema());
		return ret;
	}

	@SuppressWarnings("serial")
	public static class Subset extends TreeMap<String, Subset> {
		private boolean isMapping = false;
		
		@SuppressWarnings("unchecked")
		public Subset loadFromMap(Map<String, Object> data) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				Subset value = new Subset();
				value.loadFromMap((Map<String, Object>)entry.getValue());
				put(entry.getKey(), value);
			}
			return this;
		}
		
		public Object toJson() {
			if (isMapping)
				return "";
			Map<String, Object> ret = new TreeMap<String, Object>();
			for (Map.Entry<String, Subset> entry : entrySet()) {
				ret.put(entry.getKey(), entry.getValue().toJson());
			}
			return ret;
		}

		public Object toJsonSchema() {
			Map<String, Object> ret = new TreeMap<String, Object>();
			for (Map.Entry<String, Subset> entry : entrySet()) {
				ret.put(entry.getKey(), entry.getValue().toJsonSchema());
			}
			return ret;
		}

		void loadFromComment(String text, KbStruct type, boolean forKeys) {
			loadFromComment(text, type, forKeys, false);
		}
		
		void loadFromComment(String text, KbStruct type, boolean forKeys, boolean internal) {
			List<String> commaParts = new ArrayList<String>();
			while (true) {
				int commaPos = indexOfRoundBrackets(text, ',');
				if (commaPos > 0) {
					commaParts.add(text.substring(0, commaPos));
					text = text.substring(commaPos + 1);
				} else {
					commaParts.add(text);
					break;
				}
			}
			for (String commaPart : commaParts) {
				int dotPos = indexOfRoundBrackets(commaPart, '.');
				String key = dotPos > 0 ? commaPart.substring(0, dotPos) : commaPart;
				KbType itemType = null;
				for (KbStructItem item : type.getItems()) {
					if (item.getName().equals(key)) {
						itemType = resolveTypedefs(item.getItemType());
						break;
					}
				}
				if (itemType == null)
					continue;
				if (itemType instanceof KbMapping)
					key = "mapping." + key;
				Subset value = get(key);
				if (value == null)
					value = new Subset();
				if (dotPos > 0) {
					String leftPart = commaPart.substring(dotPos + 1);
					if (leftPart.startsWith("(") && leftPart.endsWith(")"))
						leftPart = leftPart.substring(1, leftPart.length() - 1);
					KbStruct inner = digInto(itemType);
					if (inner == null)
						continue;
					value.loadFromComment(leftPart, inner, forKeys, true);
				} else if (itemType instanceof KbMapping) {
					value.isMapping = (!forKeys) || (!internal);
				}
				put(key, value);
			}
		}
		
		private KbType resolveTypedefs(KbType type) {
			if (type instanceof KbTypedef) 
				return resolveTypedefs(((KbTypedef)type).getAliasType());
			return type;
		}
		
		private KbStruct digInto(KbType type) {
			type = resolveTypedefs(type);
			if (type instanceof KbStruct)
				return (KbStruct)type;
			if (type instanceof KbList)
				return digInto(((KbList)type).getElementType());
			if (type instanceof KbMapping)
				return digInto(((KbMapping)type).getValueType());
			return null;
		}
		
		private int indexOfRoundBrackets(String text, char ch) {
			int level = 0;
			for (int pos = 0; pos < text.length(); pos++) {
				if (text.charAt(pos) == '(') {
					level++;
				} else if (text.charAt(pos) == ')') {
					level--;
				} else if (level == 0 && text.charAt(pos) == ch) {
					return pos;
				}
			}
			return -1;
		}
	}
}

package us.kbase.kidl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Utils {

	public static String prop(Map<?,?> map, String propName) {
		return propAbstract(map, propName, String.class);
	}

	public static String propOrNull(Map<?,?> map, String propName) {
		if (map.get(propName) == null)
			return null;
		return prop(map, propName);
	}

	public static List<?> propList(Map<?,?> map, String propName) {
		return propAbstract(map, propName, List.class);
	}

	public static Map<?,?> propMap(Map<?,?> map, String propName, JSyncProcessor subst) {
		subst.checkMapProp(map, propName);
		return propAbstract(map, propName, Map.class);
	}
	
	private static <T> T propAbstract(Map<?,?> map, String propName, Class<T> returnType) {
		if (!map.containsKey(propName))
			throw new IllegalStateException("No property in the map: " + propName);
		Object ret = map.get(propName);
		if (ret == null)
			throw new IllegalStateException("No property in the map: " + propName);
		if (returnType != null && !returnType.isInstance(ret))
			throw new IllegalStateException("Value for property [" + propName + "] is not compatible " +
					"with type [" + returnType.getName() + "], it has type: " + ret.getClass().getName());
		return (T)ret;
	}

	public static List<String> repareTypingString(List<?> list) {
		return repareTypingAbstract(list, String.class);
	}

	public static List<Map> repareTypingMap(List<?> list, JSyncProcessor subst) {
		subst.checkListItems(list);
		return repareTypingAbstract(list, Map.class);
	}

	private static <T> List<T> repareTypingAbstract(List<?> list, Class<T> itemType) {
		List<T> ret = new ArrayList<T>();
		for (Object item : list) {
			if (!itemType.isInstance(item))
				throw new IllegalStateException("List item is not compatible with type " +
						"[" + itemType.getName() + "], it has type: " + item.getClass().getName());
			ret.add((T)item);
		}
		return ret;
	}

	public static List<Map> getListOfMapProp(Map<?,?> data, String propName, JSyncProcessor subst) {
		return Utils.repareTypingMap(Utils.propList(data, propName), subst);
	}
	
	public static String getPerlSimpleType(Map<?,?> map) {
		return getPerlSimpleType(prop(map, "!"));
	}

	public static String getPerlSimpleType(String type) {
		return type.contains("::") ? type.substring(type.lastIndexOf("::") + 2) : type;
	}
	
	public static KbType createTypeFromMap(Map<?,?> data, JSyncProcessor subst) {
		String typeType = Utils.getPerlSimpleType(data);
		KbType ret = typeType.equals("Typedef") ? new KbTypedef().loadFromMap(data, subst) :
			KbBasicType.createFromMap(data, subst);
		return ret;
	}
	
	public static int intPropFromString(Map<?,?> map, String propName) {
		String value = prop(map, propName);
		try {
			return Integer.parseInt(value);
		} catch(Exception ex) {
			throw new IllegalStateException("Value for property [" + propName + "] is not integer: " + value);
		}
	}
}

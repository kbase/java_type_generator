package us.kbase.scripts;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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
	
	public static String capitalize(String text) {
		return capitalize(text, false);
	}
	
	public static String inCamelCase(String text) {
		return capitalize(text, true);
	}
		
	public static String capitalize(String text, boolean camel) {
		StringBuilder ret = new StringBuilder();
		StringTokenizer st = new StringTokenizer(text, "_-");
		boolean firstToken = true;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (Character.isLowerCase(token.charAt(0)) && !(camel && firstToken)) {
				token = token.substring(0, 1).toUpperCase() + token.substring(1);
			}
			if (camel && firstToken && Character.isUpperCase(token.charAt(0))) {
				token = token.substring(0, 1).toLowerCase() + token.substring(1);				
			}
			ret.append(token);
			firstToken = false;
		}
		return ret.toString();
	}
	
	public static List<String> readFileLines(File f) throws IOException {
		return readStreamLines(new FileInputStream(f));
	}

	public static String readFileText(File f) throws IOException {
		return readStreamText(new FileInputStream(f));
	}

	public static List<String> readStreamLines(InputStream is) throws IOException {
		return readStreamLines(is, true);
	}
	
	public static List<String> readStreamLines(InputStream is, boolean closeAfter) throws IOException {
		List<String> ret = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while (true) {
			String l = br.readLine();
			if (l == null)
				break;
			ret.add(l);
		}
		if (closeAfter)
			br.close();
		return ret;
	}
	
	public static void writeFileLines(List<String> lines, File targetFile) throws IOException {
		PrintWriter pw = new PrintWriter(targetFile);
		for (String l : lines)
			pw.println(l);
		pw.close();
	}
	
	public static void copyStreams(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
	}

	public static String readStreamText(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStreams(is, baos);
		return new String(baos.toByteArray());
	}

	public static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory())
			for (File f : fileOrDir.listFiles()) 
				deleteRecursively(f);
		fileOrDir.delete();
	}
}
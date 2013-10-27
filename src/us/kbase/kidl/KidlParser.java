package us.kbase.kidl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.jkidl.SpecParser;

public class KidlParser {

	public static List<KbService> parseSpec(File specFile, File tempDir) throws KidlParseException {
		return parseSpec(specFile, tempDir, null);
	}

	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn) throws KidlParseException {
		return parseSpec(specFile, tempDir, modelToTypeJsonSchemaReturn, null);
	}

	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, String kbTop) throws KidlParseException {
		return parseSpec(specFile, tempDir, modelToTypeJsonSchemaReturn, kbTop, false);
	}
	
	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, String kbTop, boolean internal) throws KidlParseException {
		Map<?,?> map = null;
		try {
			if (internal) {
				map = parseSpecInt(specFile, tempDir, modelToTypeJsonSchemaReturn);
			} else {
				map = parseSpecExt(specFile, tempDir, modelToTypeJsonSchemaReturn, kbTop);
			}
			return parseSpec(map);
		} catch (KidlParseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KidlParseException("Error during parsing spec-file: " + ex.getMessage(), ex);
		}
	}
	
	public static List<KbService> parseSpec(Map<?,?> parseMap) throws KidlParseException {
		return KbService.loadFromMap(parseMap);
	}

	public static Map<?,?> parseSpecInt(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn) throws Exception {
        IncludeProvider ip = new FileIncludeProvider(specFile.getCanonicalFile().getParentFile());
		return parseSpecInt(specFile, tempDir, modelToTypeJsonSchemaReturn, ip);
	}
	
	public static Map<?,?> parseSpecInt(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, IncludeProvider ip) throws Exception {
        SpecParser p = new SpecParser(new DataInputStream(new FileInputStream(specFile)));
        Map<String, KbModule> root = p.SpecStatement(ip);
        Map<String,List<Object>> ret = new LinkedHashMap<String, List<Object>>();
        for (KbModule module : root.values()) {
        	List<Object> modList = ret.get(module.getServiceName());
        	if (modList == null) {
        		modList = new ArrayList<Object>();
        		ret.put(module.getServiceName(), modList);
        	}
        	modList.add(module.toJson());
        }
        if (modelToTypeJsonSchemaReturn != null) {
        	for (Map.Entry<String, KbModule> entry : root.entrySet()) {
        		Map<String, String> typeToSchema = new TreeMap<String, String>();
        		for (KbModuleComp comp : entry.getValue().getModuleComponents())
        			if (comp instanceof KbTypedef) {
        				KbTypedef typedef = (KbTypedef)comp;
        				Map<?,?> schemaMap = createJsonSchema(typedef);
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                        StringWriter sw = new StringWriter();
                        mapper.writeValue(sw, schemaMap);
                        sw.close();
        				typeToSchema.put(typedef.getName(), sw.toString());
        			}
        		if (typeToSchema.size() > 0)
        			modelToTypeJsonSchemaReturn.put(entry.getKey(), typeToSchema);
        	}
        }
        return ret;
	}

	private static Map<String, Object> createJsonSchema(KbType type) throws IOException {
		return createJsonSchema(type, false);
	}
	
	private static Map<String, Object> createJsonSchema(KbType type, boolean inner) throws IOException {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		if (type instanceof KbTypedef) {
			KbTypedef td = (KbTypedef)type;
			if (!inner) {
				ret.put("id", td.getName());
				ret.put("description", td.getComment());
			}
			ret.putAll(createJsonSchema(td.getAliasType(), true));
		} else if (type instanceof KbScalar) {
			KbScalar sc = (KbScalar)type;
			ret.put("type", sc.getJsonStyleName());
			ret.put("original-type", sc.getName());
		} else if (type instanceof KbUnspecifiedObject) {
			ret.put("type", "object");
			ret.put("original-type", "UnspecifiedObject");
		} else if (type instanceof KbList) {
			KbList ls = (KbList)type;
			ret.put("type", "array");
			ret.put("original-type", "list");
			ret.put("items", createJsonSchema(ls.getElementType(), true));
		} else if (type instanceof KbMapping) {
			KbMapping mp = (KbMapping)type;
			ret.put("type", "object");
			ret.put("original-type", "mapping");
			ret.put("additionalProperties", createJsonSchema(mp.getValueType(), true));
		} else if (type instanceof KbTuple) {
			KbTuple tp = (KbTuple)type;
			ret.put("type", "array");
			ret.put("original-type", "tuple");
			ret.put("maxItems", tp.getElementTypes().size());
			ret.put("minItems", tp.getElementTypes().size());
			List<Object> items = new ArrayList<Object>();
			for (KbType iType : tp.getElementTypes())
				items.add(createJsonSchema(iType, true));
			ret.put("items", items);
		} else if (type instanceof KbStruct) {
			KbStruct st = (KbStruct)type;
			ret.put("type", "object");
			ret.put("original-type", "structure");
			Map<String, Object> props = new LinkedHashMap<String, Object>();
			for (KbStructItem item : st.getItems())
				props.put(item.getName(), createJsonSchema(item.getItemType(), true));
			ret.put("properties", props);
			ret.put("additionalProperties", true);
			List<String> reqList = new ArrayList<>();
			for (KbStructItem item : st.getItems())
				if (!item.isOptional())
					reqList.add(item.getName());
			ret.put("required", reqList);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<?,?> parseSpecExt(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, String kbTop) 
					throws KidlParseException, IOException, InterruptedException, 
					ParserConfigurationException, SAXException {
		if (tempDir == null)
			tempDir = new File(".");
		File workDir = new File(tempDir, "temp_" + System.currentTimeMillis());
		workDir.mkdir();
		try {
			File bashFile = new File(workDir, "comp_server.sh");
			File specDir = specFile.getAbsoluteFile().getParentFile();
			File xmlFile = new File(workDir, "parsing_file.xml");
			if (kbTop == null)
				kbTop = System.getenv("KB_TOP");
			String compileTypespecDir = "";
			if (kbTop != null && kbTop.trim().length() > 0) {
				compileTypespecDir = kbTop + "/bin/";
			} else {
				System.out.println("WARNING: KB_TOP environment variable is not defined, " +
						"so compile_typespec is supposed to be in PATH");
			}
			PrintWriter pw = new PrintWriter(bashFile);
			pw.println("#!/bin/bash");
			boolean createJsonSchemas = modelToTypeJsonSchemaReturn != null;
			pw.println("" +
					compileTypespecDir + "compile_typespec --path \"" + specDir.getAbsolutePath() + "\"" +
					" --xmldump " + xmlFile.getName() + " " + (createJsonSchemas ? "--jsonschema " : "") + 
					"\"" + specFile.getAbsolutePath() + "\" " + workDir.getName()
					);
			pw.close();
			Process proc = new ProcessBuilder("bash", bashFile.getCanonicalPath()).directory(tempDir)
					.redirectErrorStream(true).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder errTextSB = new StringBuilder();
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				System.out.println("KIDL: " + l);
				errTextSB.append(l).append('\n');
			}
			br.close();
			proc.waitFor();
			if (!xmlFile.exists()) {
				String errText = errTextSB.toString();
				String[] options = {"path", "xmldump", "jsonschema"};
				String caption = null;
				for (String opt : options) {
					if (errText.contains("Unknown option: " + opt)) {
						caption = "It seems that you're using wrong branch of \"typecomp\" module (it should be \"dev-prototypes\")";
						break;
					}
				}
				if (caption == null)
					caption = "Parsing file wasn't created";
				throw new KidlParseException(caption + ", here is KIDL output:\n" + errText);
			}
			Map<String,Object> map = SpecXmlHelper.parseXml(xmlFile);
			Set<String> moduleNames = new HashSet<String>();
			for (Object obj : map.values()) {
				List<List<Object>> modList = (List<List<Object>>)obj;
				for (List<Object> module : modList) {
					if (module.size() != 3)
						throw new IllegalStateException("Wrong parse structure");
					module.set(1, new ArrayList<Object>());
					Map<String, Object> moduleProps = (Map<String, Object>)module.get(0);
					String moduleName = moduleProps.get("module_name").toString();
					moduleNames.add(moduleName);
				}
			}
			if (createJsonSchemas) {
				File schemasRoot = new File(workDir, "jsonschema");
				if (schemasRoot.exists())
					for (File moduleDir : schemasRoot.listFiles()) {
						if (!moduleDir.isDirectory())
							continue;
						String moduleName = moduleDir.getName();
						if (!moduleNames.contains(moduleName)) {
							continue;
						}
						Map<String, String> type2schema = new TreeMap<String, String>();
						for (File schemaFile : moduleDir.listFiles()) {
							if (!schemaFile.getName().endsWith(".json"))
								continue;
							String typeName = schemaFile.getName();
							typeName = typeName.substring(0, typeName.length() - 5);
							StringWriter sw = new StringWriter();
							PrintWriter schemaPw = new PrintWriter(sw);
							BufferedReader schemaBr = new BufferedReader(new FileReader(schemaFile));
							while (true) {
								String l = schemaBr.readLine();
								if (l == null)
									break;
								schemaPw.println(l);
							}
							schemaBr.close();
							schemaPw.close();
							type2schema.put(typeName, sw.toString());
						}
						modelToTypeJsonSchemaReturn.put(moduleDir.getName(), type2schema);
					}
			}
			return map;
		} finally {
			deleteRecursively(workDir);				
		}
	}	
	
	private static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory())
			for (File f : fileOrDir.listFiles()) 
				deleteRecursively(f);
		fileOrDir.delete();
	}
}

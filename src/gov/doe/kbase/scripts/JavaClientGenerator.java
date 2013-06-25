package gov.doe.kbase.scripts;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

import com.googlecode.jsonschema2pojo.DefaultGenerationConfig;
import com.googlecode.jsonschema2pojo.Jackson1Annotator;
import com.googlecode.jsonschema2pojo.SchemaGenerator;
import com.googlecode.jsonschema2pojo.SchemaMapper;
import com.googlecode.jsonschema2pojo.SchemaStore;
import com.googlecode.jsonschema2pojo.rules.RuleFactory;
import com.sun.codemodel.JCodeModel;

public class JavaClientGenerator {
	private static final char[] propWordDelim = {'_', '-'};

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Usage: <program> <json_parsing_file> <json_schema_out_dir> <src_out_dir> <java_package_without_model>");
			return;
		}
		File jsonParsingFile = new File(args[0]);
		File jsonSchemaOutDir = new File(args[1]);
		File srcOutDir = new File(args[2]);
		String packageParent = args[3];
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.INDENT_OUTPUT, true);
		Map<?,?> map = mapper.readValue(jsonParsingFile, Map.class);
		JSyncProcessor subst = new JSyncProcessor(map);
		List<KbService> srvList = KbService.loadFromMap(map, subst);
		JavaData data = prepareDataStructures(srvList);
		outputData(data, jsonSchemaOutDir, srcOutDir, packageParent);
	}

	private static JavaData prepareDataStructures(List<KbService> services) {
		Set<JavaType> nonPrimitiveTypes = new TreeSet<JavaType>();
		JavaData data = new JavaData();
		for (KbService service: services) {
			for (KbModule module : service.getModules()) {
				List<JavaFunc> funcs = new ArrayList<JavaFunc>();
				for (KbModuleComp comp : module.getModuleComponents()) {
					if (comp instanceof KbFuncdef) {
						String moduleName = module.getModuleName();
						KbFuncdef func = (KbFuncdef)comp;
						String funcJavaName = Utils.inCamelCase(func.getName());
						List<JavaFuncParam> params = new ArrayList<JavaFuncParam>();
						for (KbParameter param : func.getParameters()) {
							JavaType type = findBasic(param.getType(), module.getModuleName(), nonPrimitiveTypes);
							params.add(new JavaFuncParam(param, Utils.inCamelCase(param.getName()), type));
						}
						List<JavaFuncParam> returns = new ArrayList<JavaFuncParam>();
						for (KbParameter param : func.getReturnType()) {
							JavaType type = findBasic(param.getType(), module.getModuleName(), nonPrimitiveTypes);
							returns.add(new JavaFuncParam(param, param.getName() == null ? null : Utils.inCamelCase(param.getName()), type));
						}
						JavaType retMultiType = null;
						if (returns.size() > 1) {
							List<KbType> subTypes = new ArrayList<KbType>();
							for (JavaFuncParam retPar : returns)
								subTypes.add(retPar.getOriginal().getType());
							KbTuple tuple = new KbTuple(subTypes);
							retMultiType = new JavaType(null, tuple, moduleName, new ArrayList<KbTypedef>());
							for (JavaFuncParam retPar : returns)
								retMultiType.addInternalType(retPar.getType());
							nonPrimitiveTypes.add(retMultiType);
						}
						funcs.add(new JavaFunc(moduleName, func, funcJavaName, params, returns, retMultiType));
					}
				}
				data.addModule(module, funcs);
			}
		}
		data.setTypes(nonPrimitiveTypes);
		return data;
	}

	private static void outputData(JavaData data, File jsonOutDir, File srcOutDir, String packageParent) throws Exception {
		if (!srcOutDir.exists())
			srcOutDir.mkdirs();
		generatePojos(data, jsonOutDir, srcOutDir, packageParent);
		generateSerializersForTuples(data,srcOutDir, packageParent);
		generateClientClass(data, srcOutDir, packageParent);
		checkCallerClass(srcOutDir);
	}

	private static void generatePojos(JavaData data, File jsonOutDir,
			File srcOutDir, String packageParent) throws Exception,
			MalformedURLException, IOException {
		JCodeModel codeModel = new JCodeModel();
		DefaultGenerationConfig cfg = new DefaultGenerationConfig() {
			@Override
			public char[] getPropertyWordDelimiters() {
				return propWordDelim;
			}
			@Override
			public boolean isIncludeHashcodeAndEquals() {
				return false;
			}
			@Override
			public boolean isIncludeToString() {
				return false;
			}
			@Override
			public boolean isIncludeJsr303Annotations() {
				return false;
			}
		};
		SchemaStore ss = new SchemaStore();
		RuleFactory rf = new RuleFactory(cfg, new Jackson1Annotator(), ss);
		SchemaGenerator sg = new SchemaGenerator();
		SchemaMapper sm = new SchemaMapper(rf, sg);
		for (JavaType type : data.getTypes()) {
			File f = writeJsonSchema(jsonOutDir, packageParent, type);
			URL source = f.toURI().toURL();
			sm.generate(codeModel, type.getJavaClassName(), "", source);
		}
		codeModel.build(srcOutDir);
	}

	private static void generateSerializersForTuples(JavaData data, File srcOutDir, String packageParent) throws IOException {
		File parentDir = getParentSourceDir(srcOutDir, packageParent);
		for (JavaType type : data.getTypes()) {
			if (!(type.getMainType() instanceof KbTuple))
				continue;
			File moduleDir = new File(parentDir, type.getModuleName());
			File tupleClassFile = new File(moduleDir, type.getJavaClassName() + ".java");
			if (!tupleClassFile.exists())
				throw new IllegalStateException("Can't find " + tupleClassFile + " file");
			List<String> lines = Utils.readFileLines(tupleClassFile);
			int pos;
			for (pos = 0; pos < lines.size(); pos++) {
				String l = lines.get(pos);
				if (l.startsWith("public class "))
					break;
				if (l.startsWith("@JsonSerialize"))
					lines.set(pos, "//" + l);
			}
			lines.add(pos, "@JsonSerialize(using = " + type.getJavaClassName() + "Serializer.class)");
			lines.add(pos + 1, "@JsonDeserialize(using = " + type.getJavaClassName() + "Deserializer.class)");
			updateImports(lines, "org.codehaus.jackson.map.annotate.JsonSerialize", "org.codehaus.jackson.map.annotate.JsonDeserialize");
			Utils.writeFileLines(lines, tupleClassFile);
			String packageName = packageParent + "." + type.getModuleName();
			List<String> serLines = new ArrayList<String>(Arrays.asList(
					"package " + packageName + ";",
					"",
					"import java.io.*;",
					"import java.util.*;",
					"import org.codehaus.jackson.map.*;",
					"import org.codehaus.jackson.map.annotate.*;",
					"import org.codehaus.jackson.type.*;",
					"import org.codehaus.jackson.*;",
					"",
					"",
					"public class " + type.getJavaClassName() + "Serializer extends JsonSerializer<" + type.getJavaClassName() + "> {",
					"    public void serialize(" + type.getJavaClassName() + " value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {",
					"        jgen.writeStartArray();"
					));
			for (String field : type.getInternalFields())
				serLines.add(
						"        jgen.writeObject(value.get" + Utils.capitalize(field) + "());"
						);
			serLines.addAll(Arrays.asList(
					"        jgen.writeEndArray();",
					"    }",
					"}"
					));
			Utils.writeFileLines(serLines, new File(moduleDir, type.getJavaClassName() + "Serializer.java"));
			List<String> deserLines = new ArrayList<String>(Arrays.asList(
					"package " +  packageName + ";",
					"",
					"import java.io.*;",
					"import java.util.*;",
					"import org.codehaus.jackson.map.*;",
					"import org.codehaus.jackson.map.annotate.*;",
					"import org.codehaus.jackson.type.*;",
					"import org.codehaus.jackson.*;",
					"",
					"",
					"public class " + type.getJavaClassName() + "Deserializer extends JsonDeserializer<" + type.getJavaClassName() + "> {",
					"    public " + type.getJavaClassName() + " deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {",
					"        " + type.getJavaClassName() + " res = new " + type.getJavaClassName() + "();",
					"        if (!p.isExpectedStartArrayToken()) {",
					"            System.out.println(\"Bad parse in " + type.getJavaClassName() + "Deserializer: \" + p.getCurrentToken());",
					"            return null;",
					"        }",
					"        p.nextToken();"
					));
			for (int i = 0; i < type.getInternalFields().size(); i++) {
				String field = type.getInternalFields().get(i);
				JavaType iType = type.getInternalTypes().get(i);
				String classRef = null;
				if (iType.needClassGeneration()) {
					classRef = packageName + "." + iType.getJavaClassName() + ".class";
				} else if (iType.getMainType() instanceof KbScalar) {
					classRef = iType.getMainType().getJavaStyleName() + ".class";
				} else { // For List<...>
					classRef = "new TypeReference<" + getTypeName(iType, packageParent, type.getModuleName()) + ">() {}";
				}
				deserLines.add(
						"        res.set" + Utils.capitalize(field) + "(p.readValueAs(" + classRef + "));"
						);
			}
			deserLines.addAll(Arrays.asList(
					"        p.nextToken();",
					"        return res;",
					"    }",
					"}"
					));
			Utils.writeFileLines(deserLines, new File(moduleDir, type.getJavaClassName() + "Deserializer.java"));			
		}
	}

	private static File getParentSourceDir(File srcOutDir, String packageParent) {
		File parentDir = new File(srcOutDir.getAbsolutePath() + "/" + packageParent.replace('.', '/'));
		if (!parentDir.exists())
			parentDir.mkdirs();
		return parentDir;
	}

	private static void generateClientClass(JavaData data, File srcOutDir, String packageParent) throws Exception {
		File parentDir = getParentSourceDir(srcOutDir, packageParent);
		for (JavaModule module : data.getModules()) {
			File moduleDir = new File(parentDir, module.getModuleName());
			if (!moduleDir.exists())
				moduleDir.mkdir();
			File classFile = new File(moduleDir, "Client.java");
			List<String> classLines = new ArrayList<String>(Arrays.asList(
					"package " + packageParent + "." + module.getModuleName() + ";",
					"",
					"import java.net.*;",
					"import java.io.*;",
					"import java.util.*;",
					"import org.codehaus.jackson.map.*;",
					"import org.codehaus.jackson.map.annotate.*;",
					"import org.codehaus.jackson.type.*;",
					"import org.codehaus.jackson.*;",
					"",
					"import us.kbase.rpc.Caller;",
					"",
					"public class Client {",
					"    private Caller caller;",
					"",
					"    public Client(String url) throws MalformedURLException {",
					"        caller = new Caller(url);",
					"    }"
					));
			for (JavaFunc func : module.getFuncs()) {
				JavaType retType = null;
				if (func.getRetMultyType() == null) {
					if (func.getReturns().size() > 0) {
						retType = func.getReturns().get(0).getType();
					}
				} else {
					retType = func.getRetMultyType();
				}
				StringBuilder funcParams = new StringBuilder();
				for (JavaFuncParam param : func.getParams()) {
					if (funcParams.length() > 0)
						funcParams.append(", ");
					funcParams.append(getTypeName(param.getType(), packageParent, func.getModuleName()) + " " + param.getJavaName());
				}
				String retTypeName = getTypeName(retType, packageParent, module.getModuleName());
				classLines.add("");
				classLines.add("    public " + retTypeName + " " + func.getJavaName() + "(" + funcParams + ") throws Exception {");
				classLines.add("        List<Object> args = new ArrayList<Object>();");
				for (JavaFuncParam param : func.getParams()) {
					classLines.add("        args.add(" + param.getJavaName() + ");");
				}
				if (func.getRetMultyType() == null) {
					classLines.addAll(Arrays.asList(
							"        Object retType = new TypeReference<List<" + retTypeName + ">>() {};",
							"        List<" + retTypeName + "> res = caller.jsonrpc_call(\"" + module.getOriginal().getModuleName() + "." + func.getOriginal().getName() + "\", args, retType);",
							"        return res.get(0);",
							"    }"
							));
				} else {
					classLines.addAll(Arrays.asList(
							"        Object retType = " + retTypeName + ".class;",
							"        " + retTypeName + " res = caller.jsonrpc_call(\"" + module.getOriginal().getModuleName() + "." + func.getOriginal().getName() + "\", args, retType);",
							"        return res;",
							"    }"
							));					
				}
			}
			classLines.add("}");
			Utils.writeFileLines(classLines, classFile);
		}
	}

	private static void checkCallerClass(File srcOutDir) throws Exception {
		File callerClassFile = new File(srcOutDir.getAbsolutePath() + "/us/kbase/rpc/Caller.java");
		if (callerClassFile.exists())
			return;
		Utils.writeFileLines(Utils.readStreamLines(JavaClientGenerator.class.getResourceAsStream("Caller.java.properties")), callerClassFile);
	}
	
	private static void updateImports(List<String> javaCodeLines, String... addNamesToimports) {
		int insertPos = -1;
		Set<String> usedImports = new HashSet<String>();
		for (int pos = 0; ; pos++) {
			String l = javaCodeLines.get(pos).trim();
			boolean isImport = l.startsWith("import ");
			boolean isPackage = l.startsWith("package ");
			if (!(l.length() == 0 || isPackage || isImport))
				break;
			if (isPackage)
				insertPos = pos + 2;
			if (isImport) {
				usedImports.add(l.substring(7, l.length() - 1).trim());
				insertPos = pos + 1;
			}
			pos++;
		}
		for (String importName : addNamesToimports) {
			if (usedImports.contains(importName))
				continue;
			javaCodeLines.add(insertPos, "import " + importName + ";");
			insertPos++;
		}
	}

	private static File writeJsonSchema(File parentOutDir, String packageParent, JavaType type) throws Exception {
		LinkedHashMap<String, Object> tree = new LinkedHashMap<String, Object>();
		tree.put("$schema", "http://json-schema.org/draft-04/schema#");
		tree.put("id", type.getModuleName() + "." + type.getJavaClassName());
		tree.put("description", type.getComment());
		tree.put("type", "object");
		tree.put("javaType", packageParent + "." + type.getModuleName() + "." + type.getJavaClassName());
		if (type.getMainType() instanceof KbMapping) {
			if (!type.getInternalTypes().get(0).getJavaClassName().equals("String"))
				throw new IllegalStateException("Type [" + type.getInternalTypes().get(0).getOriginalTypeName() + "] " +
						"can not be used as map key type");
			JavaType subType = type.getInternalTypes().get(1);
			LinkedHashMap<String, Object> typeTree = createJsonRefTypeTree(type.getModuleName(), subType, null);
			tree.put("additionalProperties", typeTree);
		} else {
			LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>();
			for (int itemPos = 0; itemPos < type.getInternalTypes().size(); itemPos++) {
				JavaType iType = type.getInternalTypes().get(itemPos);
				String field = type.getInternalFields().get(itemPos);
				props.put(field, createJsonRefTypeTree(type.getModuleName(), iType, type.getInternalComment(itemPos)));
			}
			tree.put("properties", props);
			tree.put("additionalProperties", true);
		}
		File dir = new File(parentOutDir, type.getModuleName());
		if (!dir.exists())
			dir.mkdirs();
		File jsonFile = new File(dir, type.getJavaClassName() + ".json"); 
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.INDENT_OUTPUT, true);
		mapper.writeValue(jsonFile, tree);
		return jsonFile;
	}

	private static LinkedHashMap<String, Object> createJsonRefTypeTree(String module, JavaType type, String comment) {
		LinkedHashMap<String, Object> typeTree = new LinkedHashMap<String, Object>();
		if (comment != null && comment.trim().length() > 0)
			typeTree.put("description", comment);
		if (type.needClassGeneration()) {
			String modulePrefix = type.getModuleName().equals(module) ? "" : ("../" + type.getModuleName() + "/");
			typeTree.put("$ref", modulePrefix + type.getJavaClassName() + ".json");
		} else if (type.getMainType() instanceof KbScalar) {
			typeTree.put("type", ((KbScalar)type.getMainType()).getJsonStyleName());
		} else if (type.getMainType() instanceof KbList) {
			typeTree.put("type", "array");
			typeTree.put("items", createJsonRefTypeTree(module, type.getInternalTypes().get(0), null));
		} else {
			throw new IllegalStateException("Unknown type: " + type.getMainType().getClass().getName());
		}
		return typeTree;
	}

	private static JavaType findBasic(KbType type, String moduleName, Set<JavaType> nonPrimitiveTypes) {
		JavaType ret = findBasic(null, type, moduleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes);
		return ret;
	}

	private static JavaType findBasic(String typeName, KbType type, String defaultModuleName, String typeModuleName, List<KbTypedef> aliases, Set<JavaType> nonPrimitiveTypes) {
		if (type instanceof KbBasicType) {
			JavaType ret = new JavaType(typeName, (KbBasicType)type, typeModuleName == null ? defaultModuleName : typeModuleName, aliases);
			if (!(type instanceof KbScalar))
				if (type instanceof KbStruct) {
					for (KbStructItem item : ((KbStruct)type).getItems()) {
						ret.addInternalType(findBasic(null, item.getItemType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes));
						ret.addInternalField(item.getName(), "");
					}
				} else if (type instanceof KbList) {
					ret.addInternalType(findBasic(null, ((KbList)type).getElementType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes));
				} else if (type instanceof KbMapping) {
					ret.addInternalType(findBasic(null, ((KbMapping)type).getKeyType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes));
					ret.addInternalType(findBasic(null, ((KbMapping)type).getValueType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes));
				} else if (type instanceof KbTuple) {
					for (KbType iType : ((KbTuple)type).getElementTypes())
						ret.addInternalType(findBasic(null, iType, defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes));
				} else {
					throw new IllegalStateException("Unknown basic type: " + type.getClass().getSimpleName());
				}
			if (ret.needClassGeneration())
				nonPrimitiveTypes.add(ret);
			return ret;
		} else {
			KbTypedef typeRef = (KbTypedef)type;
			aliases.add(typeRef);
			return findBasic(typeRef.getName(), typeRef.getAliasType(), defaultModuleName, typeRef.getModule(), aliases, nonPrimitiveTypes);
		}
	}

	private static String getTypeName(JavaType type, String packageParent, String currentModule) {
		KbBasicType kbt = type.getMainType();
		if (type.needClassGeneration()) {
			return getPackagePrefix(packageParent, currentModule, type) + type.getJavaClassName();
		} else if (kbt instanceof KbScalar) {
			return kbt.getJavaStyleName();
		} else if (kbt instanceof KbList) {
			return getPackagePrefix(packageParent, currentModule, type) + "List<" + getTypeName(type.getInternalTypes().get(0), packageParent, currentModule) + ">";
		} else {
			throw new IllegalStateException("Unknown data type: " + kbt.getClass().getName());
		}
	}

	private static String getPackagePrefix(String packageParent, String currentModule, JavaType type) {
		return packageParent == null ? "" : (currentModule.equals(type.getModuleName()) ? "" : (packageParent + "." + type.getModuleName() + "."));
	}
}

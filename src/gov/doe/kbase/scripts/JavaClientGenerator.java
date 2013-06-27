package gov.doe.kbase.scripts;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.googlecode.jsonschema2pojo.rules.Rule;
import com.googlecode.jsonschema2pojo.rules.RuleFactory;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

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
				Set<Integer> tupleTypes = new TreeSet<Integer>();
				for (KbModuleComp comp : module.getModuleComponents()) {
					if (comp instanceof KbFuncdef) {
						String moduleName = module.getModuleName();
						KbFuncdef func = (KbFuncdef)comp;
						String funcJavaName = Utils.inCamelCase(func.getName());
						List<JavaFuncParam> params = new ArrayList<JavaFuncParam>();
						for (KbParameter param : func.getParameters()) {
							JavaType type = findBasic(param.getType(), module.getModuleName(), nonPrimitiveTypes, tupleTypes);
							params.add(new JavaFuncParam(param, Utils.inCamelCase(param.getName()), type));
						}
						List<JavaFuncParam> returns = new ArrayList<JavaFuncParam>();
						for (KbParameter param : func.getReturnType()) {
							JavaType type = findBasic(param.getType(), module.getModuleName(), nonPrimitiveTypes, tupleTypes);
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
							//nonPrimitiveTypes.add(retMultiType);
						}
						funcs.add(new JavaFunc(moduleName, func, funcJavaName, params, returns, retMultiType));
					} else {
						findBasic((KbTypedef)comp, module.getModuleName(), nonPrimitiveTypes, tupleTypes);
					}
				}
				data.addModule(module, funcs, tupleTypes);
			}
		}
		data.setTypes(nonPrimitiveTypes);
		return data;
	}

	private static void outputData(JavaData data, File jsonOutDir, File srcOutDir, String packageParent) throws Exception {
		if (!srcOutDir.exists())
			srcOutDir.mkdirs();
		generatePojos(data, jsonOutDir, srcOutDir, packageParent);
		generateTupleClasses(data,srcOutDir, packageParent);
		generateClientClass(data, srcOutDir, packageParent);
		checkUtilityClasses(srcOutDir);
	}

	private static void generatePojos(JavaData data, File jsonOutDir,
			File srcOutDir, String packageParent) throws Exception {
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
		RuleFactory rf = new RuleFactory(cfg, new Jackson1Annotator(), ss) {
			@Override
			public Rule<JPackage, JType> getObjectRule() {
				return new JsonSchemaToPojoCustomObjectRule(this);
			}
		};
		SchemaGenerator sg = new SchemaGenerator();
		SchemaMapper sm = new SchemaMapper(rf, sg);
		for (JavaType type : data.getTypes()) {
			Set<Integer> tupleTypes = data.getModule(type.getModuleName()).getTupleTypes();
			File f = writeJsonSchema(jsonOutDir, packageParent, type, tupleTypes);
			URL source = f.toURI().toURL();
			sm.generate(codeModel, type.getJavaClassName(), "", source);
		}
		codeModel.build(srcOutDir);
	}
	
	private static void generateTupleClasses(JavaData data, File srcOutDir, String packageParent) throws Exception {
		File parentDir = getParentSourceDir(srcOutDir, packageParent);
		for (JavaModule module : data.getModules()) {
			File moduleDir = new File(parentDir, module.getModuleName());
			Set<Integer> tupleTypes = module.getTupleTypes();
			if (tupleTypes.size() > 0) {
				if (!moduleDir.exists())
					moduleDir.mkdir();
				for (int tupleType : tupleTypes) {
					if (tupleType < 1)
						throw new IllegalStateException("Wrong tuple type: " + tupleType);
					File tupleFile = new File(moduleDir, "Tuple" + tupleType + ".java");
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < tupleType; i++) {
						if (sb.length() > 0)
							sb.append(", ");
						sb.append('T').append(i+1);
					}
					List<String> classLines = new ArrayList<String>(Arrays.asList(
							"package " + packageParent + "." + module.getModuleName() + ";",
							"",
							"import java.util.HashMap;",
							"import java.util.Map;",
							"import org.codehaus.jackson.annotate.JsonAnyGetter;",
							"import org.codehaus.jackson.annotate.JsonAnySetter;",
							"",
							"public class Tuple3 <" + sb + "> {"
							));
					for (int i = 0; i < tupleType; i++) {
						classLines.add("    private T" + (i + 1) + " e" + (i + 1) + ";");
					}
					classLines.add("    private Map<String, Object> additionalProperties = new HashMap<String, Object>();");
					for (int i = 0; i < tupleType; i++) {
						classLines.addAll(Arrays.asList(
								"",
								"    public T" + (i + 1) + " getE" + (i + 1) + "() {",
								"        return e" + (i + 1) + ";",
								"    }",
								"",
								"    public void setE" + (i + 1) + "(T" + (i + 1) + " e" + (i + 1) + ") {",
								"        this.e" + (i + 1) + " = e" + (i + 1) + ";",
								"    }"
								));
					}
					classLines.addAll(Arrays.asList(
							"",
							"    @JsonAnyGetter",
							"    public Map<String, Object> getAdditionalProperties() {",
							"        return this.additionalProperties;",
							"    }",
							"",
							"    @JsonAnySetter",
							"    public void setAdditionalProperties(String name, Object value) {",
							"        this.additionalProperties.put(name, value);",
							"    }",
							"}"
							));
					Utils.writeFileLines(classLines, tupleFile);
				}
			}
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
					"import java.util.*;",
					"import org.codehaus.jackson.type.*;",
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

	private static void checkUtilityClasses(File srcOutDir) throws Exception {
		checkUtilityClass(srcOutDir, "Caller");
		checkUtilityClass(srcOutDir, "JacksonTupleModule");
	}

	private static void checkUtilityClass(File srcOutDir, String className) throws Exception {
		File dir = new File(srcOutDir.getAbsolutePath() + "/us/kbase/rpc");
		if (!dir.exists())
			dir.mkdirs();
		File dstClassFile = new File(dir, className + ".java");
		if (dstClassFile.exists())
			return;
		Utils.writeFileLines(Utils.readStreamLines(JavaClientGenerator.class.getResourceAsStream(className + ".java.properties")), dstClassFile);
	}
	
	private static File writeJsonSchema(File parentOutDir, String packageParent, JavaType type, Set<Integer> tupleTypes) throws Exception {
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
			LinkedHashMap<String, Object> typeTree = createJsonRefTypeTree(type.getModuleName(), subType, null, false, packageParent, tupleTypes);
			tree.put("additionalProperties", typeTree);
			throw new IllegalStateException();
		} else {
			LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>();
			for (int itemPos = 0; itemPos < type.getInternalTypes().size(); itemPos++) {
				JavaType iType = type.getInternalTypes().get(itemPos);
				String field = type.getInternalFields().get(itemPos);
				props.put(field, createJsonRefTypeTree(type.getModuleName(), iType, type.getInternalComment(itemPos), false, packageParent, tupleTypes));
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

	private static LinkedHashMap<String, Object> createJsonRefTypeTree(String module, JavaType type, String comment, boolean insideTypeParam, String packageParent, Set<Integer> tupleTypes) {
		LinkedHashMap<String, Object> typeTree = new LinkedHashMap<String, Object>();
		if (comment != null && comment.trim().length() > 0)
			typeTree.put("description", comment);
		if (type.needClassGeneration()) {
			if (insideTypeParam) {
				typeTree.put("type", "object");
				typeTree.put("javaType", packageParent + "." + type.getModuleName() + "." + type.getJavaClassName());
			} else {
				String modulePrefix = type.getModuleName().equals(module) ? "" : ("../" + type.getModuleName() + "/");
				typeTree.put("$ref", modulePrefix + type.getJavaClassName() + ".json");
			}
		} else if (type.getMainType() instanceof KbScalar) {
			if (insideTypeParam) {
				typeTree.put("type", "object");
				typeTree.put("javaType", ((KbScalar)type.getMainType()).getJavaStyleName());
			} else {
				typeTree.put("type", ((KbScalar)type.getMainType()).getJsonStyleName());
			}
		} else if (type.getMainType() instanceof KbList) {
			LinkedHashMap<String, Object> subType = createJsonRefTypeTree(module, type.getInternalTypes().get(0), null, insideTypeParam, packageParent, tupleTypes);
			if (insideTypeParam) {
				typeTree.put("type", "object");
				typeTree.put("javaType", "java.util.List");
				typeTree.put("javaTypeParams", subType);
			} else {
				typeTree.put("type", "array");
				typeTree.put("items", subType);
			}
		} else if (type.getMainType() instanceof KbMapping) {
			typeTree.put("type", "object");
			typeTree.put("javaType", "java.util.Map");
			List<LinkedHashMap<String, Object>> subList = new ArrayList<LinkedHashMap<String, Object>>();
			for (JavaType iType : type.getInternalTypes())
				subList.add(createJsonRefTypeTree(module, iType, null, true, packageParent, tupleTypes));
			typeTree.put("javaTypeParams", subList);
		} else if (type.getMainType() instanceof KbTuple) {
			typeTree.put("type", "object");
			int tupleType = type.getInternalTypes().size();
			if (tupleType < 1)
				throw new IllegalStateException("Wrong count of tuple parameters: " + tupleType);
			typeTree.put("javaType", packageParent + "." + type.getModuleName() + ".Tuple" + tupleType);
			tupleTypes.add(tupleType);
			List<LinkedHashMap<String, Object>> subList = new ArrayList<LinkedHashMap<String, Object>>();
			for (JavaType iType : type.getInternalTypes())
				subList.add(createJsonRefTypeTree(module, iType, null, true, packageParent, tupleTypes));
			typeTree.put("javaTypeParams", subList);
		} else {
			throw new IllegalStateException("Unknown type: " + type.getMainType().getClass().getName());
		}
		return typeTree;
	}

	private static JavaType findBasic(KbType type, String moduleName, Set<JavaType> nonPrimitiveTypes, Set<Integer> tupleTypes) {
		JavaType ret = findBasic(null, type, moduleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes, tupleTypes);
		return ret;
	}

	private static JavaType findBasic(String typeName, KbType type, String defaultModuleName, String typeModuleName, List<KbTypedef> aliases, Set<JavaType> nonPrimitiveTypes, Set<Integer> tupleTypes) {
		if (type instanceof KbBasicType) {
			JavaType ret = new JavaType(typeName, (KbBasicType)type, typeModuleName == null ? defaultModuleName : typeModuleName, aliases);
			if (!(type instanceof KbScalar))
				if (type instanceof KbStruct) {
					for (KbStructItem item : ((KbStruct)type).getItems()) {
						ret.addInternalType(findBasic(null, item.getItemType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes, tupleTypes));
						ret.addInternalField(item.getName(), "");
					}
				} else if (type instanceof KbList) {
					ret.addInternalType(findBasic(null, ((KbList)type).getElementType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes, tupleTypes));
				} else if (type instanceof KbMapping) {
					ret.addInternalType(findBasic(null, ((KbMapping)type).getKeyType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes, tupleTypes));
					ret.addInternalType(findBasic(null, ((KbMapping)type).getValueType(), defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes, tupleTypes));
				} else if (type instanceof KbTuple) {
					tupleTypes.add(((KbTuple)type).getElementTypes().size());
					for (KbType iType : ((KbTuple)type).getElementTypes())
						ret.addInternalType(findBasic(null, iType, defaultModuleName, null, new ArrayList<KbTypedef>(), nonPrimitiveTypes, tupleTypes));
				} else {
					throw new IllegalStateException("Unknown basic type: " + type.getClass().getSimpleName());
				}
			if (ret.needClassGeneration())
				nonPrimitiveTypes.add(ret);
			return ret;
		} else {
			KbTypedef typeRef = (KbTypedef)type;
			aliases.add(typeRef);
			return findBasic(typeRef.getName(), typeRef.getAliasType(), defaultModuleName, typeRef.getModule(), aliases, nonPrimitiveTypes, tupleTypes);
		}
	}

	private static String getTypeName(JavaType type, String packageParent, String currentModule) {
		KbBasicType kbt = type.getMainType();
		if (type.needClassGeneration()) {
			return getPackagePrefix(packageParent, currentModule, type) + type.getJavaClassName();
		} else if (kbt instanceof KbScalar) {
			return kbt.getJavaStyleName();
		} else if (kbt instanceof KbList) {
			return "List<" + getTypeName(type.getInternalTypes().get(0), packageParent, currentModule) + ">";
		} else if (kbt instanceof KbMapping) {
			return "Map<" + getTypeName(type.getInternalTypes().get(0), packageParent, currentModule) + "," + 
					getTypeName(type.getInternalTypes().get(1), packageParent, currentModule) + ">";
		} else if (kbt instanceof KbTuple) {
			int paramCount = type.getInternalTypes().size();
			StringBuilder sb = new StringBuilder();
			for (JavaType iType : type.getInternalTypes()) {
				if (sb.length() > 0)
					sb.append(',');
				sb.append(getTypeName(iType, packageParent, currentModule));
			}
			return "Tuple" + paramCount + "<" + sb + ">";
		} else {
			throw new IllegalStateException("Unknown data type: " + kbt.getClass().getName());
		}
	}

	private static String getPackagePrefix(String packageParent, String currentModule, JavaType type) {
		return packageParent == null ? "" : (currentModule.equals(type.getModuleName()) ? "" : (packageParent + "." + type.getModuleName() + "."));
	}
}

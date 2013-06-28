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
import org.codehaus.jackson.type.TypeReference;

import com.googlecode.jsonschema2pojo.DefaultGenerationConfig;
import com.googlecode.jsonschema2pojo.Jackson1Annotator;
import com.googlecode.jsonschema2pojo.SchemaGenerator;
import com.googlecode.jsonschema2pojo.SchemaMapper;
import com.googlecode.jsonschema2pojo.SchemaStore;
import com.googlecode.jsonschema2pojo.rules.Rule;
import com.googlecode.jsonschema2pojo.rules.RuleFactory;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class JavaClientGenerator {
	private static final char[] propWordDelim = {'_', '-'};

	public static void main(String[] args) throws Exception {
		if (args.length != 5) {
			System.out.println("Usage: <program> <mode:json|spec> <json_parsing_file|spec_file> <json_schema_out_dir> <src_out_dir> <java_package_without_model>");
			return;
		}
		String mode = args[0];
		File inputFile = new File(args[1]);
		File jsonSchemaOutDir = new File(args[2]);
		File srcOutDir = new File(args[3]);
		String packageParent = args[4];
		if (mode.equals("json")) {
			processJson(inputFile, jsonSchemaOutDir, srcOutDir, packageParent);
		} else if (mode.equals("spec")) {
			processSpec(inputFile, jsonSchemaOutDir, srcOutDir, packageParent);
		} else {
			throw new IllegalStateException("Unsupported mode: " + mode);
		}
	}
	
	public static void processSpec(File specFile, File jsonSchemaOutDir, File srcOutDir, String packageParent) throws Exception {		
		processJson(transformSpecToJson(specFile), jsonSchemaOutDir, srcOutDir, packageParent);
	}
	
	public static File transformSpecToJson(File specFile) throws Exception {
		throw new IllegalStateException("Mode 'spec' is not supported yet.");
	}
	
	public static JavaData processJson(File jsonParsingFile, File jsonSchemaOutDir, File srcOutDir, String packageParent) throws Exception {		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.INDENT_OUTPUT, true);
		Map<?,?> map = mapper.readValue(jsonParsingFile, Map.class);
		JSyncProcessor subst = new JSyncProcessor(map);
		List<KbService> srvList = KbService.loadFromMap(map, subst);
		JavaData data = prepareDataStructures(srvList);
		outputData(data, jsonSchemaOutDir, srcOutDir, packageParent);
		return data;
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
							tupleTypes.add(returns.size());
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
		for (JavaType type : data.getTypes()) {
			Set<Integer> tupleTypes = data.getModule(type.getModuleName()).getTupleTypes();
			File dir = new File(jsonOutDir, type.getModuleName());
			if (!dir.exists())
				dir.mkdirs();
			File jsonFile = new File(dir, type.getJavaClassName() + ".json"); 
			writeJsonSchema(jsonFile, packageParent, type, tupleTypes);
		}
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
			File jsonFile = new File(new File(jsonOutDir, type.getModuleName()), type.getJavaClassName() + ".json"); 
			URL source = jsonFile.toURI().toURL();
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
							"public class Tuple" + tupleType + " <" + sb + "> {"
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
			//File classFile = new File(moduleDir, "Client.java");
			JCodeModel model = new JCodeModel();
			JDefinedClass clientClass = model._package(packageParent + "." + module.getModuleName())._class("Client");
			JType callerType = model.ref("us.kbase.rpc.Caller");
			JFieldVar callerField = clientClass.field(JMod.PRIVATE, callerType, "caller");
			JMethod constr = clientClass.constructor(JMod.PUBLIC);
			JType stringType = model.ref("java.lang.String");
			JVar urlVar = constr.param(stringType, "url");
			constr._throws(model.ref("java.net.MalformedURLException"));
			constr.body().assign(callerField, JExpr._new(callerType).arg(urlVar));
			for (JavaFunc func : module.getFuncs()) {
				JavaType retType = null;
				boolean uniqueRet = func.getRetMultyType() == null;
				if (uniqueRet) {
					if (func.getReturns().size() > 0) {
						retType = func.getReturns().get(0).getType();
					}
				} else {
					retType = func.getRetMultyType();
				}
				JType retJType = getJType(retType, packageParent, model);
				JMethod method = clientClass.method(JMod.PUBLIC, retJType, func.getJavaName());
				List<JVar> inputVars = new ArrayList<JVar>();
				for (JavaFuncParam param : func.getParams()) {
					JType argType = getJType(param.getType(), packageParent, model);
					JVar argVar = method.param(argType, param.getJavaName());
					inputVars.add(argVar);
				}
				method._throws(model.ref("java.lang.Exception"));
				JBlock block = method.body();
				JVar arrayListVar = block.decl(model.ref(List.class).narrow(Object.class), "args", 
						JExpr._new(model.ref(ArrayList.class).narrow(Object.class)));
				for (JVar inputVar : inputVars) {
					block.add(arrayListVar.invoke("add").arg(inputVar));
				}
				JClass typeReferenceType = model.ref(TypeReference.class);
				JClass outerRetType = uniqueRet ? typeReferenceType.narrow(model.ref(List.class).narrow(retJType)) :
					typeReferenceType.narrow(retJType);
				JVar retTypeVar = block.decl(outerRetType, "retType");
				block.directStatement("retType = new " + outerRetType.name() + "() {};");
				//block.assign(retTypeVar, JExpr._new(model.anonymousClass(outerRetType)));
				JType resType = uniqueRet ? model.ref(List.class).narrow(retJType) : retJType;
				JInvocation rpcMethod = JExpr.invoke(callerField, "jsonrpc_call").arg(module.getOriginal().getModuleName() + "." + func.getOriginal().getName()).arg(arrayListVar).arg(retTypeVar);
				JVar resVar = block.decl(resType, "res", rpcMethod);
				block._return(uniqueRet ? JExpr.invoke(resVar, "get").arg(JExpr.lit(0)) : resVar);
			}
			model.build(srcOutDir);
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
	
	private static void writeJsonSchema(File jsonFile, String packageParent, JavaType type, Set<Integer> tupleTypes) throws Exception {
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
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.INDENT_OUTPUT, true);
		mapper.writeValue(jsonFile, tree);
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

	private static JClass getJType(JavaType type, String packageParent, JCodeModel codeModel) {
		KbBasicType kbt = type.getMainType();
		if (type.needClassGeneration()) {
			return codeModel.ref(getPackagePrefix(packageParent, type) + type.getJavaClassName());
		} else if (kbt instanceof KbScalar) {
			return codeModel.ref(((KbScalar)kbt).getJavaStyleName());
		} else if (kbt instanceof KbList) {
			return codeModel.ref("java.util.List").narrow(getJType(type.getInternalTypes().get(0), packageParent, codeModel));
		} else if (kbt instanceof KbMapping) {
			return codeModel.ref("java.util.Map").narrow(getJType(type.getInternalTypes().get(0), packageParent, codeModel), 
					getJType(type.getInternalTypes().get(1), packageParent, codeModel));
		} else if (kbt instanceof KbTuple) {
			int paramCount = type.getInternalTypes().size();
			List<JClass> paramTypes = new ArrayList<JClass>();
			for (JavaType iType : type.getInternalTypes())
				paramTypes.add(getJType(iType, packageParent, codeModel));
			return codeModel.ref(getPackagePrefix(packageParent, type) + "Tuple" + paramCount).narrow(paramTypes);
		} else {
			throw new IllegalStateException("Unknown data type: " + kbt.getClass().getName());
		}
	}

	private static String getPackagePrefix(String packageParent, JavaType type) {
		return packageParent + "." + type.getModuleName() + ".";
	}
}

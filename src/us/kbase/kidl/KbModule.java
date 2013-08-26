package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KbModule {
	private String moduleName;
	private String serviceName;
	private String comment;
	private List<String> options;
	private List<KbModuleComp> moduleComponents;
	private List<KbTypeInfo> typeInfoList;
	private Map<String, KbType> nameToType;
	
	public void loadFromList(List<?> data) {
		if (data.size() != 3)
			throw new IllegalStateException("List has wrong number of elements: " + data.size());
		Map<?,?> props = (Map<?,?>)data.get(0);
		moduleName = Utils.prop(props, "module_name");
		serviceName = Utils.prop(props, "service_name");
		comment = Utils.propOrNull(props, "comment");
		List<?> optionList = Utils.propList(props, "options");
		options = Collections.unmodifiableList(Utils.repareTypingString(optionList));
		moduleComponents = new ArrayList<KbModuleComp>();
		for (Map<?,?> compProps : Utils.getListOfMapProp(props, "module_components")) {
			String compType = Utils.getPerlSimpleType(compProps);
			if (compType.equals("Typedef")) {
				moduleComponents.add(new KbTypedef().loadFromMap(compProps));
			} else if (compType.equals("Funcdef")) {
				moduleComponents.add(new KbFuncdef().loadFromMap(compProps));
			} else {
				throw new IllegalStateException("Unknown module component type: " + compType);
			}
		}
		moduleComponents = Collections.unmodifiableList(moduleComponents);
		typeInfoList = new ArrayList<KbTypeInfo>();
		for (Map<?,?> infoProps : Utils.repareTypingMap((List<?>)data.get(1))) {
			typeInfoList.add(new KbTypeInfo().loadFromMap(infoProps));
		}
		typeInfoList = Collections.unmodifiableList(typeInfoList);
		Map<?,?> typeMap = (Map<?,?>)data.get(2);
		nameToType = new LinkedHashMap<String, KbType>();
		for (Object key : typeMap.keySet()) {
			String typeName = key.toString();
			Map<?,?> typeProps = Utils.propMap(typeMap, typeName);
			nameToType.put(typeName, Utils.createTypeFromMap(typeProps));
		}
		nameToType = Collections.unmodifiableMap(nameToType);
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public List<String> getOptions() {
		return options;
	}
	
	public String getComment() {
		return comment;
	}
	
	public List<KbModuleComp> getModuleComponents() {
		return moduleComponents;
	}
	
	public List<KbTypeInfo> getTypeInfoList() {
		return typeInfoList;
	}
	
	public Map<String, KbType> getNameToType() {
		return nameToType;
	}
}

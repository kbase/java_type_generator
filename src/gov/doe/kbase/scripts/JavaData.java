package gov.doe.kbase.scripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JavaData {
	private List<JavaType> types = new ArrayList<JavaType>();
	private Map<String, JavaModule> modules = new TreeMap<String, JavaModule>();
	
	public JavaData() {
	}

	public void addModule(KbModule original, List<JavaFunc> funcs) {
		JavaModule jm = new JavaModule(original, funcs);
		modules.put(jm.getModuleName(), jm);
	}
	
	public JavaModule getModule(String name) {
		JavaModule ret = modules.get(name);
		if (ret == null)
			throw new IllegalStateException("Can't find module with name: " + name);
		return ret;
	}
	
	public List<JavaModule> getModules() {
		return new ArrayList<JavaModule>(modules.values());
	}
	
	public void setTypes(Collection<JavaType> types) {
		this.types.addAll(types);
		this.types = Collections.unmodifiableList(this.types);
	}
	
	public List<JavaType> getTypes() {
		return types;
	}
}

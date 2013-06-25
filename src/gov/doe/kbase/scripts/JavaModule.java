package gov.doe.kbase.scripts;

import java.util.Collections;
import java.util.List;

public class JavaModule {
	private String moduleName;
	private KbModule original;
	private List<JavaFunc> funcs;
	
	public JavaModule(KbModule original, List<JavaFunc> funcs) {
		this.moduleName = Utils.capitalize(original.getModuleName()).toLowerCase();
		this.original = original;
		this.funcs = Collections.unmodifiableList(funcs);
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public KbModule getOriginal() {
		return original;
	}
	
	public List<JavaFunc> getFuncs() {
		return funcs;
	}
}

package gov.doe.kbase.scripts;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JavaModule {
	private String moduleName;
	private KbModule original;
	private List<JavaFunc> funcs;
	private Set<Integer> tupleTypes;
	
	public JavaModule(KbModule original, List<JavaFunc> funcs, Set<Integer> tupleTypes) {
		this.moduleName = Utils.capitalize(original.getModuleName()).toLowerCase();
		this.original = original;
		this.funcs = Collections.unmodifiableList(funcs);
		this.tupleTypes = Collections.unmodifiableSet(tupleTypes);
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
	
	public Set<Integer> getTupleTypes() {
		return tupleTypes;
	}
}

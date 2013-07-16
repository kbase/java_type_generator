package us.kbase.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class JavaImportHolder {
	private String currentPackage;
	private Set<String> importedClasses = new TreeSet<String>();
	
	public JavaImportHolder(String currentPackage) {
		this.currentPackage = currentPackage;
	}
	
	public String ref(String fullyQualifiedClass) {
		String className = fullyQualifiedClass;
		String packageName = "";
		int dotPos = fullyQualifiedClass.lastIndexOf('.');
		if (dotPos > 0) {
			className = fullyQualifiedClass.substring(dotPos + 1);
			packageName = fullyQualifiedClass.substring(0, dotPos);
		}
		if (!(packageName.equals("java.lang") || packageName.equals(currentPackage)))
			importedClasses.add(fullyQualifiedClass);
		return className;
	}
	
	public List<String> generateImports() {
		List<String> ret = new ArrayList<String>();
		for (String className : importedClasses)
			ret.add("import " + className + ";");
		return ret;
	}
}

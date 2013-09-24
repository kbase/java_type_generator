package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KbService {
	private String name;
	private List<KbModule> modules;
	
	public KbService(String name) {
		this.name = name;
	}
		
	public void loadFromList(List<?> data) throws KidlParseException {
		List<KbModule> modules = new ArrayList<KbModule>();
		for (Object item : data) {
			KbModule mod = new KbModule();
			mod.loadFromList((List<?>)item);
			modules.add(mod);
		}
		this.modules = Collections.unmodifiableList(modules);
	}
	
	public static List<KbService> loadFromMap(Map<?,?> data) throws KidlParseException {
		List<KbService> ret = new ArrayList<KbService>();
		for (Map.Entry<?,?> entry : data.entrySet()) {
			KbService srv = new KbService("" + entry.getKey());
			srv.loadFromList((List<?>)entry.getValue());
			ret.add(srv);
		}
		return ret;
	}

	public String getName() {
		return name;
	}
	
	public List<KbModule> getModules() {
		return modules;
	}
}

package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represents group of modules with the same service name 
 * (first optional part of module name before colon). If module
 * name defined by one keyword without colon then service name
 * is supposed to be equal to module name.
 */
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
	
	
    public Map<String, Object> forTemplates() {
        Map<String, Object> ret = new LinkedHashMap<String, Object>();
        List<Object> modules = new ArrayList<Object>();
        boolean psbl = false;
        boolean only = true;
        int funcCount = 0;
        for (KbModule m : getModules()) {
            modules.add(m.forTemplates());
            for (KbModuleComp mc : m.getModuleComponents())
                if (mc instanceof KbFuncdef) {
                    KbFuncdef func = (KbFuncdef)mc;
                    funcCount++;
                    boolean req = func.isAuthenticationRequired();
                    boolean opt = func.isAuthenticationOptional();
                    psbl |= req || opt;
                    only &= req;
                }
        }
        only &= funcCount > 0;
        ret.put("modules", modules);
        if (psbl)
            ret.put("authenticated", true);
        if (only)
            ret.put("authenticated_only", true);
        return ret;
    }
}

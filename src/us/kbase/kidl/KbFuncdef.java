package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KbFuncdef implements KbModuleComp {
	private String name;
	private boolean async;
	private String authentication;
	private String comment;
	private List<KbParameter> parameters;
	private List<KbParameter> returnType;
	private Map<?,?> data = null;
	
	public KbFuncdef() {}
	
	public KbFuncdef(String name, String comment) {
		this.name = name;
		this.async = false;
		this.comment = comment == null ? "" : comment;
		parameters = new ArrayList<KbParameter>();
		returnType = new ArrayList<KbParameter>();
	}

	public KbFuncdef loadFromMap(Map<?,?> data, String defaultAuth) throws KidlParseException {
		name = Utils.prop(data, "name");
		async = (0 != Utils.intPropFromString(data, "async"));
		authentication = Utils.prop(data, "authentication");  // defaultAuth was already involved on kidl stage
		comment = Utils.prop(data, "comment");
		parameters = loadParameters(Utils.propList(data, "parameters"), false);
		returnType = loadParameters(Utils.propList(data, "return_type"), true);
		this.data = data;
		return this;
	}
	
	private static List<KbParameter> loadParameters(List<?> inputList, boolean isReturn) throws KidlParseException {
		List<KbParameter> ret = new ArrayList<KbParameter>();
		for (Map<?,?> data : Utils.repareTypingMap(inputList)) {
			ret.add(new KbParameter().loadFromMap(data, isReturn, ret.size() + 1));
		}
		return Collections.unmodifiableList(ret);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAsync() {
		return async;
	}
	
	public String getAuthentication() {
		return authentication;
	}
	
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	
	public String getComment() {
		return comment;
	}
	
	public List<KbParameter> getParameters() {
		return parameters;
	}
	
	public List<KbParameter> getReturnType() {
		return returnType;
	}
	
	public Map<?, ?> getData() {
		return data;
	}
	
	private List<Object> toJson(List<KbParameter> list) {
		List<Object> ret = new ArrayList<Object>();
		for (KbParameter param : list)
			ret.add(param.toJson());
		return ret;
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Funcdef");
		ret.put("async", async ? "1" : "0");
		ret.put("authentication", authentication);
		ret.put("comment", comment);
		ret.put("name", name);
		ret.put("parameters", toJson(parameters));
		ret.put("return_type", toJson(returnType));
		return ret;
	}
}

package us.kbase.jkidl;

import java.util.Map;

import us.kbase.kidl.KbModule;

public interface IncludeProvider {
	public Map<String, KbModule> parseInclude(String includeLine) throws ParseException;
}

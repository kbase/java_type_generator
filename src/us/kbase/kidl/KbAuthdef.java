package us.kbase.kidl;

public class KbAuthdef implements KbModuleComp {
	private String type;
	
	public KbAuthdef(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	@Override
	public Object toJson(ObjectUsageInfo oui) {
		return "auth_default" + type;
	}
}

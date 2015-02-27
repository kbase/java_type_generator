package us.kbase.kidl;

/**
 * Class represents authentication module component or function modifier in spec-file.
 */
public class KbAuthdef implements KbModuleComp {
	private String type;
	
	public static final String OPTIONAL = "optional";
    public static final String REQUIRED = "required";
	
	public KbAuthdef(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	@Override
	public Object toJson() {
		return "auth_default" + type;
	}

    @Override
    public Object forTemplates() {
        throw new IllegalStateException("Templates are not yet supported for authentication");
    }
}

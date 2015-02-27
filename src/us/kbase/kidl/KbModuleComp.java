package us.kbase.kidl;

/**
 * Element of module declaration. This could be Typedef of Funcdef (or Auth string in some cases).
 * @author rsutormin
 */
public interface KbModuleComp {
	public Object toJson();
	public Object forTemplates();
}

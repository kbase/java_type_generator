package us.kbase.scripts.tests.test10.gwt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class FileTypeGWT implements Serializable {
    private String id;
    private String name;
    private List<String> valid_file_extensions;
    private LinkedHashMap<String,String> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValidFileExtensions() {
        return valid_file_extensions;
    }

    public void setValidFileExtensions(List<String> valid_file_extensions) {
        this.valid_file_extensions = valid_file_extensions;
    }

    public LinkedHashMap<String,String> getProperties() {
        return properties;
    }

    public void setProperties(LinkedHashMap<String,String> properties) {
        this.properties = properties;
    }
}

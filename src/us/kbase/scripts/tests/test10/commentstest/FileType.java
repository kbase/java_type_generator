
package us.kbase.scripts.tests.test10.commentstest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * <p>Original spec-file type: file_type</p>
 * <pre>
 * An object that encapsulates properties of a file type.  Note that unless otherwise stated, any
 * method which returns a file_type also includes all inhereted valid_file_extensions and properties.
 * file_type_id_ref id
 *     the unique string based id of this file type
 *     
 * string name
 *     the human readable long name of the file type
 * list<string> valid_file_extensions
 *     a list of extensions that are associated with this file type.  Note that in some cases this will
 *     include extensions that are inhereted from parent file types (e.g. a valid extension to a tab
 *     delimited file may be 'tab', but also 'txt' because it is a text file as well)
 * mapping<string,string> properties
 *     a simple mapping of key/value pairs used to describe attributes of the file type.  These in general
 *     can have any string as a key, but note that the following keys have been adopted for most file
 *     types by convention.  Note that if these properties are not defined, then they are inherited from
 *     a parent file_type object
 *         default-extension  -  gives the single default extension
 *         default-web-renderer  -  the name/id of the widget that should be used by default to view the
 *                                  contents of this file type
 * </pre>
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "name",
    "valid_file_extensions",
    "properties"
})
public class FileType {

    @JsonProperty("id")
    private java.lang.String id;
    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("valid_file_extensions")
    private List<java.lang.String> validFileExtensions = new ArrayList<java.lang.String>();
    @JsonProperty("properties")
    private Map<String, String> properties;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("id")
    public java.lang.String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(java.lang.String id) {
        this.id = id;
    }

    public FileType withId(java.lang.String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public FileType withName(java.lang.String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("valid_file_extensions")
    public List<java.lang.String> getValidFileExtensions() {
        return validFileExtensions;
    }

    @JsonProperty("valid_file_extensions")
    public void setValidFileExtensions(List<java.lang.String> validFileExtensions) {
        this.validFileExtensions = validFileExtensions;
    }

    public FileType withValidFileExtensions(List<java.lang.String> validFileExtensions) {
        this.validFileExtensions = validFileExtensions;
        return this;
    }

    @JsonProperty("properties")
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public FileType withProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

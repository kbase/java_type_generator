
package us.kbase.scripts.tests.test9.gwttest;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * <p>Original spec-file type: simple_struct</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "prop1",
    "prop2",
    "prop3"
})
public class SimpleStruct {

    @JsonProperty("prop1")
    private Integer prop1;
    @JsonProperty("prop2")
    private Double prop2;
    @JsonProperty("prop3")
    private String prop3;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("prop1")
    public Integer getProp1() {
        return prop1;
    }

    @JsonProperty("prop1")
    public void setProp1(Integer prop1) {
        this.prop1 = prop1;
    }

    public SimpleStruct withProp1(Integer prop1) {
        this.prop1 = prop1;
        return this;
    }

    @JsonProperty("prop2")
    public Double getProp2() {
        return prop2;
    }

    @JsonProperty("prop2")
    public void setProp2(Double prop2) {
        this.prop2 = prop2;
    }

    public SimpleStruct withProp2(Double prop2) {
        this.prop2 = prop2;
        return this;
    }

    @JsonProperty("prop3")
    public String getProp3() {
        return prop3;
    }

    @JsonProperty("prop3")
    public void setProp3(String prop3) {
        this.prop3 = prop3;
    }

    public SimpleStruct withProp3(String prop3) {
        this.prop3 = prop3;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

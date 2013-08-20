
package us.kbase.scripts.tests.test10.commentstest;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * <p>Original spec-file type: struct2</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "val1"
})
public class Struct2 {

    @JsonProperty("val1")
    private Integer val1;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("val1")
    public Integer getVal1() {
        return val1;
    }

    @JsonProperty("val1")
    public void setVal1(Integer val1) {
        this.val1 = val1;
    }

    public Struct2 withVal1(Integer val1) {
        this.val1 = val1;
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

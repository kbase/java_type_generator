
package us.kbase.scripts.tests.test4.boolandobjecttest;

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
import us.kbase.Tuple2;


/**
 * <p>Original spec-file type: bool_struct</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "val1",
    "val2",
    "val3",
    "val4"
})
public class BoolStruct {

    @JsonProperty("val1")
    private java.lang.Boolean val1;
    @JsonProperty("val2")
    private List<java.lang.Boolean> val2 = new ArrayList<java.lang.Boolean>();
    @JsonProperty("val3")
    private Map<String, Boolean> val3;
    @JsonProperty("val4")
    private Tuple2 <Boolean, Boolean> val4;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("val1")
    public java.lang.Boolean getVal1() {
        return val1;
    }

    @JsonProperty("val1")
    public void setVal1(java.lang.Boolean val1) {
        this.val1 = val1;
    }

    public BoolStruct withVal1(java.lang.Boolean val1) {
        this.val1 = val1;
        return this;
    }

    @JsonProperty("val2")
    public List<java.lang.Boolean> getVal2() {
        return val2;
    }

    @JsonProperty("val2")
    public void setVal2(List<java.lang.Boolean> val2) {
        this.val2 = val2;
    }

    public BoolStruct withVal2(List<java.lang.Boolean> val2) {
        this.val2 = val2;
        return this;
    }

    @JsonProperty("val3")
    public Map<String, Boolean> getVal3() {
        return val3;
    }

    @JsonProperty("val3")
    public void setVal3(Map<String, Boolean> val3) {
        this.val3 = val3;
    }

    public BoolStruct withVal3(Map<String, Boolean> val3) {
        this.val3 = val3;
        return this;
    }

    @JsonProperty("val4")
    public Tuple2 <Boolean, Boolean> getVal4() {
        return val4;
    }

    @JsonProperty("val4")
    public void setVal4(Tuple2 <Boolean, Boolean> val4) {
        this.val4 = val4;
    }

    public BoolStruct withVal4(Tuple2 <Boolean, Boolean> val4) {
        this.val4 = val4;
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

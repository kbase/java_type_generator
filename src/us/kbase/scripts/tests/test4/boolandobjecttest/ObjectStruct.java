
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
 * <p>Original spec-file type: object_struct</p>
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
public class ObjectStruct {

    @JsonProperty("val1")
    private us.kbase.UObject val1;
    @JsonProperty("val2")
    private List<us.kbase.UObject> val2 = new ArrayList<us.kbase.UObject>();
    @JsonProperty("val3")
    private Map<String, us.kbase.UObject> val3;
    @JsonProperty("val4")
    private Tuple2 <us.kbase.UObject, us.kbase.UObject> val4;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("val1")
    public us.kbase.UObject getVal1() {
        return val1;
    }

    @JsonProperty("val1")
    public void setVal1(us.kbase.UObject val1) {
        this.val1 = val1;
    }

    public ObjectStruct withVal1(us.kbase.UObject val1) {
        this.val1 = val1;
        return this;
    }

    @JsonProperty("val2")
    public List<us.kbase.UObject> getVal2() {
        return val2;
    }

    @JsonProperty("val2")
    public void setVal2(List<us.kbase.UObject> val2) {
        this.val2 = val2;
    }

    public ObjectStruct withVal2(List<us.kbase.UObject> val2) {
        this.val2 = val2;
        return this;
    }

    @JsonProperty("val3")
    public Map<String, us.kbase.UObject> getVal3() {
        return val3;
    }

    @JsonProperty("val3")
    public void setVal3(Map<String, us.kbase.UObject> val3) {
        this.val3 = val3;
    }

    public ObjectStruct withVal3(Map<String, us.kbase.UObject> val3) {
        this.val3 = val3;
        return this;
    }

    @JsonProperty("val4")
    public Tuple2 <us.kbase.UObject, us.kbase.UObject> getVal4() {
        return val4;
    }

    @JsonProperty("val4")
    public void setVal4(Tuple2 <us.kbase.UObject, us.kbase.UObject> val4) {
        this.val4 = val4;
    }

    public ObjectStruct withVal4(Tuple2 <us.kbase.UObject, us.kbase.UObject> val4) {
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

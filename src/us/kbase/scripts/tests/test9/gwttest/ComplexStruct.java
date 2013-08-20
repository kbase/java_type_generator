
package us.kbase.scripts.tests.test9.gwttest;

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
 * <p>Original spec-file type: complex_struct</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "val1",
    "val2",
    "val3"
})
public class ComplexStruct {

    @JsonProperty("val1")
    private List<Map<String, us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> val1 = new ArrayList<Map<String, us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>>();
    @JsonProperty("val2")
    private Map<String, List<us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> val2;
    @JsonProperty("val3")
    private us.kbase.Tuple2 <List<us.kbase.scripts.tests.test9.gwttest.SimpleStruct> , Map<String, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>> val3;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("val1")
    public List<Map<String, us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> getVal1() {
        return val1;
    }

    @JsonProperty("val1")
    public void setVal1(List<Map<String, us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> val1) {
        this.val1 = val1;
    }

    public ComplexStruct withVal1(List<Map<String, us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> val1) {
        this.val1 = val1;
        return this;
    }

    @JsonProperty("val2")
    public Map<String, List<us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> getVal2() {
        return val2;
    }

    @JsonProperty("val2")
    public void setVal2(Map<String, List<us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> val2) {
        this.val2 = val2;
    }

    public ComplexStruct withVal2(Map<String, List<us.kbase.Tuple2 <us.kbase.scripts.tests.test9.gwttest.SimpleStruct, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>>> val2) {
        this.val2 = val2;
        return this;
    }

    @JsonProperty("val3")
    public us.kbase.Tuple2 <List<us.kbase.scripts.tests.test9.gwttest.SimpleStruct> , Map<String, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>> getVal3() {
        return val3;
    }

    @JsonProperty("val3")
    public void setVal3(us.kbase.Tuple2 <List<us.kbase.scripts.tests.test9.gwttest.SimpleStruct> , Map<String, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>> val3) {
        this.val3 = val3;
    }

    public ComplexStruct withVal3(us.kbase.Tuple2 <List<us.kbase.scripts.tests.test9.gwttest.SimpleStruct> , Map<String, us.kbase.scripts.tests.test9.gwttest.SimpleStruct>> val3) {
        this.val3 = val3;
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

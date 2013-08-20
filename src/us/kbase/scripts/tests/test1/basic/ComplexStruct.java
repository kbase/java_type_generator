
package us.kbase.scripts.tests.test1.basic;

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
    "large_prop1",
    "large_prop2",
    "large_prop3",
    "large_prop4"
})
public class ComplexStruct {

    @JsonProperty("large_prop1")
    private List<us.kbase.scripts.tests.test1.basic.SimpleStruct> largeProp1 = new ArrayList<us.kbase.scripts.tests.test1.basic.SimpleStruct>();
    @JsonProperty("large_prop2")
    private Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct> largeProp2;
    @JsonProperty("large_prop3")
    private List<Map<String, List<us.kbase.scripts.tests.test1.basic.SimpleStruct>>> largeProp3 = new ArrayList<Map<String, List<us.kbase.scripts.tests.test1.basic.SimpleStruct>>>();
    @JsonProperty("large_prop4")
    private Map<String, List<Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct>>> largeProp4;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("large_prop1")
    public List<us.kbase.scripts.tests.test1.basic.SimpleStruct> getLargeProp1() {
        return largeProp1;
    }

    @JsonProperty("large_prop1")
    public void setLargeProp1(List<us.kbase.scripts.tests.test1.basic.SimpleStruct> largeProp1) {
        this.largeProp1 = largeProp1;
    }

    public ComplexStruct withLargeProp1(List<us.kbase.scripts.tests.test1.basic.SimpleStruct> largeProp1) {
        this.largeProp1 = largeProp1;
        return this;
    }

    @JsonProperty("large_prop2")
    public Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct> getLargeProp2() {
        return largeProp2;
    }

    @JsonProperty("large_prop2")
    public void setLargeProp2(Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct> largeProp2) {
        this.largeProp2 = largeProp2;
    }

    public ComplexStruct withLargeProp2(Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct> largeProp2) {
        this.largeProp2 = largeProp2;
        return this;
    }

    @JsonProperty("large_prop3")
    public List<Map<String, List<us.kbase.scripts.tests.test1.basic.SimpleStruct>>> getLargeProp3() {
        return largeProp3;
    }

    @JsonProperty("large_prop3")
    public void setLargeProp3(List<Map<String, List<us.kbase.scripts.tests.test1.basic.SimpleStruct>>> largeProp3) {
        this.largeProp3 = largeProp3;
    }

    public ComplexStruct withLargeProp3(List<Map<String, List<us.kbase.scripts.tests.test1.basic.SimpleStruct>>> largeProp3) {
        this.largeProp3 = largeProp3;
        return this;
    }

    @JsonProperty("large_prop4")
    public Map<String, List<Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct>>> getLargeProp4() {
        return largeProp4;
    }

    @JsonProperty("large_prop4")
    public void setLargeProp4(Map<String, List<Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct>>> largeProp4) {
        this.largeProp4 = largeProp4;
    }

    public ComplexStruct withLargeProp4(Map<String, List<Map<String, us.kbase.scripts.tests.test1.basic.SimpleStruct>>> largeProp4) {
        this.largeProp4 = largeProp4;
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

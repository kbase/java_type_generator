
package us.kbase.scripts.tests.test2.regulation;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import us.kbase.scripts.tests.test2.annotation.Gene;
import us.kbase.scripts.tests.test2.sequence.SequencePos;


/**
 * <p>Original spec-file type: binding_site</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "regulator",
    "binding_pos"
})
public class BindingSite {

    /**
     * <p>Original spec-file type: gene</p>
     * <pre>
     * Regulating gene
     * </pre>
     * 
     */
    @JsonProperty("regulator")
    private Gene regulator;
    /**
     * <p>Original spec-file type: sequence_pos</p>
     * <pre>
     * position of fragment on a sequence
     * </pre>
     * 
     */
    @JsonProperty("binding_pos")
    private SequencePos bindingPos;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * <p>Original spec-file type: gene</p>
     * <pre>
     * Regulating gene
     * </pre>
     * 
     */
    @JsonProperty("regulator")
    public Gene getRegulator() {
        return regulator;
    }

    /**
     * <p>Original spec-file type: gene</p>
     * <pre>
     * Regulating gene
     * </pre>
     * 
     */
    @JsonProperty("regulator")
    public void setRegulator(Gene regulator) {
        this.regulator = regulator;
    }

    public BindingSite withRegulator(Gene regulator) {
        this.regulator = regulator;
        return this;
    }

    /**
     * <p>Original spec-file type: sequence_pos</p>
     * <pre>
     * position of fragment on a sequence
     * </pre>
     * 
     */
    @JsonProperty("binding_pos")
    public SequencePos getBindingPos() {
        return bindingPos;
    }

    /**
     * <p>Original spec-file type: sequence_pos</p>
     * <pre>
     * position of fragment on a sequence
     * </pre>
     * 
     */
    @JsonProperty("binding_pos")
    public void setBindingPos(SequencePos bindingPos) {
        this.bindingPos = bindingPos;
    }

    public BindingSite withBindingPos(SequencePos bindingPos) {
        this.bindingPos = bindingPos;
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

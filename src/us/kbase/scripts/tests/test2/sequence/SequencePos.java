
package us.kbase.scripts.tests.test2.sequence;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * <p>Original spec-file type: sequence_pos</p>
 * <pre>
 * position of fragment on a sequence
 * </pre>
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "start",
    "stop",
    "sequence_id"
})
public class SequencePos {

    @JsonProperty("start")
    private Integer start;
    @JsonProperty("stop")
    private Integer stop;
    @JsonProperty("sequence_id")
    private Integer sequenceId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("start")
    public Integer getStart() {
        return start;
    }

    @JsonProperty("start")
    public void setStart(Integer start) {
        this.start = start;
    }

    public SequencePos withStart(Integer start) {
        this.start = start;
        return this;
    }

    @JsonProperty("stop")
    public Integer getStop() {
        return stop;
    }

    @JsonProperty("stop")
    public void setStop(Integer stop) {
        this.stop = stop;
    }

    public SequencePos withStop(Integer stop) {
        this.stop = stop;
        return this;
    }

    @JsonProperty("sequence_id")
    public Integer getSequenceId() {
        return sequenceId;
    }

    @JsonProperty("sequence_id")
    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    public SequencePos withSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
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

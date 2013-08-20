
package us.kbase.scripts.tests.test2.annotation;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import us.kbase.scripts.tests.test2.sequence.SequencePos;


/**
 * <p>Original spec-file type: gene</p>
 * <pre>
 * Regulating gene
 * </pre>
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "gene_id",
    "genome_id",
    "gene_name",
    "pos",
    "gene_descr"
})
public class Gene {

    @JsonProperty("gene_id")
    private Integer geneId;
    @JsonProperty("genome_id")
    private Integer genomeId;
    @JsonProperty("gene_name")
    private String geneName;
    /**
     * <p>Original spec-file type: sequence_pos</p>
     * <pre>
     * position of fragment on a sequence
     * </pre>
     * 
     */
    @JsonProperty("pos")
    private SequencePos pos;
    @JsonProperty("gene_descr")
    private String geneDescr;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("gene_id")
    public Integer getGeneId() {
        return geneId;
    }

    @JsonProperty("gene_id")
    public void setGeneId(Integer geneId) {
        this.geneId = geneId;
    }

    public Gene withGeneId(Integer geneId) {
        this.geneId = geneId;
        return this;
    }

    @JsonProperty("genome_id")
    public Integer getGenomeId() {
        return genomeId;
    }

    @JsonProperty("genome_id")
    public void setGenomeId(Integer genomeId) {
        this.genomeId = genomeId;
    }

    public Gene withGenomeId(Integer genomeId) {
        this.genomeId = genomeId;
        return this;
    }

    @JsonProperty("gene_name")
    public String getGeneName() {
        return geneName;
    }

    @JsonProperty("gene_name")
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public Gene withGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    /**
     * <p>Original spec-file type: sequence_pos</p>
     * <pre>
     * position of fragment on a sequence
     * </pre>
     * 
     */
    @JsonProperty("pos")
    public SequencePos getPos() {
        return pos;
    }

    /**
     * <p>Original spec-file type: sequence_pos</p>
     * <pre>
     * position of fragment on a sequence
     * </pre>
     * 
     */
    @JsonProperty("pos")
    public void setPos(SequencePos pos) {
        this.pos = pos;
    }

    public Gene withPos(SequencePos pos) {
        this.pos = pos;
        return this;
    }

    @JsonProperty("gene_descr")
    public String getGeneDescr() {
        return geneDescr;
    }

    @JsonProperty("gene_descr")
    public void setGeneDescr(String geneDescr) {
        this.geneDescr = geneDescr;
    }

    public Gene withGeneDescr(String geneDescr) {
        this.geneDescr = geneDescr;
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

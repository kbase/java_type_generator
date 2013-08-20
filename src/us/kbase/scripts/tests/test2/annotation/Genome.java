
package us.kbase.scripts.tests.test2.annotation;

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
import us.kbase.scripts.tests.test2.taxonomy.Taxon;


/**
 * <p>Original spec-file type: genome</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "genome_id",
    "genome_name",
    "sequence_id",
    "taxon",
    "genes",
    "genes_by_names"
})
public class Genome {

    @JsonProperty("genome_id")
    private Integer genomeId;
    @JsonProperty("genome_name")
    private java.lang.String genomeName;
    @JsonProperty("sequence_id")
    private Integer sequenceId;
    /**
     * <p>Original spec-file type: taxon</p>
     * 
     * 
     */
    @JsonProperty("taxon")
    private Taxon taxon;
    @JsonProperty("genes")
    private List<us.kbase.scripts.tests.test2.annotation.Gene> genes = new ArrayList<us.kbase.scripts.tests.test2.annotation.Gene>();
    @JsonProperty("genes_by_names")
    private Map<String, List<us.kbase.scripts.tests.test2.annotation.Gene>> genesByNames;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("genome_id")
    public Integer getGenomeId() {
        return genomeId;
    }

    @JsonProperty("genome_id")
    public void setGenomeId(Integer genomeId) {
        this.genomeId = genomeId;
    }

    public Genome withGenomeId(Integer genomeId) {
        this.genomeId = genomeId;
        return this;
    }

    @JsonProperty("genome_name")
    public java.lang.String getGenomeName() {
        return genomeName;
    }

    @JsonProperty("genome_name")
    public void setGenomeName(java.lang.String genomeName) {
        this.genomeName = genomeName;
    }

    public Genome withGenomeName(java.lang.String genomeName) {
        this.genomeName = genomeName;
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

    public Genome withSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
        return this;
    }

    /**
     * <p>Original spec-file type: taxon</p>
     * 
     * 
     */
    @JsonProperty("taxon")
    public Taxon getTaxon() {
        return taxon;
    }

    /**
     * <p>Original spec-file type: taxon</p>
     * 
     * 
     */
    @JsonProperty("taxon")
    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public Genome withTaxon(Taxon taxon) {
        this.taxon = taxon;
        return this;
    }

    @JsonProperty("genes")
    public List<us.kbase.scripts.tests.test2.annotation.Gene> getGenes() {
        return genes;
    }

    @JsonProperty("genes")
    public void setGenes(List<us.kbase.scripts.tests.test2.annotation.Gene> genes) {
        this.genes = genes;
    }

    public Genome withGenes(List<us.kbase.scripts.tests.test2.annotation.Gene> genes) {
        this.genes = genes;
        return this;
    }

    @JsonProperty("genes_by_names")
    public Map<String, List<us.kbase.scripts.tests.test2.annotation.Gene>> getGenesByNames() {
        return genesByNames;
    }

    @JsonProperty("genes_by_names")
    public void setGenesByNames(Map<String, List<us.kbase.scripts.tests.test2.annotation.Gene>> genesByNames) {
        this.genesByNames = genesByNames;
    }

    public Genome withGenesByNames(Map<String, List<us.kbase.scripts.tests.test2.annotation.Gene>> genesByNames) {
        this.genesByNames = genesByNames;
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

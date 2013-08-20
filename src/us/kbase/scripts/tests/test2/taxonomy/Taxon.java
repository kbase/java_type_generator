
package us.kbase.scripts.tests.test2.taxonomy;

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
 * <p>Original spec-file type: taxon</p>
 * 
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "parent_id",
    "name",
    "sub_taxons"
})
public class Taxon {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sub_taxons")
    private List<Integer> subTaxons = new ArrayList<Integer>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public Taxon withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("parent_id")
    public Integer getParentId() {
        return parentId;
    }

    @JsonProperty("parent_id")
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Taxon withParentId(Integer parentId) {
        this.parentId = parentId;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Taxon withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("sub_taxons")
    public List<Integer> getSubTaxons() {
        return subTaxons;
    }

    @JsonProperty("sub_taxons")
    public void setSubTaxons(List<Integer> subTaxons) {
        this.subTaxons = subTaxons;
    }

    public Taxon withSubTaxons(List<Integer> subTaxons) {
        this.subTaxons = subTaxons;
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

package us.kbase.scripts.tests.test2.gwt;

import java.io.Serializable;
import java.util.List;

public class TaxonGWT implements Serializable {
    private Integer id;
    private Integer parent_id;
    private String name;
    private List<Integer> sub_taxons;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parent_id;
    }

    public void setParentId(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getSubTaxons() {
        return sub_taxons;
    }

    public void setSubTaxons(List<Integer> sub_taxons) {
        this.sub_taxons = sub_taxons;
    }
}

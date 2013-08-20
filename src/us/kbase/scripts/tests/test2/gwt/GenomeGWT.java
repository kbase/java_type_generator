package us.kbase.scripts.tests.test2.gwt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class GenomeGWT implements Serializable {
    private Integer genome_id;
    private String genome_name;
    private Integer sequence_id;
    private TaxonGWT taxon;
    private List<GeneGWT> genes;
    private LinkedHashMap<String,List<GeneGWT>> genes_by_names;

    public Integer getGenomeId() {
        return genome_id;
    }

    public void setGenomeId(Integer genome_id) {
        this.genome_id = genome_id;
    }

    public String getGenomeName() {
        return genome_name;
    }

    public void setGenomeName(String genome_name) {
        this.genome_name = genome_name;
    }

    public Integer getSequenceId() {
        return sequence_id;
    }

    public void setSequenceId(Integer sequence_id) {
        this.sequence_id = sequence_id;
    }

    public TaxonGWT getTaxon() {
        return taxon;
    }

    public void setTaxon(TaxonGWT taxon) {
        this.taxon = taxon;
    }

    public List<GeneGWT> getGenes() {
        return genes;
    }

    public void setGenes(List<GeneGWT> genes) {
        this.genes = genes;
    }

    public LinkedHashMap<String,List<GeneGWT>> getGenesByNames() {
        return genes_by_names;
    }

    public void setGenesByNames(LinkedHashMap<String,List<GeneGWT>> genes_by_names) {
        this.genes_by_names = genes_by_names;
    }
}

package us.kbase.scripts.tests.test2.gwt;

import java.io.Serializable;

public class GeneGWT implements Serializable {
    private Integer gene_id;
    private Integer genome_id;
    private String gene_name;
    private SequencePosGWT pos;
    private String gene_descr;

    public Integer getGeneId() {
        return gene_id;
    }

    public void setGeneId(Integer gene_id) {
        this.gene_id = gene_id;
    }

    public Integer getGenomeId() {
        return genome_id;
    }

    public void setGenomeId(Integer genome_id) {
        this.genome_id = genome_id;
    }

    public String getGeneName() {
        return gene_name;
    }

    public void setGeneName(String gene_name) {
        this.gene_name = gene_name;
    }

    public SequencePosGWT getPos() {
        return pos;
    }

    public void setPos(SequencePosGWT pos) {
        this.pos = pos;
    }

    public String getGeneDescr() {
        return gene_descr;
    }

    public void setGeneDescr(String gene_descr) {
        this.gene_descr = gene_descr;
    }
}

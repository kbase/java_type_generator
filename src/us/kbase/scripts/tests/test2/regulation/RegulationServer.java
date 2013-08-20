package us.kbase.scripts.tests.test2.regulation;

import java.util.List;
import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;
import us.kbase.Tuple2;
import us.kbase.Tuple3;
import us.kbase.scripts.tests.test2.annotation.Gene;
import us.kbase.scripts.tests.test2.annotation.Genome;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: Regulation</p>
 * <pre>
 * </pre>
 */
public class RegulationServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public RegulationServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: get_genome</p>
     * <pre>
     * </pre>
     * @param   genome   Original type "genome" (see {@link us.kbase.scripts.tests.test2.annotation.Genome Genome} for details)
     */
    @JsonServerMethod(rpc = "Regulation.get_genome", tuple = true)
    public Tuple2<String, Genome> getGenome(String genomeName, Genome genome) throws Exception {
        String return1 = null;
        Genome return2 = null;
        //BEGIN get_genome
        return1 = genomeName;
        return2 = genome;
        //END get_genome
        Tuple2<String, Genome> returnVal = new Tuple2<String, Genome>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: get_regulator_binding_sites_and_genes</p>
     * <pre>
     * </pre>
     * @param   regulatingGene   Original type "regulator" (Regulating gene) &rarr; Original type "gene" (see {@link us.kbase.scripts.tests.test2.annotation.Gene Gene} for details)
     */
    @JsonServerMethod(rpc = "Regulation.get_regulator_binding_sites_and_genes", tuple = true)
    public Tuple3<Gene, List<BindingSite>, List<Gene>> getRegulatorBindingSitesAndGenes(Gene regulatingGene, List<BindingSite> retBindingSite, List<Gene> retGenes) throws Exception {
        Gene return1 = null;
        List<BindingSite> return2 = null;
        List<Gene> return3 = null;
        //BEGIN get_regulator_binding_sites_and_genes
        return1 = regulatingGene;
        return2 = retBindingSite;
        return3 = retGenes;
        //END get_regulator_binding_sites_and_genes
        Tuple3<Gene, List<BindingSite>, List<Gene>> returnVal = new Tuple3<Gene, List<BindingSite>, List<Gene>>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        returnVal.setE3(return3);
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new RegulationServer().startupServer(Integer.parseInt(args[0]));
    }
}

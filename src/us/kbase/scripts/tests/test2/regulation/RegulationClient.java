package us.kbase.scripts.tests.test2.regulation;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;
import us.kbase.Tuple2;
import us.kbase.Tuple3;
import us.kbase.scripts.tests.test2.annotation.Gene;
import us.kbase.scripts.tests.test2.annotation.Genome;

/**
 * <p>Original spec-file module name: Regulation</p>
 * <pre>
 * </pre>
 */
public class RegulationClient {
    private JsonClientCaller caller;

    public RegulationClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: get_genome</p>
     * <pre>
     * </pre>
     * @param   genome   Original type "genome" (see {@link us.kbase.scripts.tests.test2.annotation.Genome Genome} for details)
     */
    public Tuple2<String, Genome> getGenome(String genomeName, Genome genome) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(genomeName);
        args.add(genome);
        TypeReference<Tuple2<String, Genome>> retType = new TypeReference<Tuple2<String, Genome>>() {};
        Tuple2<String, Genome> res = caller.jsonrpcCall("Regulation.get_genome", args, retType, true, false);
        return res;
    }

    /**
     * <p>Original spec-file function name: get_regulator_binding_sites_and_genes</p>
     * <pre>
     * </pre>
     * @param   regulatingGene   Original type "regulator" (Regulating gene) &rarr; Original type "gene" (see {@link us.kbase.scripts.tests.test2.annotation.Gene Gene} for details)
     */
    public Tuple3<Gene, List<BindingSite>, List<Gene>> getRegulatorBindingSitesAndGenes(Gene regulatingGene, List<BindingSite> retBindingSite, List<Gene> retGenes) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(regulatingGene);
        args.add(retBindingSite);
        args.add(retGenes);
        TypeReference<Tuple3<Gene, List<BindingSite>, List<Gene>>> retType = new TypeReference<Tuple3<Gene, List<BindingSite>, List<Gene>>>() {};
        Tuple3<Gene, List<BindingSite>, List<Gene>> res = caller.jsonrpcCall("Regulation.get_regulator_binding_sites_and_genes", args, retType, true, false);
        return res;
    }
}

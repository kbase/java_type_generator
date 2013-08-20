package us.kbase.scripts.tests.test3.tuplestest;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;
import us.kbase.Tuple2;
import us.kbase.Tuple3;

/**
 * <p>Original spec-file module name: TuplesTest</p>
 * <pre>
 * </pre>
 */
public class TuplestestClient {
    private JsonClientCaller caller;

    public TuplestestClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: simple_call</p>
     * <pre>
     * </pre>
     * @param   val   Original type "outer_tuple"
     * @return   Original type "outer_tuple"
     */
    public Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> simpleCall(Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> val) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val);
        TypeReference<List<Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>>>> retType = new TypeReference<List<Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>>>>() {};
        List<Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>>> res = caller.jsonrpcCall("TuplesTest.simple_call", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: complex_call</p>
     * <pre>
     * </pre>
     */
    public Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> complexCall(List<Map<String,Tuple3<Integer, Double, String>>> val1, Map<String,List<Tuple3<Integer, Double, String>>> val2) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val1);
        args.add(val2);
        TypeReference<Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>>> retType = new TypeReference<Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>>>() {};
        Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> res = caller.jsonrpcCall("TuplesTest.complex_call", args, retType, true, false);
        return res;
    }
}

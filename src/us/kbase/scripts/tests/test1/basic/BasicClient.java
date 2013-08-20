package us.kbase.scripts.tests.test1.basic;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;
import us.kbase.Tuple2;
import us.kbase.Tuple4;

/**
 * <p>Original spec-file module name: Basic</p>
 * <pre>
 * </pre>
 */
public class BasicClient {
    private JsonClientCaller caller;

    public BasicClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: one_simple_param</p>
     * <pre>
     * </pre>
     */
    public Integer oneSimpleParam(Integer val) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val);
        TypeReference<List<Integer>> retType = new TypeReference<List<Integer>>() {};
        List<Integer> res = caller.jsonrpcCall("Basic.one_simple_param", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: one_complex_param</p>
     * <pre>
     * </pre>
     * @param   val2   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test1.basic.ComplexStruct ComplexStruct} for details)
     * @return   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test1.basic.ComplexStruct ComplexStruct} for details)
     */
    public ComplexStruct oneComplexParam(ComplexStruct val2) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val2);
        TypeReference<List<ComplexStruct>> retType = new TypeReference<List<ComplexStruct>>() {};
        List<ComplexStruct> res = caller.jsonrpcCall("Basic.one_complex_param", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: many_simple_params</p>
     * <pre>
     * </pre>
     */
    public Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>> manySimpleParams(Integer val1, Double val2, String val3, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>> val4) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val1);
        args.add(val2);
        args.add(val3);
        args.add(val4);
        TypeReference<Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>>> retType = new TypeReference<Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>>>() {};
        Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>> res = caller.jsonrpcCall("Basic.many_simple_params", args, retType, true, false);
        return res;
    }

    /**
     * <p>Original spec-file function name: many_complex_params</p>
     * <pre>
     * </pre>
     * @param   simpleVal   Original type "simple_struct" (see {@link us.kbase.scripts.tests.test1.basic.SimpleStruct SimpleStruct} for details)
     * @param   complexVal   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test1.basic.ComplexStruct ComplexStruct} for details)
     */
    public Tuple2<SimpleStruct, ComplexStruct> manyComplexParams(SimpleStruct simpleVal, ComplexStruct complexVal) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(simpleVal);
        args.add(complexVal);
        TypeReference<Tuple2<SimpleStruct, ComplexStruct>> retType = new TypeReference<Tuple2<SimpleStruct, ComplexStruct>>() {};
        Tuple2<SimpleStruct, ComplexStruct> res = caller.jsonrpcCall("Basic.many_complex_params", args, retType, true, false);
        return res;
    }
}

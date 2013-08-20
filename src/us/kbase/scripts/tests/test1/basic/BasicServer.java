package us.kbase.scripts.tests.test1.basic;

import java.util.List;
import java.util.Map;
import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;
import us.kbase.Tuple2;
import us.kbase.Tuple4;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: Basic</p>
 * <pre>
 * </pre>
 */
public class BasicServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public BasicServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: one_simple_param</p>
     * <pre>
     * </pre>
     */
    @JsonServerMethod(rpc = "Basic.one_simple_param")
    public Integer oneSimpleParam(Integer val) throws Exception {
        Integer returnVal = null;
        //BEGIN one_simple_param
        returnVal = val;
        //END one_simple_param
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: one_complex_param</p>
     * <pre>
     * </pre>
     * @param   val2   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test1.basic.ComplexStruct ComplexStruct} for details)
     * @return   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test1.basic.ComplexStruct ComplexStruct} for details)
     */
    @JsonServerMethod(rpc = "Basic.one_complex_param")
    public ComplexStruct oneComplexParam(ComplexStruct val2) throws Exception {
        ComplexStruct returnVal = null;
        //BEGIN one_complex_param
        returnVal = val2;
        //END one_complex_param
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: many_simple_params</p>
     * <pre>
     * </pre>
     */
    @JsonServerMethod(rpc = "Basic.many_simple_params", tuple = true)
    public Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>> manySimpleParams(Integer val1, Double val2, String val3, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>> val4) throws Exception {
        Integer return1 = null;
        Double return2 = null;
        String return3 = null;
        Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>> return4 = null;
        //BEGIN many_simple_params
        return1 = val1;
        return2 = val2;
        return3 = val3;
        return4 = val4;
        //END many_simple_params
        Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>> returnVal = new Tuple4<Integer, Double, String, Tuple2<List<Map<String,Integer>>, Map<String,List<Double>>>>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        returnVal.setE3(return3);
        returnVal.setE4(return4);
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: many_complex_params</p>
     * <pre>
     * </pre>
     * @param   simpleVal   Original type "simple_struct" (see {@link us.kbase.scripts.tests.test1.basic.SimpleStruct SimpleStruct} for details)
     * @param   complexVal   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test1.basic.ComplexStruct ComplexStruct} for details)
     */
    @JsonServerMethod(rpc = "Basic.many_complex_params", tuple = true)
    public Tuple2<SimpleStruct, ComplexStruct> manyComplexParams(SimpleStruct simpleVal, ComplexStruct complexVal) throws Exception {
        SimpleStruct return1 = null;
        ComplexStruct return2 = null;
        //BEGIN many_complex_params
        return1 = simpleVal;
        return2 = complexVal;
        //END many_complex_params
        Tuple2<SimpleStruct, ComplexStruct> returnVal = new Tuple2<SimpleStruct, ComplexStruct>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new BasicServer().startupServer(Integer.parseInt(args[0]));
    }
}

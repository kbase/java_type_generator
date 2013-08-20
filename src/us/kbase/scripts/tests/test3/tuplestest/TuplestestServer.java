package us.kbase.scripts.tests.test3.tuplestest;

import java.util.List;
import java.util.Map;
import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;
import us.kbase.Tuple2;
import us.kbase.Tuple3;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: TuplesTest</p>
 * <pre>
 * </pre>
 */
public class TuplestestServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public TuplestestServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: simple_call</p>
     * <pre>
     * </pre>
     * @param   val   Original type "outer_tuple"
     * @return   Original type "outer_tuple"
     */
    @JsonServerMethod(rpc = "TuplesTest.simple_call")
    public Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> simpleCall(Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> val) throws Exception {
        Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> returnVal = null;
        //BEGIN simple_call
        returnVal = val;
        //END simple_call
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: complex_call</p>
     * <pre>
     * </pre>
     */
    @JsonServerMethod(rpc = "TuplesTest.complex_call", tuple = true)
    public Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> complexCall(List<Map<String,Tuple3<Integer, Double, String>>> val1, Map<String,List<Tuple3<Integer, Double, String>>> val2) throws Exception {
        List<Map<String,Tuple3<Integer, Double, String>>> return1 = null;
        Map<String,List<Tuple3<Integer, Double, String>>> return2 = null;
        //BEGIN complex_call
        return1 = val1;
        return2 = val2;
        //END complex_call
        Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>> returnVal = new Tuple2<List<Map<String,Tuple3<Integer, Double, String>>>, Map<String,List<Tuple3<Integer, Double, String>>>>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new TuplestestServer().startupServer(Integer.parseInt(args[0]));
    }
}

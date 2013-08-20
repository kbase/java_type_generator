package us.kbase.scripts.tests.test9.gwttest;

import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: GwtTest</p>
 * <pre>
 * </pre>
 */
public class GwttestServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public GwttestServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: one_complex_param</p>
     * <pre>
     * </pre>
     * @param   val   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test9.gwttest.ComplexStruct ComplexStruct} for details)
     * @return   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test9.gwttest.ComplexStruct ComplexStruct} for details)
     */
    @JsonServerMethod(rpc = "GwtTest.one_complex_param")
    public ComplexStruct oneComplexParam(ComplexStruct val) throws Exception {
        ComplexStruct returnVal = null;
        //BEGIN one_complex_param
        returnVal = val;
        //END one_complex_param
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new GwttestServer().startupServer(Integer.parseInt(args[0]));
    }
}

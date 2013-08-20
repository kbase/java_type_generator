package us.kbase.scripts.tests.test4.boolandobjecttest;

import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;
import us.kbase.Tuple2;
import us.kbase.UObject;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: BoolAndObjectTest</p>
 * <pre>
 * </pre>
 */
public class BoolandobjecttestServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public BoolandobjecttestServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: object_check</p>
     * <pre>
     * </pre>
     * @param   simple   Original type "object"
     * @param   complex   Original type "object_struct" (see {@link us.kbase.scripts.tests.test4.boolandobjecttest.ObjectStruct ObjectStruct} for details)
     */
    @JsonServerMethod(rpc = "BoolAndObjectTest.object_check", tuple = true)
    public Tuple2<UObject, ObjectStruct> objectCheck(UObject simple, ObjectStruct complex) throws Exception {
        UObject return1 = null;
        ObjectStruct return2 = null;
        //BEGIN object_check
        return1 = simple;
        return2 = complex;
        //END object_check
        Tuple2<UObject, ObjectStruct> returnVal = new Tuple2<UObject, ObjectStruct>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new BoolandobjecttestServer().startupServer(Integer.parseInt(args[0]));
    }
}

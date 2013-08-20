package us.kbase.scripts.tests.test7.emptyargs;

import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: EmptyArgs</p>
 * <pre>
 * </pre>
 */
public class EmptyargsServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public EmptyargsServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: get_object</p>
     * <pre>
     * </pre>
     */
    @JsonServerMethod(rpc = "EmptyArgs.get_object")
    public void getObject() throws Exception {
        //BEGIN get_object
        //END get_object
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new EmptyargsServer().startupServer(Integer.parseInt(args[0]));
    }
}

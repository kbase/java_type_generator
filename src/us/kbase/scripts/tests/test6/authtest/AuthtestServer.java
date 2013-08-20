package us.kbase.scripts.tests.test6.authtest;

import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;
import us.kbase.auth.AuthToken;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: AuthTest</p>
 * <pre>
 * </pre>
 */
public class AuthtestServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public AuthtestServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: call_with_auth</p>
     * <pre>
     * </pre>
     */
    @JsonServerMethod(rpc = "AuthTest.call_with_auth")
    public String callWithAuth(String val, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN call_with_auth
        returnVal = val;
        //END call_with_auth
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: call_with_opt_auth</p>
     * <pre>
     * </pre>
     */
    @JsonServerMethod(rpc = "AuthTest.call_with_opt_auth", authOptional=true)
    public String callWithOptAuth(String val, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN call_with_opt_auth
        returnVal = val;
        //END call_with_opt_auth
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new AuthtestServer().startupServer(Integer.parseInt(args[0]));
    }
}

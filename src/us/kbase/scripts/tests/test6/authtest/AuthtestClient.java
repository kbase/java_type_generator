package us.kbase.scripts.tests.test6.authtest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;
import us.kbase.auth.TokenFormatException;

/**
 * <p>Original spec-file module name: AuthTest</p>
 * <pre>
 * </pre>
 */
public class AuthtestClient {
    private JsonClientCaller caller;

    public AuthtestClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    public AuthtestClient(String url, String token) throws
            MalformedURLException, IOException, TokenFormatException {
        caller = new JsonClientCaller(url, token);
    }

    public AuthtestClient(String url, String user, String password) throws MalformedURLException {
        caller = new JsonClientCaller(url, user, password);
    }

    public boolean isAuthAllowedForHttp() {
        return caller.isAuthAllowedForHttp();
    }

    public void setAuthAllowedForHttp(boolean isAuthAllowedForHttp) {
        caller.setAuthAllowedForHttp(isAuthAllowedForHttp);
    }

    /**
     * <p>Original spec-file function name: call_with_auth</p>
     * <pre>
     * </pre>
     */
    public String callWithAuth(String val) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("AuthTest.call_with_auth", args, retType, true, true);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: call_with_opt_auth</p>
     * <pre>
     * </pre>
     */
    public String callWithOptAuth(String val) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("AuthTest.call_with_opt_auth", args, retType, true, false);
        return res.get(0);
    }
}

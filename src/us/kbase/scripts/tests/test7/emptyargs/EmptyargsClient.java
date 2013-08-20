package us.kbase.scripts.tests.test7.emptyargs;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;

/**
 * <p>Original spec-file module name: EmptyArgs</p>
 * <pre>
 * </pre>
 */
public class EmptyargsClient {
    private JsonClientCaller caller;

    public EmptyargsClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: get_object</p>
     * <pre>
     * </pre>
     */
    public void getObject() throws Exception {
        List<Object> args = new ArrayList<Object>();
        TypeReference<Object> retType = new TypeReference<Object>() {};
        caller.jsonrpcCall("EmptyArgs.get_object", args, retType, false, false);
    }
}

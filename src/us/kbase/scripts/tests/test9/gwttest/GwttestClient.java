package us.kbase.scripts.tests.test9.gwttest;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;

/**
 * <p>Original spec-file module name: GwtTest</p>
 * <pre>
 * </pre>
 */
public class GwttestClient {
    private JsonClientCaller caller;

    public GwttestClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: one_complex_param</p>
     * <pre>
     * </pre>
     * @param   val   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test9.gwttest.ComplexStruct ComplexStruct} for details)
     * @return   Original type "complex_struct" (see {@link us.kbase.scripts.tests.test9.gwttest.ComplexStruct ComplexStruct} for details)
     */
    public ComplexStruct oneComplexParam(ComplexStruct val) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(val);
        TypeReference<List<ComplexStruct>> retType = new TypeReference<List<ComplexStruct>>() {};
        List<ComplexStruct> res = caller.jsonrpcCall("GwtTest.one_complex_param", args, retType, true, false);
        return res.get(0);
    }
}

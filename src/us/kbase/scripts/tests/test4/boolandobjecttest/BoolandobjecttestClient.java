package us.kbase.scripts.tests.test4.boolandobjecttest;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;
import us.kbase.Tuple2;
import us.kbase.UObject;

/**
 * <p>Original spec-file module name: BoolAndObjectTest</p>
 * <pre>
 * </pre>
 */
public class BoolandobjecttestClient {
    private JsonClientCaller caller;

    public BoolandobjecttestClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: object_check</p>
     * <pre>
     * </pre>
     * @param   simple   Original type "object"
     * @param   complex   Original type "object_struct" (see {@link us.kbase.scripts.tests.test4.boolandobjecttest.ObjectStruct ObjectStruct} for details)
     */
    public Tuple2<UObject, ObjectStruct> objectCheck(UObject simple, ObjectStruct complex) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(simple);
        args.add(complex);
        TypeReference<Tuple2<UObject, ObjectStruct>> retType = new TypeReference<Tuple2<UObject, ObjectStruct>>() {};
        Tuple2<UObject, ObjectStruct> res = caller.jsonrpcCall("BoolAndObjectTest.object_check", args, retType, true, false);
        return res;
    }
}

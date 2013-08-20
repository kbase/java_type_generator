package us.kbase.scripts.tests.test10.commentstest;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.type.TypeReference;
import us.kbase.JsonClientCaller;
import us.kbase.Tuple2;
import us.kbase.Tuple5;

/**
 * <p>Original spec-file module name: CommentsTest</p>
 * <pre>
 * KBase File Type Manager Service
 * This service tracks the type of files that KBase software recognizes and
 * properties of these file types (such as acceptable file extensions).  File types
 * exist in a hierarchy (e.g. XML is a type of TEXT file) and are uniquely identified
 * by a short unique string id.  File types are loosely coupled to different types
 * of data supported by KBase.
 * In the future, this service may provide some validation capabilities to ensure
 * that a given file matches some basic properties of  file type, e.g. a valid XML
 * document.
 * created 10/18/2012 - msneddon
 * </pre>
 */
public class CommentstestClient {
    private JsonClientCaller caller;

    public CommentstestClient(String url) throws MalformedURLException {
        caller = new JsonClientCaller(url);
    }

    /**
     * <p>Original spec-file function name: get_this_file_type_only</p>
     * <pre>
     * Returns the specified file_type object with THIS file_type object's extensions and properties ONLY,
     * not any of the extensions or properties inhereted by a parent. Therefore be careful since you may not
     * have all valid file extensions and properties for this file type!
     * </pre>
     * @param   id   Original type "file_type_id_ref" (Reference to file type which is necessary for complicated network of cross-references) &rarr; Original type "file_type_id" (The unique ID of a file type, which cannot contain any spaces (e.g. file, text, html))
     * @param   type   Original type "file_type_ref2" (This reference certainly should reflect some purpose of developer.) &rarr; Original type "file_type_ref" &rarr; Original type "file_type" (see {@link us.kbase.scripts.tests.test10.commentstest.FileType FileType} for details)
     * @param   val5   Original type "my_tuple" (Testing tuple comment)
     */
    public Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>> getThisFileTypeOnly(String id, FileType type, List<String> val3, String val4, Tuple2<String, Map<String,String>> val5) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(id);
        args.add(type);
        args.add(val3);
        args.add(val4);
        args.add(val5);
        TypeReference<Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>>> retType = new TypeReference<Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>>>() {};
        Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>> res = caller.jsonrpcCall("CommentsTest.get_this_file_type_only", args, retType, true, false);
        return res;
    }

    /**
     * <p>Original spec-file function name: second_method</p>
     * <pre>
     * </pre>
     * @param   arg   Original type "struct2" (see {@link us.kbase.scripts.tests.test10.commentstest.Struct2 Struct2} for details)
     * @return   Original type "struct2" (see {@link us.kbase.scripts.tests.test10.commentstest.Struct2 Struct2} for details)
     */
    public Struct2 secondMethod(Struct2 arg) throws Exception {
        List<Object> args = new ArrayList<Object>();
        args.add(arg);
        TypeReference<List<Struct2>> retType = new TypeReference<List<Struct2>>() {};
        List<Struct2> res = caller.jsonrpcCall("CommentsTest.second_method", args, retType, true, false);
        return res.get(0);
    }
}

package us.kbase.scripts.tests.test10.commentstest;

import java.util.List;
import java.util.Map;
import us.kbase.JsonServerMethod;
import us.kbase.JsonServerServlet;
import us.kbase.Tuple2;
import us.kbase.Tuple5;

//BEGIN_HEADER
//END_HEADER

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
public class CommentstestServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    //END_CLASS_HEADER

    public CommentstestServer() throws Exception {
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
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
    @JsonServerMethod(rpc = "CommentsTest.get_this_file_type_only", tuple = true)
    public Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>> getThisFileTypeOnly(String id, FileType type, List<String> val3, String val4, Tuple2<String, Map<String,String>> val5) throws Exception {
        String return1 = null;
        FileType return2 = null;
        List<String> return3 = null;
        String return4 = null;
        Tuple2<String, Map<String,String>> return5 = null;
        //BEGIN get_this_file_type_only
        return1 = id;
        return2 = type;
        return3 = val3;
        return4 = val4;
        return5 = val5;
        //END get_this_file_type_only
        Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>> returnVal = new Tuple5<String, FileType, List<String>, String, Tuple2<String, Map<String,String>>>();
        returnVal.setE1(return1);
        returnVal.setE2(return2);
        returnVal.setE3(return3);
        returnVal.setE4(return4);
        returnVal.setE5(return5);
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: second_method</p>
     * <pre>
     * </pre>
     * @param   arg   Original type "struct2" (see {@link us.kbase.scripts.tests.test10.commentstest.Struct2 Struct2} for details)
     * @return   Original type "struct2" (see {@link us.kbase.scripts.tests.test10.commentstest.Struct2 Struct2} for details)
     */
    @JsonServerMethod(rpc = "CommentsTest.second_method")
    public Struct2 secondMethod(Struct2 arg) throws Exception {
        Struct2 returnVal = null;
        //BEGIN second_method
        returnVal = arg;
        //END second_method
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new CommentstestServer().startupServer(Integer.parseInt(args[0]));
    }
}

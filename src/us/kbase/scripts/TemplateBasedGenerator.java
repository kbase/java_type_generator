package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.kidl.test.KidlTest;
import us.kbase.templates.TemplateFormatter;

public class TemplateBasedGenerator {
    
    public static void main(String[] args) throws Exception {
        test("ws");
    }
    
    private static void test(String specName) throws Exception {
        File spec = new File("./other/"+specName+".spec");
        FileReader fr = new FileReader(spec);
        String defUrl = "https://kbase.us/services/ws";
        File outDir = new File("temp", specName);
        generate(fr, defUrl, "JsVal", "ClientVal", null, outDir);
        File testDir = new File("other", specName);
        cmpFiles(outDir, testDir, "JsVal.js");
    }
    
    private static void cmpFiles(File dir1, File dir2, String fileName) throws Exception {
        String text1 = TextUtils.readFileText(new File(dir1, fileName));
        String text2 = TextUtils.readFileText(new File(dir2, fileName));
        if (KidlTest.isDiff(text1, text2)) {
            System.out.println("Files [" + fileName + "] are different");
            KidlTest.showDiff(text1, text2);
        }
    }
    
    public static void generate(Reader specReader, String defaultUrl, String jsName,
            String perlClientName, IncludeProvider ip, File outDir) throws Exception {
        if (ip == null)
            ip = new FileIncludeProvider(new File("."));
        List<KbService> srvs = KidlParser.parseSpec(KidlParser.parseSpecInt(specReader, null, ip));
        KbService service = srvs.get(0);
        Map<String, Object> context = service.forTemplates();
        if (defaultUrl != null)
            context.put("default_service_url", defaultUrl);
        context.put("client_package_name", perlClientName);
        if (!outDir.exists())
            outDir.mkdirs();
        File jsClient = new File(outDir, jsName + ".js");
        TemplateFormatter.formatTemplate("javascript_client", context, jsClient);
        File perlClient = new File(outDir, perlClientName + ".pm");
        TemplateFormatter.formatTemplate("perl_client", context, perlClient);
    }
}

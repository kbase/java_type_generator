package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
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
        String jsClient = "JsVal";
        String perlClient = "ClientVal";
        generate(fr, defUrl, jsClient, perlClient, null, outDir);
        File testDir = new File("other", specName);
        cmpFiles(outDir, testDir, jsClient + ".js");
        cmpFiles(outDir, testDir, perlClient + ".pm");
    }
    
    private static void cmpFiles(File dir1, File dir2, String fileName) throws Exception {
        String text1 = TextUtils.readFileText(new File(dir1, fileName));
        String text2 = TextUtils.readFileText(new File(dir2, fileName));
        if (isDiff(text1, text2)) {
            System.out.println("Files [" + fileName + "] are different");
            PrintWriter pw = new PrintWriter(new File(dir1, fileName + ".diff"));
            printDiff(text1, text2, pw);
            pw.close();
        }
    }
    
    public static boolean isDiff(String origText, String newText) throws Exception {
        List<String> origLn = TextUtils.getLines(origText);
        List<String> newLn = TextUtils.getLines(newText);
        if (origLn.size() != newLn.size()) {
            return true;
        }
        for (int pos = 0; pos < origLn.size(); pos++) {
            String l1 = origLn.get(pos);
            String l2 = newLn.get(pos);
            if (!l1.equals(l2)) {
                return true;
            }
        }
        return false;
    }

    private static void printDiff(String origText, String newText, PrintWriter pw) throws Exception {
        List<String> origLn = TextUtils.getLines(origText);
        List<String> newLn = TextUtils.getLines(newText);
        int origWidth = 0;
        for (String l : origLn)
            if (origWidth < l.length())
                origWidth = l.length();
        if (origWidth > 100)
            origWidth = 100;
        int maxSize = Math.max(origLn.size(), newLn.size());
        for (int pos = 0; pos < maxSize; pos++) {
            String origL = pos < origLn.size() ? origLn.get(pos) : "<no-data>";
            String newL = pos < newLn.size() ? newLn.get(pos) : "<no-data>";
            boolean eq = origL.equals(newL);
            if (origL.length() > origWidth) {
                pw.println("/" + (eq ? " " : "*") +origL);
                pw.println("\\" + (eq ? " " : "*") + newL);
            } else {
                String sep = eq ? "   " : " * ";
                char[] gap = new char[origWidth - origL.length()];
                Arrays.fill(gap, ' ');
                pw.println(origL + new String(gap) + sep + newL);
            }
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
        context.put("enable_client_retry", true);
        context.put("empty_escaper", "");
        if (!outDir.exists())
            outDir.mkdirs();
        File jsClient = new File(outDir, jsName + ".js");
        TemplateFormatter.formatTemplate("javascript_client", context, jsClient);
        File perlClient = new File(outDir, perlClientName + ".pm");
        TemplateFormatter.formatTemplate("perl_client", context, perlClient);
    }
}

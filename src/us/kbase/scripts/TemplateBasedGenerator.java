package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.templates.TemplateFormatter;

public class TemplateBasedGenerator {
    
    public static void main(String[] args) throws Exception {
        test("erdb");
        test("fba");
        test("is");
        test("ws");
    }
    
    private static void test(String specName) throws Exception {
        File spec = new File("./other/"+specName+".spec");
        FileReader fr = new FileReader(spec);
        String defUrl = "https://kbase.us/services/ws";
        File outDir = new File("temp", specName);
        String jsClient = "JsVal";
        String perlClient = "ClientVal";
        String perlServer = "ServiceVal";
        String perlImpl = "ImplVal";
        String perlPsgi = "PsgiVal";
        String pythonClient = "PyVal";
        String pythonServer = "PyserverVal";
        String pythonImpl = "PyimplVal";
        generate(fr, defUrl, jsClient, perlClient, perlServer, perlImpl, perlPsgi, 
                pythonClient, pythonServer, pythonImpl, null, outDir);
        File testDir = new File("other", specName);
        cmpFiles(outDir, testDir, jsClient + ".js", false);
        cmpFiles(outDir, testDir, perlClient + ".pm" , true);
        cmpFiles(outDir, testDir, pythonClient + ".py" , false);
        cmpFiles(outDir, testDir, perlServer + ".pm", false);
        cmpFiles(outDir, testDir, pythonServer + ".py", false);
        cmpFiles(outDir, testDir, perlImpl + ".pm", true);
        cmpFiles(outDir, testDir, pythonImpl + ".py", false);
        cmpFiles(outDir, testDir, perlPsgi, false);
    }
    
    public static void generate(Reader specReader, String defaultUrl, String jsName,
            String perlClientName, String perlServerName, String perlImplName, String perlPsgiName, 
            String pythonClientName, String pythonServerName, String pythonImplName, 
            IncludeProvider ip, File outDir) throws Exception {
        if (ip == null)
            ip = new FileIncludeProvider(new File("."));
        List<KbService> srvs = KidlParser.parseSpec(KidlParser.parseSpecInt(specReader, null, ip));
        KbService service = srvs.get(0);
        Map<String, Object> context = service.forTemplates(perlImplName, pythonImplName);
        if (defaultUrl != null)
            context.put("default_service_url", defaultUrl);
        context.put("client_package_name", perlClientName);
        context.put("server_package_name", perlServerName);
        context.put("impl_package_name", perlImplName);
        context.put("enable_client_retry", true);
        context.put("empty_escaper", "");  // ${empty_escaper}
        context.put("display", new StringUtils());
        if (!outDir.exists())
            outDir.mkdirs();
        if (jsName != null) {
            File jsClient = new File(outDir, jsName + ".js");
            TemplateFormatter.formatTemplate("javascript_client", context, jsClient);
        }
        if (perlClientName != null) {
            File perlClient = new File(outDir, perlClientName + ".pm");
            TemplateFormatter.formatTemplate("perl_client", context, perlClient);
        }
        if (pythonClientName != null) {
            File pythonClient = new File(outDir, pythonClientName + ".py");
            TemplateFormatter.formatTemplate("python_client", context, pythonClient);
        }
        if (perlServerName != null) {
            File perlServer = new File(outDir, perlServerName + ".pm");
            TemplateFormatter.formatTemplate("perl_server", context, perlServer);
        }
        if (pythonServerName != null) {
            File pythonServer = new File(outDir, pythonServerName + ".py");
            TemplateFormatter.formatTemplate("python_server", context, pythonServer);
        }
        if (perlImplName != null || pythonImplName != null) {
            List<Map<String, Object>> modules = (List<Map<String, Object>>)context.get("modules");
            for (int modulePos = 0; modulePos < modules.size(); modulePos++) {
                Map<String, Object> module = new LinkedHashMap<String, Object>(modules.get(modulePos));
                List<Map<String, Object>> methods = (List<Map<String, Object>>)module.get("methods");
                List<String> methodNames = new ArrayList<String>();
                for (Map<String, Object> method : methods)
                    methodNames.add(method.get("name").toString());
                File perlImpl = null;
                if (perlImplName != null) {
                    String perlModuleImplName = (String)module.get("impl_package_name");
                    perlImpl = new File(outDir, perlModuleImplName + ".pm");
                    Map<String, String> prevCode = PrevCodeParser.parsePrevCode(perlImpl, "#", methodNames, false);
                    module.put("module_header", prevCode.get(PrevCodeParser.HEADER));
                    module.put("module_constructor", prevCode.get(PrevCodeParser.CONSTRUCTOR));
                    for (Map<String, Object> method : methods) {
                        String code = prevCode.get(PrevCodeParser.METHOD + method.get("name"));
                        method.put("user_code", code == null ? "" : code);
                    }
                }
                File pythonImpl = null;
                if (pythonImplName != null) {
                    String pythonModuleImplName = (String)module.get("pymodule");
                    pythonImpl = new File(outDir, pythonModuleImplName + ".py");
                    Map<String, String> prevCode = PrevCodeParser.parsePrevCode(pythonImpl, "#", methodNames, true);
                    module.put("py_module_header", prevCode.get(PrevCodeParser.HEADER));
                    module.put("py_module_class_header", prevCode.get(PrevCodeParser.CLSHEADER));
                    module.put("py_module_constructor", prevCode.get(PrevCodeParser.CONSTRUCTOR));
                    for (Map<String, Object> method : methods) {
                        String code = prevCode.get(PrevCodeParser.METHOD + method.get("name"));
                        method.put("py_user_code", code == null ? "" : code);
                    }
                }
                Map<String, Object> moduleContext = new LinkedHashMap<String, Object>();
                moduleContext.put("module", module);
                moduleContext.put("server_package_name", perlServerName);
                moduleContext.put("empty_escaper", "");  // ${empty_escaper}
                moduleContext.put("display", new StringUtils());
                if (perlImplName != null)
                    TemplateFormatter.formatTemplate("perl_impl", moduleContext, perlImpl);
                if (pythonImplName != null)
                    TemplateFormatter.formatTemplate("python_impl", moduleContext, pythonImpl);
            }
        }
        if (perlPsgiName != null) {
            File perlPsgi = new File(outDir, perlPsgiName);
            TemplateFormatter.formatTemplate("perl_psgi", context, perlPsgi);
        }
    }

    private static void cmpFiles(File dir1, File dir2, String fileName, boolean trim) throws Exception {
        String text1 = TextUtils.readFileText(new File(dir1, fileName));
        String text2 = TextUtils.readFileText(new File(dir2, fileName));
        if (isDiff(text1, text2, trim)) {
            System.out.println("Files [" + dir1.getName() + "/" + fileName + "] are different");
            PrintWriter pw = new PrintWriter(new File(dir1, fileName + ".diff"));
            printDiff(text1, text2, trim, pw);
            pw.close();
        }
    }
    
    public static boolean isDiff(String origText, String newText, boolean trim) throws Exception {
        List<String> origLn = TextUtils.getLines(origText);
        List<String> newLn = TextUtils.getLines(newText);
        if (origLn.size() != newLn.size()) {
            return true;
        }
        for (int pos = 0; pos < origLn.size(); pos++) {
            String l1 = origLn.get(pos);
            String l2 = newLn.get(pos);
            boolean eq = trim ? l1.trim().equals(l2.trim()) : l1.equals(l2);
            if (!eq) {
                return true;
            }
        }
        return false;
    }

    private static void printDiff(String origText, String newText, boolean trim, PrintWriter pw) throws Exception {
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
            boolean eq = trim ? origL.trim().equals(newL.trim()) : origL.equals(newL);
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
}

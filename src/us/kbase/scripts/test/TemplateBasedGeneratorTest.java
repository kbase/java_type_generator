package us.kbase.scripts.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import us.kbase.kidl.test.KidlTest;
import us.kbase.scripts.TemplateBasedGenerator;
import us.kbase.scripts.TextUtils;
import us.kbase.scripts.util.ProcessHelper;

public class TemplateBasedGeneratorTest {

    @Test
    public void mainTest() throws Exception {
        boolean ok = true;
        for (int testNum : KidlTest.getTestSpecNumbers())
            if (testNum < 21)
                ok &= test(testNum);
        Assert.assertTrue(ok);
    }
    
    private static boolean test(int testNum) throws Exception {
        File workDir = prepareWorkDir(testNum);
        File specFile = new File(workDir, "spec." + testNum + ".spec");
        prepareSpec(specFile, KidlTest.readTestSpec(testNum));
        FileReader fr = new FileReader(specFile);
        String defUrl = "https://kbase.us/services/ws";
        String jsClient = "JsClient";
        String perlClient = "PerlClient";
        String perlServer = "PerlServer";
        String perlImpl = "PerlImpl";
        String perlPsgi = "PerlPsgi";
        String pythonClient = "PythonClient";
        String pythonServer = "PythonServer";
        String pythonImpl = "PythonImpl";
        long time1 = System.currentTimeMillis();
        File oldDir = prepareOldTypecompCode(specFile, defUrl, jsClient, perlClient, perlServer, 
                perlImpl, perlPsgi, pythonClient, pythonServer, pythonImpl);
        time1 = System.currentTimeMillis() - time1;
        File outDir = new File(workDir, "new");
        long time2 = System.currentTimeMillis();
        TemplateBasedGenerator.generate(fr, defUrl, jsClient, perlClient, perlServer, 
                perlImpl, perlPsgi, pythonClient, pythonServer, pythonImpl, false, null, outDir);
        time2 = System.currentTimeMillis() - time2;
        //System.out.println("Test [" + testNum + "], old-time: " + time1 + ", new-time: " + time2);
        boolean ok = true;
        ok &= cmpFiles(testNum, outDir, oldDir, jsClient + ".js", false);
        ok &= cmpFiles(testNum, outDir, oldDir, perlClient + ".pm" , true);
        ok &= cmpFiles(testNum, outDir, oldDir, pythonClient + ".py" , false);
        ok &= cmpFiles(testNum, outDir, oldDir, perlServer + ".pm", false);
        ok &= cmpFiles(testNum, outDir, oldDir, pythonServer + ".py", false);
        ok &= cmpFiles(testNum, outDir, oldDir, perlImpl + ".pm", true);
        ok &= cmpFiles(testNum, outDir, oldDir, pythonImpl + ".py", false);
        ok &= cmpFiles(testNum, outDir, oldDir, perlPsgi, false);
        return ok;
    }

    private static void prepareSpec(File specFile, InputStream is) throws IOException {
        PrintWriter pw = new PrintWriter(specFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            pw.println(l);
        }
        br.close();
        pw.close();
    }

    private static File prepareOldTypecompCode(File testSpec, String defUrl, String jsClient, 
            String perlClient, String perlServer, String perlImpl, String perlPsgi, 
            String pythonClient, String pythonServer, String pythonImpl) throws IOException {
        File workDir = testSpec.getParentFile();
        File bashFile = new File(workDir, "parse_old.sh");
        File serverOutDir = new File(workDir, "old");
        serverOutDir.mkdir();
        TextUtils.writeFileLines(Arrays.asList(
                "#!/bin/bash",
                getKbBinDir() + "compile_typespec --path " + workDir.getAbsolutePath() + " --url " +
                defUrl + " --scripts " + serverOutDir.getName() + " --psgi " + perlPsgi + " --impl " + 
                perlImpl + " --service " + perlServer + " --client " + perlClient + " --js " + 
                jsClient + " --py " + pythonClient + " --pyserver " + pythonServer + " --pyimpl " + 
                pythonImpl + " " + testSpec.getName() + " " + serverOutDir.getName() + " >t.out 2>t.err"
                ), bashFile);
        ProcessHelper.cmd("bash", bashFile.getCanonicalPath()).exec(workDir);
        return serverOutDir;
    }

    private static String getKbBinDir() {
        String kbTop = System.getenv("KB_TOP");
        if (kbTop != null && kbTop.trim().length() > 0)
            return kbTop + "/bin/";
        return "";
    }

    private static File prepareWorkDir(int testNum) throws IOException {
        File tempDir = new File(".").getCanonicalFile();
        if (!tempDir.getName().equals("test")) {
            tempDir = new File(tempDir, "test");
            if (!tempDir.exists())
                tempDir.mkdir();
        }
        for (File dir : tempDir.listFiles()) {
            if (dir.isDirectory() && dir.getName().startsWith("spec" + testNum + "_"))
                try {
                    TextUtils.deleteRecursively(dir);
                } catch (Exception e) {
                    System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
                }
        }
        File workDir = new File(tempDir, "spec" + testNum + "_" + System.currentTimeMillis());
        if (!workDir.exists())
            workDir.mkdir();
        return workDir;
    }

    private static boolean cmpFiles(int testNum, File dir1, File dir2, String fileName, boolean trim) throws Exception {
        String text1 = TextUtils.readFileText(new File(dir1, fileName));
        String text2 = TextUtils.readFileText(new File(dir2, fileName));
        if (isDiff(text1, text2, trim)) {
            System.out.println("Test [" + testNum + "], files [" + fileName + "] are different");
            PrintWriter pw = new PrintWriter(new File(dir1, fileName + ".diff"));
            printDiff(text1, text2, trim, pw);
            pw.close();
            return false;
        }
        return true;
    }
    
    public static boolean isDiff(String origText, String newText, boolean trim) throws Exception {
        List<String> origLn = TextUtils.getLines(origText);
        List<String> newLn = TextUtils.getLines(newText);
        if (origLn.size() != newLn.size()) {
            return true;
        }
        for (int pos = 0; pos < origLn.size(); pos++) {
            String l1 = removeTrailingStars(origLn.get(pos));
            String l2 = removeTrailingStars(newLn.get(pos));
            boolean eq = trim ? l1.trim().equals(l2.trim()) : l1.equals(l2);
            if (!eq) {
                return true;
            }
        }
        return false;
    }
    
    private static String removeTrailingStars(String line) {
        if (line.startsWith("**")) {
            int pos = 0;
            while (pos < line.length() && line.charAt(pos) == '*')
                pos++;
            line = line.substring(pos);
        }
        return line;
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
            String origL = pos < origLn.size() ? removeTrailingStars(origLn.get(pos)) : "<no-data>";
            String newL = pos < newLn.size() ? removeTrailingStars(newLn.get(pos)) : "<no-data>";
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

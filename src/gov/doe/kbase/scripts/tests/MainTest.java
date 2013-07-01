package gov.doe.kbase.scripts.tests;

import gov.doe.kbase.scripts.JavaClientGenerator;
import gov.doe.kbase.scripts.JavaData;
import gov.doe.kbase.scripts.JavaFunc;
import gov.doe.kbase.scripts.JavaModule;
import gov.doe.kbase.scripts.KbFuncdef;
import gov.doe.kbase.scripts.Utils;
import gov.doe.kbase.scripts.util.ProcessHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Test;

public class MainTest extends Assert {
	private static final String parsingScript = "compile_typespec.pl";
	
	@Test
	public void test1() throws Exception {
		startTest(1);
	}
	
	private static void startTest(int testNum) throws Exception {
		File tempDir = new File(".").getCanonicalFile();
		for (File dir : tempDir.listFiles()) {
			if (dir.isDirectory() && dir.getName().startsWith("test" + testNum))
				try {
					deleteDirRecursively(dir);
				} catch (Exception e) {
					System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
				}
		}
		File workDir = new File(tempDir, "test" + testNum + "_" + System.currentTimeMillis());
		System.out.println("Test is staring in directory: " + workDir.getName());
		if (!workDir.exists())
			workDir.mkdir();
		String testFileName = "test" + testNum + ".spec";
		try {
			Utils.writeFileLines(Utils.readStreamLines(MainTest.class.getResourceAsStream(testFileName + ".properties")), new File(workDir, testFileName));
		} catch (Exception ex) {
			String zipFileName = "test" + testNum + ".zip";
			try {
				ZipInputStream zis = new ZipInputStream(MainTest.class.getResourceAsStream(zipFileName + ".properties"));
				while (true) {
					ZipEntry ze = zis.getNextEntry();
					System.out.println("MainTest: zip_entry=" + ze);
					if (ze == null)
						break;
					Utils.writeFileLines(Utils.readStreamLines(zis, false), new File(workDir, ze.getName()));
				}
				zis.close();
			} catch (Exception e2) {
				throw new IllegalStateException("Can not find neither " + testFileName + " resource nor " + zipFileName + " in resources having .properties suffix", ex);
			}
		}
		Utils.writeFileLines(Utils.readStreamLines(MainTest.class.getResourceAsStream(parsingScript + ".properties")), new File(workDir, parsingScript));
		File bashFile = new File(workDir, "parse.sh");
		File serverOutDir = new File(workDir, "out");
		serverOutDir.mkdir();
		Utils.writeFileLines(Arrays.asList(
				"#!/bin/bash",
				"export KB_TOP=/kb/deployment:$KB_TOP",
				"export KB_RUNTIME=/kb/runtime:$KB_RUNTIME",
				"export PATH=/kb/runtime/bin:/kb/deployment/bin:$PATH",
				"export PERL5LIB=/kb/deployment/lib:$PERL5LIB",
				"cd \"" + workDir.getAbsolutePath() + "\"",
				"perl " + parsingScript + " --scripts " + serverOutDir.getName() + " --psgi service.psgi " + testFileName + " " + serverOutDir.getName() + " >comp.out 2>comp.err"
				), bashFile);
		ProcessHelper.cmd("bash", bashFile.getCanonicalPath()).exec(workDir);
		File srcDir = new File(workDir, "src");
		File jsonSchemaDir = new File(workDir, "tempJsonSchemas");
		String rootPackageName = MainTest.class.getPackage().getName();
		String testPackage = rootPackageName + ".test" + testNum;
		JavaData parsingData = JavaClientGenerator.processJson(new File(workDir, "parsing_tree_for_java.json"), jsonSchemaDir, srcDir, testPackage);
		File libDir = new File(workDir, "lib");
		libDir.mkdir();
		StringBuilder classPath = new StringBuilder();
		List<URL> cpUrls = new ArrayList<URL>();
		addLib("jackson-all-1.9.11.jar", libDir, classPath, cpUrls);
		addLib("junit-4.9.jar", libDir, classPath, cpUrls);
		File binDir = new File(workDir, "bin");
		binDir.mkdir();
        for (JavaModule module : parsingData.getModules()) {
        	ProcessHelper.cmd("javac", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", classPath.toString(), "src/" + testPackage.replace('.', '/') + "/" + module.getModuleName() + "/Client.java").exec(workDir);
        }
        File testJavaFile = new File(workDir, "src/" + testPackage.replace('.', '/') + "/Test" + testNum + ".java");
        copyStreams(MainTest.class.getResourceAsStream("Test" + testNum + ".java.properties"), new FileOutputStream(testJavaFile));
    	ProcessHelper.cmd("javac", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", classPath.toString(), "src/" + testPackage.replace('.', '/') + "/Test" + testNum + ".java").exec(workDir);
        cpUrls.add(binDir.toURI().toURL());
        URLClassLoader urlcl = URLClassLoader.newInstance(cpUrls.toArray(new URL[cpUrls.size()]));
        for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File serverImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.pm");
            List<String> serverLines = Utils.readFileLines(serverImpl);
            for (int pos = 0; pos < serverLines.size(); pos++) {
            	String line = serverLines.get(pos);
            	if (line.startsWith("    #BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (origNameToFunc.containsKey(origFuncName)) {
            			KbFuncdef origFunc = origNameToFunc.get(origFuncName).getOriginal();
            			int paramCount = origFunc.getParameters().size();
            			for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            				pos++;
            				serverLines.add(pos, "    $return" + (paramCount > 1 ? ("_" + (paramPos + 1)) : "") + " = $" + origFunc.getParameters().get(paramPos).getName() + ";");
            			}
            		}
            	}
            }
            Utils.writeFileLines(serverLines, serverImpl);
        }
        File plackupFile = new File(serverOutDir, "start_server.sh");
        File pidFile = new File(serverOutDir, "pid.txt");
		Utils.writeFileLines(Arrays.asList(
				"#!/bin/bash",
				"export KB_TOP=/kb/deployment:$KB_TOP",
				"export KB_RUNTIME=/kb/runtime:$KB_RUNTIME",
				"export PATH=/kb/runtime/bin:/kb/deployment/bin:$PATH",
				"export PERL5LIB=/kb/deployment/lib:$PERL5LIB",
				"echo $PERL5LIB",
				"cd \"" + serverOutDir.getAbsolutePath() + "\"",
				"plackup --listen :9999 service.psgi >server.out 2>server.err & pid=$!",
				"echo $pid > " + pidFile.getName()
				), plackupFile);
		int portNum = 9999;
		try {
			ProcessHelper.cmd("bash", plackupFile.getCanonicalPath()).exec(serverOutDir);
			Thread.sleep(1000);
			for (JavaModule module : parsingData.getModules()) {
				Class<?> clientClass = urlcl.loadClass(testPackage + "." + module.getModuleName() + ".Client");
				Object client = clientClass.getConstructor(String.class).newInstance("http://localhost:" + portNum);
				Class<?> testClass = urlcl.loadClass(testPackage + ".Test" + testNum);
				testClass.getConstructor(clientClass).newInstance(client);
			}
		} finally {
			if (pidFile.exists()) {
				String pid = Utils.readFileLines(pidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println("Plackup process was finally killed: " + pid);
			}
		}
	}

	private static void addLib(String libName, File libDir, StringBuilder classPath, List<URL> libUrls) throws Exception {
		File libFile = new File(libDir, libName);
		InputStream is = MainTest.class.getResourceAsStream(libName + ".properties");
		OutputStream os = new FileOutputStream(libFile);
		copyStreams(is, os);
        if (classPath.length() > 0)
        	classPath.append(':');
        classPath.append("lib/").append(libName);
        libUrls.add(libFile.toURI().toURL());
	}

	private static void copyStreams(InputStream is, OutputStream os)
			throws IOException {
		byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
	}
	
	private static void deleteDirRecursively(File dir) {
		if (dir.isDirectory())
			for (File f : dir.listFiles()) 
				deleteDirRecursively(f);
		dir.delete();
	}
}

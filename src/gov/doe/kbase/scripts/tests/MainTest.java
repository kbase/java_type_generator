package gov.doe.kbase.scripts.tests;

import gov.doe.kbase.scripts.JavaTypeGenerator;
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
import java.lang.reflect.InvocationTargetException;
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

	@Test
	public void testSimpleTypesAndStructures() throws Exception {
		startTest(1);
	}

	@Test
	public void testIncludsAndMultiModules() throws Exception {
		startTest(2);
	}

	@Test
	public void testTuples() throws Exception {
		startTest(3);
	}

	@Test
	public void testBoolAndObject() throws Exception {
		startTest(4);
	}
	
	private static void startTest(int testNum) throws Exception {
		File tempDir = new File(".").getCanonicalFile();
		for (File dir : tempDir.listFiles()) {
			if (dir.isDirectory() && dir.getName().startsWith("test" + testNum))
				try {
					Utils.deleteRecursively(dir);
				} catch (Exception e) {
					System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
				}
		}
		File workDir = new File(tempDir, "test" + testNum + "_" + System.currentTimeMillis());
		System.out.println();
		System.out.println("Test " + testNum + " is staring in directory: " + workDir.getName());
		if (!workDir.exists())
			workDir.mkdir();
		String testFileName = "test" + testNum + ".spec";
		extractSpecFiles(testNum, workDir, testFileName);
		File serverOutDir = new File(workDir, "out");
		serverOutDir.mkdir();
		File srcDir = new File(workDir, "src");
		String rootPackageName = "gov.doe.kbase";
		String testPackage = rootPackageName + ".test" + testNum;
		File libDir = new File(workDir, "lib");
		JavaData parsingData = JavaTypeGenerator.processSpec(new File(workDir, testFileName), workDir, srcDir, testPackage, true, libDir);
		File bashFile = new File(workDir, "parse.sh");
		Utils.writeFileLines(Arrays.asList(
				"#!/bin/bash",
				"export KB_TOP=/kb/deployment:$KB_TOP",
				"export KB_RUNTIME=/kb/runtime:$KB_RUNTIME",
				"export PATH=/kb/runtime/bin:/kb/deployment/bin:$PATH",
				"export PERL5LIB=/kb/deployment/lib:$PERL5LIB",
				"cd \"" + workDir.getAbsolutePath() + "\"",
				"perl /kb/deployment/plbin/compile_typespec.pl --path " + workDir.getAbsolutePath() +
				" --scripts " + serverOutDir.getName() + " --psgi service.psgi " + 
				testFileName + " " + serverOutDir.getName() + " >comp.out 2>comp.err"
				), bashFile);
		ProcessHelper.cmd("bash", bashFile.getCanonicalPath()).exec(workDir);
		//showCompErrors(workDir);
		javaServerCorrection(srcDir, testPackage, parsingData);
		StringBuilder classPath = new StringBuilder();
		List<URL> cpUrls = new ArrayList<URL>();
		addLib("jackson-all-1.9.11", libDir, classPath, cpUrls);
		addLib("servlet-api-2.5", libDir, classPath, cpUrls);
		addLib("jetty-all-7.0.0", libDir, classPath, cpUrls);
		addLib("junit-4.9", libDir, classPath, cpUrls);
		File binDir = new File(workDir, "bin");
		binDir.mkdir();
        for (JavaModule module : parsingData.getModules()) {
        	String clientFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModuleName() + "/" + 
					getClientClassName(module) + ".java";
        	String serverFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModuleName() + "/" + 
					getServerClassName(module) + ".java";
        	runJavac(workDir, srcDir, classPath, binDir, clientFilePath, serverFilePath);
        }
        String testJavaFileName = "Test" + testNum + ".java";
    	String testFilePath = "src/" + testPackage.replace('.', '/') + "/" + testJavaFileName;
        File testJavaFile = new File(workDir, testFilePath);
        String testJavaResource = testJavaFileName + ".properties";
        InputStream testClassIS = MainTest.class.getResourceAsStream(testJavaResource);
        if (testClassIS == null) {
        	Assert.fail("Java test class resource was not found: " + testJavaResource);
        }
        Utils.copyStreams(testClassIS, new FileOutputStream(testJavaFile));
    	runJavac(workDir, srcDir, classPath, binDir, testFilePath);
        cpUrls.add(binDir.toURI().toURL());
        URLClassLoader urlcl = URLClassLoader.newInstance(cpUrls.toArray(new URL[cpUrls.size()]));
        perlServerCorrection(serverOutDir, parsingData);
        File perlPidFile = new File(serverOutDir, "pid.txt");
		int portNum = 9990 + testNum;
		try {
	        File plackupFile = new File(serverOutDir, "start_perl_server.sh");
			Utils.writeFileLines(Arrays.asList(
					"#!/bin/bash",
					"export KB_TOP=/kb/deployment:$KB_TOP",
					"export KB_RUNTIME=/kb/runtime:$KB_RUNTIME",
					"export PATH=/kb/runtime/bin:/kb/deployment/bin:$PATH",
					"export PERL5LIB=/kb/deployment/lib:$PERL5LIB",
					"cd \"" + serverOutDir.getAbsolutePath() + "\"",
					"plackup --listen :" + portNum + " service.psgi >perl_server.out 2>perl_server.err & pid=$!",
					"echo $pid > " + perlPidFile.getAbsolutePath()
					), plackupFile);
			ProcessHelper.cmd("bash", plackupFile.getCanonicalPath()).exec(serverOutDir);
			Thread.sleep(1000);
			runClientTest(testNum, testPackage, parsingData, urlcl, portNum);
		} finally {
			if (perlPidFile.exists()) {
				String pid = Utils.readFileLines(perlPidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println("Plackup process was finally killed: " + pid);
			}
		}
        File javaPidFile = new File(workDir, "pid.txt");
		try {
	        File jettyFile = new File(workDir, "start_java_server.sh");
	        JavaModule mainModule = parsingData.getModules().get(0);
			Utils.writeFileLines(Arrays.asList(
					"#!/bin/bash",
					"cd \"" + workDir.getAbsolutePath() + "\"",
					"java -cp ./bin:" + classPath + " " + testPackage + "." + mainModule.getModuleName() + "." + 
					getServerClassName(mainModule) + " " + portNum + " >java_server.out 2>java_server.err & pid=$!",
					"echo $pid > " + javaPidFile.getAbsolutePath()
					), jettyFile);
			ProcessHelper.cmd("bash", jettyFile.getCanonicalPath()).exec(serverOutDir);
			Thread.sleep(1000);
			runClientTest(testNum, testPackage, parsingData, urlcl, portNum);
		} finally {
			if (javaPidFile.exists()) {
				String pid = Utils.readFileLines(javaPidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println("Jetty process was finally killed: " + pid);
			}
			System.out.println();
		}
	}

	private static void showCompErrors(File workDir) throws IOException {
		List<String> errLines = Utils.readFileLines(new File(workDir, "comp.err"));
		if (errLines.size() > 1 || (errLines.size() == 1 && errLines.get(0).trim().length() > 0)) {
			for (String errLine : errLines)
				System.err.println(errLine);
		}
		Assert.fail("Spec-files compilation problem");
	}

	private static void runClientTest(int testNum, String testPackage,
			JavaData parsingData, URLClassLoader urlcl, int portNum)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		for (JavaModule module : parsingData.getModules()) {
			String clientClassName = getClientClassName(module);
			Class<?> clientClass = urlcl.loadClass(testPackage + "." + module.getModuleName() + "." + clientClassName);
			Object client = clientClass.getConstructor(String.class).newInstance("http://localhost:" + portNum);
			Class<?> testClass = urlcl.loadClass(testPackage + ".Test" + testNum);
			testClass.getConstructor(clientClass).newInstance(client);
		}
	}

	private static void extractSpecFiles(int testNum, File workDir,
			String testFileName) {
		try {
			Utils.writeFileLines(Utils.readStreamLines(MainTest.class.getResourceAsStream(testFileName + ".properties")), 
					new File(workDir, testFileName));
		} catch (Exception ex) {
			String zipFileName = "test" + testNum + ".zip";
			try {
				ZipInputStream zis = new ZipInputStream(MainTest.class.getResourceAsStream(zipFileName + ".properties"));
				while (true) {
					ZipEntry ze = zis.getNextEntry();
					if (ze == null)
						break;
					Utils.writeFileLines(Utils.readStreamLines(zis, false), new File(workDir, ze.getName()));
				}
				zis.close();
			} catch (Exception e2) {
				throw new IllegalStateException("Can not find neither " + testFileName + " resource nor " + zipFileName + 
						" in resources having .properties suffix", ex);
			}
		}
	}

	private static void perlServerCorrection(File serverOutDir,
			JavaData parsingData) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File perlServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.pm");
            List<String> perlServerLines = Utils.readFileLines(perlServerImpl);
            for (int pos = 0; pos < perlServerLines.size(); pos++) {
            	String line = perlServerLines.get(pos);
            	if (line.startsWith("    #BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (origNameToFunc.containsKey(origFuncName)) {
            			KbFuncdef origFunc = origNameToFunc.get(origFuncName).getOriginal();
            			int paramCount = origFunc.getParameters().size();
            			for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            				pos++;
            				perlServerLines.add(pos, "    $return" + (paramCount > 1 ? ("_" + (paramPos + 1)) : "") + " = $" + 
            						origFunc.getParameters().get(paramPos).getName() + ";");
            			}
            		}
            	}
            }
            Utils.writeFileLines(perlServerLines, perlServerImpl);
        }
	}

	private static void javaServerCorrection(File srcDir, String packageParent, JavaData parsingData) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File moduleDir = new File(srcDir.getAbsolutePath() + "/" + packageParent.replace('.', '/') + "/" + module.getModuleName());
            File perlServerImpl = new File(moduleDir, getServerClassName(module) + ".java");
            List<String> perlServerLines = Utils.readFileLines(perlServerImpl);
            for (int pos = 0; pos < perlServerLines.size(); pos++) {
            	String line = perlServerLines.get(pos);
            	if (line.startsWith("        //BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (origNameToFunc.containsKey(origFuncName)) {
            			JavaFunc func = origNameToFunc.get(origFuncName);
            			int paramCount = func.getParams().size();
            			for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            				pos++;
            				perlServerLines.add(pos, "        ret" + (paramCount > 1 ? ("" + (paramPos + 1)) : "") + " = " + 
            						func.getParams().get(paramPos).getJavaName() + ";");
            			}
            		}
            	}
            }
            Utils.writeFileLines(perlServerLines, perlServerImpl);
        }
	}

	private static void runJavac(File workDir, File srcDir, StringBuilder classPath, File binDir, 
			String... sourceFilePaths) throws IOException {
		ProcessHelper.cmd("javac", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", 
				classPath.toString(), "-source", "1.6").add(sourceFilePaths).exec(workDir);
	}

	private static String getClientClassName(JavaModule module) {
		return Utils.capitalize(module.getModuleName()) + "Client";
	}

	private static String getServerClassName(JavaModule module) {
		return Utils.capitalize(module.getModuleName()) + "Server";
	}

	private static void addLib(String libName, File libDir, StringBuilder classPath, List<URL> libUrls) throws Exception {
		String libFileName = libName + ".jar";
		File libFile = new File(libDir, libFileName);
		if (!libFile.exists()) {
			InputStream is = MainTest.class.getResourceAsStream(libFileName + ".properties");
			OutputStream os = new FileOutputStream(libFile);
			Utils.copyStreams(is, os);
		}
        if (classPath.length() > 0)
        	classPath.append(':');
        classPath.append("lib/").append(libFileName);
        libUrls.add(libFile.toURI().toURL());
	}
}

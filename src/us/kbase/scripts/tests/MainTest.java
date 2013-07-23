package us.kbase.scripts.tests;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;

import us.kbase.scripts.JavaData;
import us.kbase.scripts.JavaFunc;
import us.kbase.scripts.JavaModule;
import us.kbase.scripts.JavaTypeGenerator;
import us.kbase.scripts.KbFuncdef;
import us.kbase.scripts.Utils;
import us.kbase.scripts.util.ProcessHelper;

public class MainTest extends Assert {
	public static final String rootPackageName = "us.kbase";
	public static final String gwtPackageName = "us.kbase.gwt";
	
	public static void main(String[] args) throws Exception{
		int testNum = Integer.parseInt(args[0]);
		if (testNum == 8) {
			new MainTest().testServerCodeStoring();
		} else if (testNum == 9) {
			startTest(testNum, false);
		} else {
			startTest(testNum);
		}
	}
	
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
	public void testObject() throws Exception {
		startTest(4);
	}

	@Test
	public void testBool() throws Exception {
		startTest(5);
	}

	@Test
	public void testAuth() throws Exception {
		startTest(6);
	}

	@Test
	public void testEmptyArgsAndReturns() throws Exception {
		startTest(7);
	}

	@Test
	public void testServerCodeStoring() throws Exception {
		int testNum = 8;
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " is staring in directory: " + workDir.getName());
		String testFileName = "test" + testNum + ".spec";
		extractSpecFiles(testNum, workDir, testFileName);
		File srcDir = new File(workDir, "src");
		String testPackage = rootPackageName + ".test" + testNum;
    	String serverFilePath = "src/" + testPackage.replace('.', '/') + "/storing/StoringServer.java";
        File serverJavaFile = new File(workDir, serverFilePath);
        serverJavaFile.getParentFile().mkdirs();
        serverJavaFile.createNewFile();
		File libDir = new File(workDir, "lib");
        // Test for empty server file
		try {
			JavaTypeGenerator.processSpec(new File(workDir, testFileName), workDir, srcDir, testPackage, true, libDir, gwtPackageName);
		} catch (Exception ex) {
			Assert.assertTrue(ex.getMessage().contains("Missing header in original file"));
		}
        String testJavaResource = "Test" + testNum + ".java.properties";
        InputStream testClassIS = MainTest.class.getResourceAsStream(testJavaResource);
        if (testClassIS == null) {
        	Assert.fail("Java test class resource was not found: " + testJavaResource);
        }
        Utils.copyStreams(testClassIS, new FileOutputStream(serverJavaFile));
        // Test for full server file
		JavaData parsingData = JavaTypeGenerator.processSpec(new File(workDir, testFileName), workDir, srcDir, testPackage, true, libDir, gwtPackageName);
		List<URL> cpUrls = new ArrayList<URL>();
		String classPath = prepareClassPath(libDir, cpUrls);
		File binDir = new File(workDir, "bin");
        cpUrls.add(binDir.toURI().toURL());
		compileModulesIntoBin(workDir, srcDir, testPackage, parsingData, classPath, binDir);
		String text = Utils.readFileText(serverJavaFile);
		Assert.assertTrue(text.contains("* Header comment."));
		Assert.assertTrue(text.contains("private int myValue = -1;"));
		Assert.assertTrue(text.contains("myValue = 0;"));
		Assert.assertTrue(text.contains("myValue = 1;"));
		Assert.assertTrue(text.contains("myValue = 2;"));
	}

	@Test
	public void testGwtTransform() throws Exception {
		startTest(9, false);
	}

	private static void startTest(int testNum) throws Exception {
		startTest(testNum, true);
	}
	
	private static void startTest(int testNum, boolean needClientServer) throws Exception {
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " is staring in directory: " + workDir.getName());
		String testFileName = "test" + testNum + ".spec";
		extractSpecFiles(testNum, workDir, testFileName);
		File srcDir = new File(workDir, "src");
		String testPackage = rootPackageName + ".test" + testNum;
		File libDir = new File(workDir, "lib");
		JavaData parsingData = JavaTypeGenerator.processSpec(new File(workDir, testFileName), workDir, srcDir, testPackage, true, libDir, gwtPackageName);
		javaServerCorrection(srcDir, testPackage, parsingData);
		parsingData = JavaTypeGenerator.processSpec(new File(workDir, testFileName), workDir, srcDir, testPackage, true, libDir, gwtPackageName);
		List<URL> cpUrls = new ArrayList<URL>();
		String classPath = prepareClassPath(libDir, cpUrls);
		File binDir = new File(workDir, "bin");
        cpUrls.add(binDir.toURI().toURL());
		compileModulesIntoBin(workDir, srcDir, testPackage, parsingData, classPath, binDir);
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
		if (needClientServer) {
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
					"perl /kb/deployment/plbin/compile_typespec.pl --path " + workDir.getAbsolutePath() +
					" --scripts " + serverOutDir.getName() + " --psgi service.psgi " + 
					testFileName + " " + serverOutDir.getName() + " >comp.out 2>comp.err"
					), bashFile);
			ProcessHelper.cmd("bash", bashFile.getCanonicalPath()).exec(workDir);
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
				runClientTest(testNum, testPackage, parsingData, libDir, binDir, portNum, needClientServer);
			} finally {
				if (perlPidFile.exists()) {
					String pid = Utils.readFileLines(perlPidFile).get(0).trim();
					ProcessHelper.cmd("kill", pid).exec(workDir);
					System.out.println("Plackup process was finally killed: " + pid);
				}
			}
			Server javaServer = null;
			try {
				JavaModule mainModule = parsingData.getModules().get(0);
				long time = System.currentTimeMillis();
				javaServer = startupJavaServer(mainModule, libDir, binDir, testPackage, portNum);
				System.out.println("Java server startup time: " + (System.currentTimeMillis() - time) + " ms.");
				runClientTest(testNum, testPackage, parsingData, libDir, binDir, portNum, needClientServer);
			} finally {
				if (javaServer != null) {
					javaServer.stop();
					System.out.println("Jetty process was finally stopped");
				}
				System.out.println();
			}
		} else {
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, -1, needClientServer);
		}
	}

	private static void compileModulesIntoBin(File workDir, File srcDir, String testPackage, 
			JavaData parsingData, String classPath, File binDir) throws IOException, MalformedURLException {
		if (!binDir.exists())
			binDir.mkdir();
        for (JavaModule module : parsingData.getModules()) {
        	String clientFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModuleName() + "/" + 
					getClientClassName(module) + ".java";
        	String serverFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModuleName() + "/" + 
					getServerClassName(module) + ".java";
        	runJavac(workDir, srcDir, classPath, binDir, clientFilePath, serverFilePath);
        }
	}

	private static String prepareClassPath(File libDir, List<URL> cpUrls)
			throws Exception {
		StringBuilder classPathSB = new StringBuilder();
		addLib("jackson-all-1.9.11", libDir, classPathSB, cpUrls);
		addLib("servlet-api-2.5", libDir, classPathSB, cpUrls);
		addLib("jetty-all-7.0.0", libDir, classPathSB, cpUrls);
		addLib("junit-4.9", libDir, classPathSB, cpUrls);
		addLib("kbase-auth", libDir, classPathSB, cpUrls);
		addLib("bcpkix-jdk15on-147", libDir, classPathSB, cpUrls);
		addLib("bcprov-ext-jdk15on-147", libDir, classPathSB, cpUrls);
		addLib("ini4j-0.5.2", libDir, classPathSB, cpUrls);
		return classPathSB.toString();
	}

	private static Server startupJavaServer(JavaModule module, File libDir, File binDir, 
			String testPackage, int port) throws Exception {
		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        URLClassLoader urlcl = prepareUrlClassLoader(libDir, binDir);
        String serverClassName = testPackage + "." + module.getModuleName() + "." + getServerClassName(module);
        Class<?> serverClass = urlcl.loadClass(serverClassName);
        context.addServlet(new ServletHolder(serverClass), "/*");
        server.start();
        return server;
	}

	private static URLClassLoader prepareUrlClassLoader(File libDir, File binDir)
			throws Exception, MalformedURLException {
		List<URL> cpUrls = new ArrayList<URL>();
        prepareClassPath(libDir, cpUrls);
        cpUrls.add(binDir.toURI().toURL());
        URLClassLoader urlcl = URLClassLoader.newInstance(cpUrls.toArray(new URL[cpUrls.size()]));
		return urlcl;
	}
	
	private static File prepareWorkDir(int testNum) throws IOException {
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
		if (!workDir.exists())
			workDir.mkdir();
		return workDir;
	}

	private static void runClientTest(int testNum, String testPackage, JavaData parsingData, 
			File libDir, File binDir, int portNum, boolean needClientServer) throws Exception {
        URLClassLoader urlcl = prepareUrlClassLoader(libDir, binDir);
		ConnectException error = null;
		for (int n = 0; n < 50; n++) {
			Thread.sleep(100);
			try {
				for (JavaModule module : parsingData.getModules()) {
					Class<?> testClass = urlcl.loadClass(testPackage + ".Test" + testNum);
					if (needClientServer) {
						String clientClassName = getClientClassName(module);
						Class<?> clientClass = urlcl.loadClass(testPackage + "." + module.getModuleName() + "." + clientClassName);
						Object client = clientClass.getConstructor(String.class).newInstance("http://localhost:" + portNum);
						testClass.getConstructor(clientClass).newInstance(client);
					} else {
						testClass.getConstructor().newInstance();
					}
				}
				error = null;
				System.out.println("Timeout before server response: " + (n * 100) + " ms.");
				break;
			} catch (InvocationTargetException ex) {
				Throwable t = ex.getCause();
				if (t != null && t instanceof Exception) {
					if (t instanceof ConnectException) {
						error = (ConnectException)t;
					} else {
						throw (Exception)t;
					}
				} else {
					throw ex;
				}
			}
		}
		if (error != null)
			throw error;
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

	private static void runJavac(File workDir, File srcDir, String classPath, File binDir, 
			String... sourceFilePaths) throws IOException {
		ProcessHelper.cmd("javac", "-g:source,lines", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", 
				classPath).add(sourceFilePaths).exec(workDir);
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

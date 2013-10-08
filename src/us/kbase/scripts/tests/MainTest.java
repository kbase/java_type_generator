package us.kbase.scripts.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import us.kbase.kidl.KbFuncdef;
import us.kbase.scripts.JavaData;
import us.kbase.scripts.JavaFunc;
import us.kbase.scripts.JavaModule;
import us.kbase.scripts.JavaTypeGenerator;
import us.kbase.scripts.TextUtils;
import us.kbase.scripts.util.ProcessHelper;

/**
 * Main test class for JavaTypeGenerator.
 * @author rsutormin
 */
public class MainTest extends Assert {
	public static final String rootPackageName = "us.kbase";
	
	public static void main(String[] args) throws Exception{
		int testNum = Integer.parseInt(args[0]);
		if (testNum == 5) {
			new MainTest().testSyslog();
		} else if (testNum == 6) {
			new MainTest().testAuth();
		} else if (testNum == 8) {
			new MainTest().testServerCodeStoring();
		} else if (testNum == 9 || testNum == 10) {
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
	public void testSyslog() throws Exception {
		int testNum = 5;
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (testSyslog) is starting in directory: " + workDir.getName());
		String testPackage = rootPackageName + ".test" + testNum;
		File srcDir = new File(workDir, "src");
		File libDir = new File(workDir, "lib");
		File binDir = new File(workDir, "bin");
		JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
		javaServerCorrectionForTestCallback(srcDir, testPackage, parsingData, testPackage + ".Test" + testNum);
		String classPath = prepareClassPath(libDir, new ArrayList<URL>());
    	runJavac(workDir, srcDir, classPath, binDir, "src/us/kbase/test5/syslogtest/SyslogTestServer.java");
		runJavaServerTest(testNum, true, testPackage, libDir, binDir, parsingData);
	}
	
	@Test
	public void testAuth() throws Exception {
		int testNum = 6;
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (testAuth) is starting in directory: " + workDir.getName());
		String testPackage = rootPackageName + ".test" + testNum;
		File libDir = new File(workDir, "lib");
		File binDir = new File(workDir, "bin");
		JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, perlPort(testNum), true);
		File serverOutDir = preparePerlAndPyServerCode(testNum, workDir);
		runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, serverOutDir);
		parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, pyPort(testNum), true);
		serverOutDir = preparePerlAndPyServerCode(testNum, workDir);
		runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, serverOutDir);
		parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, javaPort(testNum), true);
		runJavaServerTest(testNum, true, testPackage, libDir, binDir, parsingData);
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
		String gwtPackageName = getGwtPackageName(testNum);
        // Test for empty server file
		try {
			JavaTypeGenerator.processSpec(new File(workDir, testFileName),
					workDir, srcDir, testPackage, true, libDir, gwtPackageName, null);
		} catch (Exception ex) {
			boolean key = ex.getMessage().contains("Missing header in original file");
			if (!key)
				ex.printStackTrace();
			Assert.assertTrue(key);
		}
        String testJavaResource = "Test" + testNum + ".java.properties";
        InputStream testClassIS = MainTest.class.getResourceAsStream(testJavaResource);
        if (testClassIS == null) {
        	Assert.fail("Java test class resource was not found: " + testJavaResource);
        }
        TextUtils.copyStreams(testClassIS, new FileOutputStream(serverJavaFile));
        // Test for full server file
		JavaData parsingData = JavaTypeGenerator.processSpec(
				new File(workDir, testFileName), workDir, srcDir, testPackage,
				true, libDir, gwtPackageName, null);
		List<URL> cpUrls = new ArrayList<URL>();
		String classPath = prepareClassPath(libDir, cpUrls);
		File binDir = new File(workDir, "bin");
        cpUrls.add(binDir.toURI().toURL());
		compileModulesIntoBin(workDir, srcDir, testPackage, parsingData, classPath, binDir);
		for (JavaModule module : parsingData.getModules())
        	createServerServletInstance(module, libDir, binDir, testPackage);
		String text = TextUtils.readFileText(serverJavaFile);
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

	@Test
	public void testComments() throws Exception {
		startTest(10, false);
	}
	
	private static void startTest(int testNum) throws Exception {
		startTest(testNum, true);
	}

	private static String getCallingMethod() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		String methodName = st[3].getMethodName();
		if (methodName.equals("startTest")) {
			methodName = st[4].getMethodName();
		}
		return methodName;
	}
	
	private static void startTest(int testNum, boolean needClientServer) throws Exception {
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (" + getCallingMethod() + ") is starting in directory: " + workDir.getName());
		String testPackage = rootPackageName + ".test" + testNum;
		File libDir = new File(workDir, "lib");
		File binDir = new File(workDir, "bin");
		JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, needClientServer);
		if (needClientServer) {
			File serverOutDir = preparePerlAndPyServerCode(testNum, workDir);
			runPerlServerTest(testNum, needClientServer, workDir, testPackage,
					libDir, binDir, parsingData, serverOutDir);
			runJavaServerTest(testNum, needClientServer, testPackage, libDir,
					binDir, parsingData);
			runPythonServerTest(testNum, needClientServer, workDir,
					testPackage, libDir, binDir, parsingData, serverOutDir);
		} else {
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, -1, needClientServer);
		}
	}

	protected static void runPythonServerTest(int testNum,
			boolean needClientServer, File workDir, String testPackage,
			File libDir, File binDir, JavaData parsingData, File serverOutDir)
			throws IOException, Exception {
		int portNum = pyPort(testNum);
		File pidFile = new File(serverOutDir, "pid.txt");
		pythonServerCorrection(serverOutDir, parsingData);
		try {
			File serverFile = findPythonServerScript(serverOutDir);
			File uwsgiFile = new File(serverOutDir, "start_py_server.sh");
			List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
			//JavaTypeGenerator.checkEnvVars(lines, "PYTHONPATH");
			lines.addAll(Arrays.asList(
					"python " + serverFile.getAbsolutePath() + " --host localhost --port " + portNum + " >py_server.out 2>py_server.err & pid=$!",
					"echo $pid > " + pidFile.getAbsolutePath()
					));
			TextUtils.writeFileLines(lines, uwsgiFile);
			ProcessHelper.cmd("bash", uwsgiFile.getCanonicalPath()).exec(serverOutDir);
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, portNum, needClientServer);
		} finally {
			if (pidFile.exists()) {
				String pid = TextUtils.readFileLines(pidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println("Python server process was finally killed: " + pid);
			}
		}
	}

	protected static int pyPort(int testNum) {
		int portNum = 10200 + testNum;
		return portNum;
	}

	protected static void runJavaServerTest(int testNum,
			boolean needClientServer, String testPackage, File libDir,
			File binDir, JavaData parsingData) throws Exception {
		int portNum = javaPort(testNum);
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
				System.out.println("Java server thread was finally stopped");
			}
			System.out.println();
		}
	}

	protected static int javaPort(int testNum) {
		int portNum = 10100 + testNum;
		return portNum;
	}

	protected static void runPerlServerTest(int testNum,
			boolean needClientServer, File workDir, String testPackage,
			File libDir, File binDir, JavaData parsingData, File serverOutDir)
			throws IOException, Exception {
		perlServerCorrection(serverOutDir, parsingData);
		File pidFile = new File(serverOutDir, "pid.txt");
		int portNum = perlPort(testNum);
		try {
			File plackupFile = new File(serverOutDir, "start_perl_server.sh");
			List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
			//JavaTypeGenerator.checkEnvVars(lines, "PERL5LIB");
			lines.addAll(Arrays.asList(
					"plackup --listen :" + portNum + " service.psgi >perl_server.out 2>perl_server.err & pid=$!",
					"echo $pid > " + pidFile.getAbsolutePath()
					));
			TextUtils.writeFileLines(lines, plackupFile);
			ProcessHelper.cmd("bash", plackupFile.getCanonicalPath()).exec(serverOutDir);
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, portNum, needClientServer);
		} finally {
			if (pidFile.exists()) {
				String pid = TextUtils.readFileLines(pidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println("Perl server process was finally killed: " + pid);
			}
		}
	}

	protected static int perlPort(int testNum) {
		int portNum = 10000 + testNum;
		return portNum;
	}

	protected static File preparePerlAndPyServerCode(int testNum, File workDir)
			throws IOException {
		String testFileName = "test" + testNum + ".spec";
		File bashFile = new File(workDir, "parse.sh");
		File serverOutDir = new File(workDir, "out");
		serverOutDir.mkdir();
		TextUtils.writeFileLines(Arrays.asList(
				"#!/bin/bash",
				getKbBinDir() + "compile_typespec --path " + workDir.getAbsolutePath() +
				" --scripts " + serverOutDir.getName() + " --psgi service.psgi " + 
				testFileName + " " + serverOutDir.getName() + " >comp.out 2>comp.err"
				), bashFile);
		ProcessHelper.cmd("bash", bashFile.getCanonicalPath()).exec(workDir);
		return serverOutDir;
	}

	protected static JavaData prepareJavaCode(int testNum, File workDir,
			String testPackage, File libDir, File binDir, Integer defaultUrlPort,
			boolean needJavaServerCorrection) throws Exception,
			IOException, MalformedURLException, FileNotFoundException {
		JavaData parsingData = null;
		String testFileName = "test" + testNum + ".spec";
		extractSpecFiles(testNum, workDir, testFileName);
		File srcDir = new File(workDir, "src");
		String gwtPackageName = getGwtPackageName(testNum);
		URL defaultUrl = defaultUrlPort == null ? null :
			new URL("http://localhost:" + defaultUrlPort);
		parsingData = JavaTypeGenerator.processSpec(
				new File(workDir, testFileName), workDir, srcDir, testPackage,
				true, libDir, gwtPackageName, defaultUrl);
		if (needJavaServerCorrection)
			javaServerCorrection(srcDir, testPackage, parsingData);
		parsingData = JavaTypeGenerator.processSpec(
				new File(workDir, testFileName), workDir, srcDir, testPackage,
				true, libDir, gwtPackageName, defaultUrl);
		List<URL> cpUrls = new ArrayList<URL>();
		String classPath = prepareClassPath(libDir, cpUrls);
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
        TextUtils.copyStreams(testClassIS, new FileOutputStream(testJavaFile));
    	runJavac(workDir, srcDir, classPath, binDir, testFilePath);
    	File docDir = new File(workDir, "doc");
    	docDir.mkdir();
    	List<String> docPackages = new ArrayList<String>(Arrays.asList(testPackage));
    	for (JavaModule module : parsingData.getModules())
    		docPackages.add(testPackage + "." + module.getModulePackage());
    	runJavaDoc(workDir, srcDir, classPath, docDir, docPackages.toArray(new String[docPackages.size()]));
		return parsingData;
	}

	private static String getGwtPackageName(int testNum) {
		return rootPackageName + ".gwt";
	}
	
	private static String getKbBinDir() {
		String kbTop = System.getenv("KB_TOP");
		if (kbTop != null && kbTop.trim().length() > 0)
			return kbTop + "/bin/";
		return "";
	}
	
	private static File findPythonServerScript(File dir) {
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith("Server.py"))
				return f;
		}
		throw new IllegalStateException("Can not find python server script");
	}
	
	private static void compileModulesIntoBin(File workDir, File srcDir, String testPackage, 
			JavaData parsingData, String classPath, File binDir) throws IOException, MalformedURLException {
		if (!binDir.exists())
			binDir.mkdir();
        for (JavaModule module : parsingData.getModules()) {
        	String clientFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModulePackage() + "/" + 
					getClientClassName(module) + ".java";
        	String serverFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModulePackage() + "/" + 
					getServerClassName(module) + ".java";
        	runJavac(workDir, srcDir, classPath, binDir, clientFilePath, serverFilePath);
        }
	}

	private static String prepareClassPath(File libDir, List<URL> cpUrls)
			throws Exception {
		JavaTypeGenerator.checkLib(libDir, "junit-4.9");
		StringBuilder classPathSB = new StringBuilder();
		for (File jarFile : libDir.listFiles()) {
			if (!jarFile.getName().endsWith(".jar"))
				continue;
			addLib(jarFile, libDir, classPathSB, cpUrls);
		}
		return classPathSB.toString();
	}

	private static Server startupJavaServer(JavaModule module, File libDir, File binDir, 
			String testPackage, int port) throws Exception {
		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        Class<?> serverClass = createServerServletInstance(module, libDir, binDir, testPackage);
        context.addServlet(new ServletHolder(serverClass), "/*");
        server.start();
        return server;
	}

	private static Class<?> createServerServletInstance(JavaModule module,
			File libDir, File binDir, String testPackage) throws Exception,
			MalformedURLException, ClassNotFoundException {
		URLClassLoader urlcl = prepareUrlClassLoader(libDir, binDir);
        String serverClassName = testPackage + "." + module.getModulePackage() + "." + getServerClassName(module);
        Class<?> serverClass = urlcl.loadClass(serverClassName);
		return serverClass;
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
		if (!tempDir.getName().equals("test")) {
			tempDir = new File(tempDir, "test");
			if (!tempDir.exists())
				tempDir.mkdir();
		}
		for (File dir : tempDir.listFiles()) {
			if (dir.isDirectory() && dir.getName().startsWith("test" + testNum + "_"))
				try {
					TextUtils.deleteRecursively(dir);
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
		System.out.println("Port: " + portNum);
        URLClassLoader urlcl = prepareUrlClassLoader(libDir, binDir);
		ConnectException error = null;
		for (int n = 0; n < 50; n++) {
			Thread.sleep(100);
			try {
				for (JavaModule module : parsingData.getModules()) {
					Class<?> testClass = urlcl.loadClass(testPackage + ".Test" + testNum);
					if (needClientServer) {
						String clientClassName = getClientClassName(module);
						Class<?> clientClass = urlcl.loadClass(testPackage + "." + module.getModulePackage() + "." + clientClassName);
						Object client = clientClass.getConstructor(URL.class).newInstance(new URL("http://localhost:" + portNum));
						try {
							testClass.getConstructor(clientClass).newInstance(client);
						} catch (NoSuchMethodException e) {
							testClass.getConstructor(clientClass, Integer.class).newInstance(client, portNum);							
						}
					} else {
						try {
							testClass.getConstructor().newInstance();
						} catch (NoSuchMethodException e) {
							testClass.getConstructor(File.class).newInstance(binDir.getParentFile());
						}
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
			TextUtils.writeFileLines(TextUtils.readStreamLines(MainTest.class.getResourceAsStream(testFileName + ".properties")), 
					new File(workDir, testFileName));
		} catch (Exception ex) {
			String zipFileName = "test" + testNum + ".zip";
			try {
				ZipInputStream zis = new ZipInputStream(MainTest.class.getResourceAsStream(zipFileName + ".properties"));
				while (true) {
					ZipEntry ze = zis.getNextEntry();
					if (ze == null)
						break;
					TextUtils.writeFileLines(TextUtils.readStreamLines(zis, false), new File(workDir, ze.getName()));
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
            List<String> perlServerLines = TextUtils.readFileLines(perlServerImpl);
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
            TextUtils.writeFileLines(perlServerLines, perlServerImpl);
        }
	}

	private static void javaServerCorrectionForTestCallback(File srcDir, String packageParent, JavaData parsingData, String testClassName) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            File moduleDir = new File(srcDir.getAbsolutePath() + "/" + packageParent.replace('.', '/') + "/" + module.getModulePackage());
            File serverImpl = new File(moduleDir, getServerClassName(module) + ".java");
            List<String> serverLines = TextUtils.readFileLines(serverImpl);
            for (int pos = 0; pos < serverLines.size(); pos++) {
            	String line = serverLines.get(pos);
            	if (line.startsWith("        //BEGIN ") || line.startsWith("        //BEGIN_CONSTRUCTOR")) {
            		pos++;
            		serverLines.add(pos, "        " + testClassName + ".serverMethod(this);");
            	}
            }
            TextUtils.writeFileLines(serverLines, serverImpl);
        }
	}

	private static void javaServerCorrection(File srcDir, String packageParent, JavaData parsingData) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File moduleDir = new File(srcDir.getAbsolutePath() + "/" + packageParent.replace('.', '/') + "/" + module.getModulePackage());
            File perlServerImpl = new File(moduleDir, getServerClassName(module) + ".java");
            List<String> perlServerLines = TextUtils.readFileLines(perlServerImpl);
            for (int pos = 0; pos < perlServerLines.size(); pos++) {
            	String line = perlServerLines.get(pos);
            	if (line.startsWith("        //BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (origNameToFunc.containsKey(origFuncName)) {
            			JavaFunc func = origNameToFunc.get(origFuncName);
            			int paramCount = func.getParams().size();
            			for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            				pos++;
            				perlServerLines.add(pos, "        return" + (paramCount > 1 ? ("" + (paramPos + 1)) : "Val") + " = " + 
            						func.getParams().get(paramPos).getJavaName() + ";");
            			}
            		}
            	}
            }
            TextUtils.writeFileLines(perlServerLines, perlServerImpl);
        }
	}

	private static void pythonServerCorrection(File serverOutDir, JavaData parsingData) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File pyServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.py");
            List<String> pyServerLines = TextUtils.readFileLines(pyServerImpl);
            for (int pos = 0; pos < pyServerLines.size(); pos++) {
            	String line = pyServerLines.get(pos);
            	if (line.startsWith("        #BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (origNameToFunc.containsKey(origFuncName)) {
            			KbFuncdef origFunc = origNameToFunc.get(origFuncName).getOriginal();
            			int paramCount = origFunc.getParameters().size();
            			for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            				pos++;
            				pyServerLines.add(pos, "        return" + (paramCount > 1 ? ("_" + (paramPos + 1)) : "Val") + " = " + 
            						origFunc.getParameters().get(paramPos).getName());
            			}
            			if (paramCount == 0) {
            				pos++;
            				pyServerLines.add(pos, "        pass");
            			}
            		}
            	}
            }
            TextUtils.writeFileLines(pyServerLines, pyServerImpl);
        }
	}

	private static void runJavac(File workDir, File srcDir, String classPath, File binDir, 
			String... sourceFilePaths) throws IOException {
		ProcessHelper.cmd("javac", "-g:source,lines", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", 
				classPath).add(sourceFilePaths).exec(workDir);
	}

	private static void runJavaDoc(File workDir, File srcDir, String classPath, File docDir, String... packages) throws IOException {
		ProcessHelper.cmd("javadoc", "-d", docDir.getName(), "-sourcepath", srcDir.getName(), "-classpath", 
				classPath).add(packages).exec(workDir, (File)null, null);
	}

	private static String getClientClassName(JavaModule module) {
		return TextUtils.capitalize(module.getModuleName()) + "Client";
	}

	private static String getServerClassName(JavaModule module) {
		return TextUtils.capitalize(module.getModuleName()) + "Server";
	}

	private static void addLib(File libFile, File libDir, StringBuilder classPath, List<URL> libUrls) throws Exception {
        if (classPath.length() > 0)
        	classPath.append(':');
        classPath.append("lib/").append(libFile.getName());
        libUrls.add(libFile.toURI().toURL());
	}
}

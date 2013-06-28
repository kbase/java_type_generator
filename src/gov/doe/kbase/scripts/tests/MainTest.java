package gov.doe.kbase.scripts.tests;

import gov.doe.kbase.scripts.JavaClientGenerator;
import gov.doe.kbase.scripts.JavaData;
import gov.doe.kbase.scripts.JavaModule;
import gov.doe.kbase.scripts.Utils;
import gov.doe.kbase.scripts.util.ProcessHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Test;

public class MainTest extends Assert {
	private static final String testPackageName = "testpackage";
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
					System.out.println("Directory was deleted: " + dir.getName());
				} catch (Exception e) {
					System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
				}
		}
		File workDir = new File(tempDir, "test" + testNum + "_" + System.currentTimeMillis());
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
				"perl " + parsingScript + " --scripts " + serverOutDir.getName() + " --psgi service.psgi " + testFileName + " " + serverOutDir.getName()
				), bashFile);
		ProcessHelper.cmd("bash", bashFile.getCanonicalPath()).exec(workDir);
		File srcDir = new File(workDir, "src");
		File jsonSchemaDir = new File(workDir, "tempJsonSchemas");
		JavaData parsingData = JavaClientGenerator.processJson(new File(workDir, "parsing_tree_for_java.json"), jsonSchemaDir, srcDir, testPackageName);
		File libDir = new File(workDir, "lib");
		libDir.mkdir();
		StringBuilder classPath = new StringBuilder();
		addLib("jackson-all-1.9.11.jar", libDir, classPath);
		File binDir = new File(workDir, "bin");
		binDir.mkdir();
        for (JavaModule module : parsingData.getModules()) {
        	ProcessHelper.cmd("javac", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", classPath.toString(), "src/" + testPackageName + "/" + module.getModuleName() + "/Client.java").exec(workDir);
        }
	}

	private static void addLib(String libName, File libDir, StringBuilder classPath) throws Exception {
		File libFile = new File(libDir, libName);
		InputStream is = MainTest.class.getResourceAsStream(libName + ".properties");
		OutputStream os = new FileOutputStream(libFile);
		byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
        if (classPath.length() > 0)
        	classPath.append(':');
        classPath.append("lib/").append(libName);
	}
	
	private static void deleteDirRecursively(File dir) {
		if (dir.isDirectory())
			for (File f : dir.listFiles()) 
				deleteDirRecursively(f);
		dir.delete();
	}
}

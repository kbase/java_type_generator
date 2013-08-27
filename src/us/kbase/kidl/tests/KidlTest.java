package us.kbase.kidl.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import us.kbase.kidl.KidlParser;

public class KidlTest {

	private static File prepareWorkDir() throws IOException {
		File tempDir = new File(".").getCanonicalFile();
		if (!tempDir.getName().equals("test")) {
			tempDir = new File(tempDir, "test");
			if (!tempDir.exists())
				tempDir.mkdir();
		}
		File workDir = new File(tempDir, "test_kidl");
		if (!workDir.exists())
			workDir.mkdir();
		return workDir;
	}

	@Test
	public void testJsonSchemas() throws Exception {
		File workDir = prepareWorkDir();
		File specFile = new File(workDir, "Test.spec");
		PrintWriter pw = new PrintWriter(specFile);
		pw.println("module Test {\ntypedef structure {\nint val1;\nfloat val2;\n} my_struct;\n};");
		pw.close();
		Map<String, Map<String, String>> schemas = new HashMap<String, Map<String, String>>();
		KidlParser.parseSpec(specFile, workDir, schemas);
		String schema = schemas.get("Test").get("my_struct");
		Assert.assertNotNull(schema);
		System.out.println(schema);
	}
}

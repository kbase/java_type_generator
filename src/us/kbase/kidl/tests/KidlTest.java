package us.kbase.kidl.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbModuleComp;
import us.kbase.kidl.KbScalar;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KbStruct;
import us.kbase.kidl.KbStructItem;
import us.kbase.kidl.KbTypedef;
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
		File specFile = prepareSpec(workDir,
				"module Test {\ntypedef structure {\nint val1;\nfloat val2;\n} my_struct;\n};");
		Map<String, Map<String, String>> schemas = new HashMap<String, Map<String, String>>();
		KidlParser.parseSpec(specFile, workDir, schemas);
		String schema = schemas.get("Test").get("my_struct");
		Assert.assertNotNull(schema);
		System.out.println(schema);
	}

	private static File prepareSpec(File workDir, String text) throws FileNotFoundException {
		File specFile = new File(workDir, "Test.spec");
		PrintWriter pw = new PrintWriter(specFile);
		pw.println(text);
		pw.close();
		return specFile;
	}
	
	@Test
	public void testOptionalAnnotation() throws Exception {
		File workDir = prepareWorkDir();
		File specFile = prepareSpec(workDir, "" +
				"module Test {\n" +
				"  /*\n" +
				"   @optional val2\n" +
				"  */\n" +
				"  typedef structure {\n" +
				"    int val1;\n" +
				"    float val2;\n" +
				"  } my_struct;\n" +
				"};");
		List<KbService> srvList = KidlParser.parseSpec(specFile, workDir, null);
		KbModule module = getModule(srvList);
		List<KbModuleComp> cmpList = module.getModuleComponents();
		Assert.assertEquals(1, cmpList.size());
		Assert.assertEquals(KbTypedef.class, cmpList.get(0).getClass());
		KbTypedef typedef = (KbTypedef)cmpList.get(0);
		Assert.assertEquals(KbStruct.class, typedef.getAliasType().getClass());
		KbStruct type = (KbStruct)typedef.getAliasType();
		Assert.assertEquals(2, type.getItems().size());
		for (KbStructItem item : type.getItems())
			Assert.assertEquals(item.getName().equals("val2"), item.isOptional());
	}
	
	@Test
	public void testReferenceId() throws Exception {
		File workDir = prepareWorkDir();
		File specFile = prepareSpec(workDir, "" +
				"module Test {\n" +
				"  /*\n" +
				"   @id_reference Test.my_struct\n" +
				"  */\n" +
				"  typedef string full_ref;\n" +
				"  \n" +
				"  /*\n" +
				"   @id_reference\n" +
				"  */\n" +
				"  typedef string just_ref;\n" +
				"  \n" +
				"  typedef structure {\n" +
				"    int val1;\n" +
				"    float val2;\n" +
				"  } my_struct;\n" +
				"};");
		List<KbService> srvList = KidlParser.parseSpec(specFile, workDir, null);
		KbModule module = getModule(srvList);
		List<KbModuleComp> cmpList = module.getModuleComponents();
		Assert.assertEquals(3, cmpList.size());
		for (int i = 0; i < cmpList.size(); i++) {
			Assert.assertEquals(KbTypedef.class, cmpList.get(i).getClass());
			KbTypedef typedef = (KbTypedef)cmpList.get(i);
			if (typedef.getName().endsWith("_ref")) {
				System.out.println(typedef.getName() + ": " + typedef.getData());
				Assert.assertEquals(KbScalar.class, typedef.getAliasType().getClass());
				KbScalar type = (KbScalar)typedef.getAliasType();
				System.out.println("IdReferences: " + type.getIdReferences());
			}
		}
	}

	protected KbModule getModule(List<KbService> srvList) {
		Assert.assertEquals(1, srvList.size());
		List<KbModule> modList = srvList.get(0).getModules();
		Assert.assertEquals(1, modList.size());
		return modList.get(0);
	}
}

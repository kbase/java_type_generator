package us.kbase.kidl.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbModuleComp;
import us.kbase.kidl.KbScalar;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KbStruct;
import us.kbase.kidl.KbStructItem;
import us.kbase.kidl.KbTypedef;
import us.kbase.kidl.KidlParseException;
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

	private static List<String> getLines(String text) throws Exception {
		BufferedReader br = new BufferedReader(new StringReader(text));
		List<String> ret = new ArrayList<String>();
		while (true) {
			String l = br.readLine();
			if (l == null)
				break;
			ret.add(l);
		}
		br.close();
		return ret;
	}
	
	public static void showDiff(String origText, String newText) throws Exception {
		List<String> origLn = getLines(origText);
		List<String> newLn = getLines(newText);
		int origWidth = 0;
		for (String l : origLn)
			if (origWidth < l.length())
				origWidth = l.length();
		if (origWidth > 100)
			origWidth = 100;
		int maxSize = Math.max(origLn.size(), newLn.size());
		for (int pos = 0; pos < maxSize; pos++) {
			String origL = pos < origLn.size() ? origLn.get(pos) : "";
			String newL = pos < newLn.size() ? newLn.get(pos) : "";
			boolean eq = origL.equals(newL);
			if (origL.length() > origWidth) {
				System.out.println("/" + (eq ? " " : "*") +origL);
				System.out.println("\\" + (eq ? " " : "*") + newL);
			} else {
				String sep = eq ? "   " : " * ";
				char[] gap = new char[origWidth - origL.length()];
				Arrays.fill(gap, ' ');
				System.out.println(origL + new String(gap) + sep + newL);
			}
		}
	}
	
	@Test
	public void testJsonSchemas() throws Exception {
		String[] tests = {
				"module Test1 {\n" +
				"  typedef list<int> test1;\n" +
				"};",				
				"module Test2 {\n" +
				"  typedef list<string> test1;\n" +
				"};",				
				"module Test3 {\n" +
				"  typedef mapping<string, int> test1;\n" +
				"};",
				"module Test4 {\n" +
				"  typedef list<tuple<string, int>> test1;\n" +
				"};",				
				"module Test5 {\n" +
				"  typedef structure {string val1; int val2; } test1;\n" +
				"};",				
				"/*\n" +
				"  test\n" +
				"*/\n" +
				"\n" +
				"module Test6 {\n" +
				"  typedef string test1;\n" +
				"  typedef list<string> test2;\n" +
				"  typedef mapping<string,int> test3;\n" +
				"  typedef tuple<int,string,float> test4;\n" +
				"  typedef list<mapping<string,int>> test5;\n" +
				"  typedef list<tuple<int,string,float>> test6;\n" +
				"  typedef mapping<string,list<int>> test7;\n" +
				"  typedef mapping<string,tuple<int,string,float>> test8;\n" +
				"  typedef tuple<list<float>,mapping<string,int>> test9;\n" +
				"  /*\n" +
				"  @optional var3\n" +
				"  */\n" +
				"  typedef structure {\n" +
				"    int val2;\n" +
				"    float val1;\n" +
				"    list<string> var3;\n" +
				"    mapping<string,int> var4;\n" +
				"    tuple<int,string,float> var5;\n" +
				"    UnspecifiedObject val6;\n" +
				"  } testA;\n" +
				"  typedef list<testA> testB;\n" +
				"  typedef mapping<string,testA> testC;\n" +
				"  typedef tuple<testA,testA,testA> testD;\n" +
				"  typedef structure {\n" +
				"    testA val1;\n" +
				"    test1 id;\n" +
				"  } testE;\n" +
				"};",
				"module Test7 {\n" +
				"  /*		  line1  \n" +
				"  line2 \n" +
				"    line3  	*/\n" +
				"  typedef string test1;\n" +
				"};",								
				"module Test8 {\n" +
				"  /* line1  \n" +
				"	   line2 \n" +
				"  */\n" +
				"  typedef string test1;\n" +
				"};",								
				"module Test9 {\n" +
				"  /*	\n" +
				"	\n" +
				"   line1	r\n" +
				"    line2\n" +
				"         line3\n" +
				" line4\n" +
				"   line5\n" +
				"  */\n" +
				"  typedef string test1;\n" +
				"};",								
				"module Test10 {\n" +
				"  /*\n" +
				"    line1  \n" +
				"		\n" +
				"     line2  \n" +
				"	\n" +
				"  */\n" +
				"  typedef string test1;\n" +
				"};",								
				"module Test11 {\n" +
				"  /*\n" +
				"  @id ws Test11.test2\n" +
				"  */\n" +
				"  typedef string test1;\n" +
				"  typedef structure {\n" +
				"    int val1;\n" +
				"    float val2;\n" +
				"    string val3;\n" +
				"  } test2;\n" +
				"  /*\n" +
				"  @id kb\n" +
				"  */\n" +
				"  typedef string test3;\n" +
				"  /*\n" +
				"  @id external src1 src2\n" +
				"  */\n" +
				"  typedef string test4;\n" +
				"  /*\n" +
				"  @id shock\n" +
				"  */\n" +
				"  typedef string test5;\n" +
				"};",
				"module Test12 {\n" +
				"  typedef structure {\n" +
				"    string val1;\n" +
				"    mapping<string,string> val2;\n" +
				"  } test2;\n" +
				"  /*\n" +
				"  @searchable ws_subset val1 val2 val2.[*] keys_of(val3,val6.[*],val6.[*].*.val2) val4.val1 val4.val2.* keys_of(val4.val2) val5.[*].(val1,val2.*) val6.[*].*.val1\n" +
				"  */\n" +
				"  typedef structure {\n" +
				"    string val1;\n" +
				"    list<string> val2;\n" +
				"    mapping<string,list<string>> val3;\n" +
				"    test2 val4;\n" +
				"    list<test2> val5;\n" +
				"    list<mapping<string,test2>> val6;\n" +
				"  } test1;\n" +
				"};",
		};
		boolean ok = true;
		for (int testNum = 0; testNum < tests.length; testNum++) {
			File workDir = prepareWorkDir();
			File specFile = prepareSpec(workDir, tests[testNum]);
			Map<String, Map<String, String>> schemas1 = new HashMap<String, Map<String, String>>();
			Map<?,?> parse1 = KidlParser.parseSpecExt(specFile, workDir, schemas1, null);
			Map<String, Map<String, String>> schemas2 = new HashMap<String, Map<String, String>>();
			Map<?,?> parse2 = KidlParser.parseSpecInt(specFile, workDir, schemas2);
			ok = ok & compareJson(parse1, parse2, "Parsing result for test #" + (testNum + 1));
			ok = ok & compareJsonSchemas(schemas1, schemas2, "Json schema for test #" + (testNum + 1));
		}
		Assert.assertTrue(ok);
	}

	@Test
	public void testJsonSchemas2() throws Exception {
		boolean ok = true;
		for (int testNum = 1; testNum <= 20; testNum++) {
			if (testNum == 9) {
				continue;
			}
			File workDir = prepareWorkDir();
			InputStream is = this.getClass().getResourceAsStream("spec." + testNum + ".properties");
			File specFile = prepareSpec(workDir, is);
			Map<String, Map<String, String>> schemas1 = new HashMap<String, Map<String, String>>();
			Map<?,?> parse1 = KidlParser.parseSpecExt(specFile, workDir, schemas1, null);
			Map<String, Map<String, String>> schemas2 = new HashMap<String, Map<String, String>>();
			Map<?,?> parse2 = KidlParser.parseSpecInt(specFile, workDir, schemas2);
			ok = ok & compareJson(parse1, parse2, "Parsing result for test #" + (testNum + 1));
			ok = ok & compareJsonSchemas(schemas1, schemas2, "Json schema for test #" + (testNum + 1));
		}
		Assert.assertTrue(ok);
	}

	public static boolean compareJsonSchemas(Map<String, Map<String, String>> schemas1,
			Map<String, Map<String, String>> schemas2, String header) throws IOException,
			JsonParseException, JsonMappingException, JsonGenerationException,
			Exception {
		boolean ok = true;
		Assert.assertEquals(schemas1.keySet(), schemas2.keySet());
		for (String moduleName : schemas1.keySet()) {
			Assert.assertEquals(schemas1.get(moduleName).keySet(), schemas2.get(moduleName).keySet());
			for (Map.Entry<String, String> entry : schemas1.get(moduleName).entrySet()) {
				String schema1 = rewriteJson(entry.getValue());
				String schema2 = rewriteJson(schemas2.get(moduleName).get(entry.getKey()));
				if (!schema1.equals(schema2)) {
					ok = false;
					System.out.println(header + " (" + moduleName + "." + entry.getKey() + "):");
					System.out.println("--------------------------------------------------------");
					showDiff(schema1, schema2);
					System.out.println();
					System.out.println("*");
				}
			}
		}
		return ok;
	}

	public static boolean compareJson(Map<?, ?> parse1, Map<?, ?> parse2, String header)
			throws JsonGenerationException, JsonMappingException, IOException,
			Exception {
		boolean ok = true;
		String parse1text = writeJson(parse1);
		String parse2text = writeJson(parse2);
		if (!parse1text.equals(parse2text)) {
			ok = false;
        	System.out.println(header + " (original/internal):");
        	System.out.println("--------------------------------------------------------");
        	showDiff(parse1text, parse2text);
        	System.out.println();
		}
		return ok;
	}

	/**
	 * Method sorts keys in maps inside JSON.
	 */
	public static String rewriteJson(String schema1) throws IOException, 
	JsonParseException, JsonMappingException, JsonGenerationException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		TreeNode schemaTree = mapper.readTree(schema1);
		Object schemaMap = mapper.treeToValue(schemaTree, Object.class);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		return mapper.writeValueAsString(schemaMap);
	}
	
	private static String writeJson(Object obj) 
			throws JsonGenerationException, JsonMappingException, IOException {
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.writeValue(sw, obj);
		sw.close();
		return sw.toString();
	}

	private static File prepareSpec(File workDir, String text) throws FileNotFoundException {
		File specFile = new File(workDir, "Test.spec");
		PrintWriter pw = new PrintWriter(specFile);
		pw.println(text);
		pw.close();
		return specFile;
	}

	private static File prepareSpec(File workDir, InputStream is) throws IOException {
		File specFile = new File(workDir, "Test.spec");
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
		return specFile;
	}
	
	@Test
	public void testOptionalAnnotation() throws Exception {
		File workDir = prepareWorkDir();
		File specFile = prepareSpec(workDir, "" +
				"module Test {\n" +
				"  /*\n" +
				"   @optional val2\n" +
				"   @new_annotation val1\n" +
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
		Object newAnnotation = type.getAnnotations().getUnknown().get("new_annotation");
		Assert.assertEquals("val1", ((List<?>)newAnnotation).get(0));
	}
	
	@Test
	public void testReferenceId() throws Exception {
		File workDir = prepareWorkDir();
		File specFile = prepareSpec(workDir, "" +
				"module Test {\n" +
				"  /*\n" +
				"   @id ws Test.my_struct\n" +
				"  */\n" +
				"  typedef string full_ref;\n" +
				"  \n" +
				"  /*\n" +
				"   @id ws\n" +
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
				Assert.assertEquals(KbScalar.class, typedef.getAliasType().getClass());
				KbScalar type = (KbScalar)typedef.getAliasType();
				String actualRefList = "" + type.getIdReference().getValidTypedefNamesForWs();
				String expectedRefList = typedef.getName().startsWith("full_") ? "[Test.my_struct]" : "[]";
				Assert.assertEquals(expectedRefList, actualRefList);
			}
		}
	}

	@Test
	public void testSyntaxError() throws Exception {
		File workDir = prepareWorkDir();
		File specFile = prepareSpec(workDir, "" +
				"module Test {\n" +
				"  bebebe\n" +
				"};");
		try {
			KidlParser.parseSpec(specFile, workDir, null);
			Assert.fail();
		} catch (KidlParseException ex) {
			Assert.assertTrue(ex.getMessage().contains("bebebe"));
		}
		String[][] specAndResult = {
				{ "" +
						"#include <nothing.types>\n" +
						"module Test {\n" +
						"};",
						"Can not find included spec-file"
				},
				{ "" +
						"module Test {\n" +
						"  typedef ;\n" +
						"};",
						"Encountered \" \";\" "
				},
				{ "" +
						"module Test {\n" +
						"  typedef test0 test1;\n" +
						"};",
						"Can not find type: test0"
				},
				{ "" +
						"module Test {\n" +
						"  typedef Test0.test0 test1;\n" +
						"};",
						"Can not find module: Test0"
				},
				{ "" +
						"module Test {\n" +
						"  /* @id */\n" +
						"  typedef int test1;\n" +
						"};",
						"Id annotations without type are not supported"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable */\n" +
						"  typedef int test1;\n" +
						"};",
						"without type"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable ws_subset test1 */\n" +
						"  typedef int test1;\n" +
						"};",
						"only for structures"
				},
				{ "" +
						"module Test {\n" +
						"  /* @optional val1 val2 val3 */\n" +
						"  typedef structure {\n" +
						"    int val1;\n" +
						"  } test1;\n" +
						"};",
						"[val2, val3]"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable ws_subset val2 */\n" +
						"  typedef structure {\n" +
						"    int val1;\n" +
						"  } test1;\n" +
						"};",
						"Can not match path val2 in searchable annotation to any field"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable ws_subset val1.val2 */\n" +
						"  typedef structure {\n" +
						"    int val1;\n" +
						"  } test1;\n" +
						"};",
						"structure"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable ws_subset val1.[*] */\n" +
						"  typedef structure {\n" +
						"    int val1;\n" +
						"  } test1;\n" +
						"};",
						"to a list"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable ws_subset val1.([*].val2,val3) */\n" +
						"  typedef structure {\n" +
						"    list<int> val1;\n" +
						"  } test1;\n" +
						"};",
						"val1.[*].val2 in searchable annotation to a structure"
				},
				{ "" +
						"module Test {\n" +
						"  /* @searchable ws_subset keys_of(val1) */\n" +
						"  typedef structure {\n" +
						"    int val1;\n" +
						"  } test1;\n" +
						"};",
						"to a mapping"
				},
		};
		for (int testNum = 0; testNum < specAndResult.length; testNum++) {
			specFile = prepareSpec(workDir, specAndResult[testNum][0]);
			try {
				KidlParser.parseSpec(specFile, workDir, null, null, true);
				Assert.fail();
			} catch (KidlParseException ex) {
				Assert.assertTrue("Actual message for test #" + (testNum + 1) + ": " + ex.getMessage(), 
						ex.getMessage().contains(specAndResult[testNum][1]));
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

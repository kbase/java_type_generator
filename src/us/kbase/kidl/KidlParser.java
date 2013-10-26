package us.kbase.kidl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.kbase.jkidl.IncludeProvider;
import us.kbase.jkidl.SpecParser;
import us.kbase.jkidl.StaticIncludeProvider;

public class KidlParser {

	public static List<KbService> parseSpec(File specFile, File tempDir) throws KidlParseException {
		return parseSpec(specFile, tempDir, null);
	}

	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn) throws KidlParseException {
		return parseSpec(specFile, tempDir, modelToTypeJsonSchemaReturn, null);
	}

	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, String kbTop) throws KidlParseException {
		return parseSpec(specFile, tempDir, modelToTypeJsonSchemaReturn, kbTop, false);
	}
	
	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, String kbTop, boolean internal) throws KidlParseException {
		File workDir = null;
		try {
			Map<?,?> map = null;
			if (internal) {
		        SpecParser p = new SpecParser(new DataInputStream(new FileInputStream(specFile)));
		        IncludeProvider ip = new StaticIncludeProvider();
		        map = SpecParser.parseAsJson(p, ip);
			} else {
			if (tempDir == null)
				tempDir = new File(".");
			workDir = new File(tempDir, "temp_" + System.currentTimeMillis());
			workDir.mkdir();
			File bashFile = new File(workDir, "comp_server.sh");
			File specDir = specFile.getAbsoluteFile().getParentFile();
			File xmlFile = new File(workDir, "parsing_file.xml");
			if (kbTop == null)
				kbTop = System.getenv("KB_TOP");
			String compileTypespecDir = "";
			if (kbTop != null && kbTop.trim().length() > 0) {
				compileTypespecDir = kbTop + "/bin/";
			} else {
				System.out.println("WARNING: KB_TOP environment variable is not defined, " +
						"so compile_typespec is supposed to be in PATH");
			}
			PrintWriter pw = new PrintWriter(bashFile);
			pw.println("#!/bin/bash");
			boolean createJsonSchemas = modelToTypeJsonSchemaReturn != null;
			pw.println("" +
					compileTypespecDir + "compile_typespec --path \"" + specDir.getAbsolutePath() + "\"" +
					" --xmldump " + xmlFile.getName() + " " + (createJsonSchemas ? "--jsonschema " : "") + 
					"\"" + specFile.getAbsolutePath() + "\" " + workDir.getName()
					);
			pw.close();
			Process proc = new ProcessBuilder("bash", bashFile.getCanonicalPath()).directory(tempDir)
					.redirectErrorStream(true).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder errTextSB = new StringBuilder();
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				System.out.println("KIDL: " + l);
				errTextSB.append(l).append('\n');
			}
			br.close();
			proc.waitFor();
			if (!xmlFile.exists()) {
				String errText = errTextSB.toString();
				String[] options = {"path", "xmldump", "jsonschema"};
				String caption = null;
				for (String opt : options) {
					if (errText.contains("Unknown option: " + opt)) {
						caption = "It seems that you're using wrong branch of \"typecomp\" module (it should be \"dev-prototypes\")";
						break;
					}
				}
				if (caption == null)
					caption = "Parsing file wasn't created";
				throw new KidlParseException(caption + ", here is KIDL output:\n" + errText);
			}
			map = SpecXmlHelper.parseXml(xmlFile);
			//			
			if (createJsonSchemas) {
				File schemasRoot = new File(workDir, "jsonschema");
				if (schemasRoot.exists())
					for (File moduleDir : schemasRoot.listFiles()) {
						if (!moduleDir.isDirectory())
							continue;
						Map<String, String> type2schema = new HashMap<String, String>();
						for (File schemaFile : moduleDir.listFiles()) {
							if (!schemaFile.getName().endsWith(".json"))
								continue;
							String typeName = schemaFile.getName();
							typeName = typeName.substring(0, typeName.length() - 5);
							StringWriter sw = new StringWriter();
							PrintWriter schemaPw = new PrintWriter(sw);
							BufferedReader schemaBr = new BufferedReader(new FileReader(schemaFile));
							while (true) {
								String l = schemaBr.readLine();
								if (l == null)
									break;
								schemaPw.println(l);
							}
							schemaBr.close();
							schemaPw.close();
							type2schema.put(typeName, sw.toString());
						}
						modelToTypeJsonSchemaReturn.put(moduleDir.getName(), type2schema);
					}
			}
			}
			return KbService.loadFromMap(map);
		} catch (KidlParseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KidlParseException("Error during parsing spec-file: " + ex.getMessage(), ex);
		} finally {
			if (workDir != null)
				deleteRecursively(workDir);			
		}
	}
	
	private static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory())
			for (File f : fileOrDir.listFiles()) 
				deleteRecursively(f);
		fileOrDir.delete();
	}
}

package us.kbase.kidl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class KidlParser {

	public static List<KbService> parseSpec(File specFile, File tempDir) throws Exception {
		if (tempDir == null)
			tempDir = new File(".").getCanonicalFile();
		File workDir = new File(tempDir, "temp_" + System.currentTimeMillis());
		workDir.mkdir();
		File bashFile = new File(workDir, "comp_server.sh");
		File specDir = specFile.getAbsoluteFile().getParentFile();
		File xmlFile = new File(workDir, "parsing_file.xml");
		String kbTop = System.getenv("KB_TOP");
		String compileTypespecDir = "";
		if (kbTop != null && kbTop.trim().length() > 0) {
			compileTypespecDir = kbTop + "/bin/";
		} else {
			System.out.println("WARNING: KB_TOP environment variable is not defined, so compile_typespec is supposed to be in PATH");
		}
		try {
			PrintWriter pw = new PrintWriter(bashFile);
			pw.println("#!/bin/bash");
			pw.println(
					compileTypespecDir + "compile_typespec --path \"" + specDir.getAbsolutePath() + "\"" +
					" --xmldump " + xmlFile.getName() + " \"" + specFile.getAbsolutePath() + "\" " + workDir.getName()
					);
			pw.close();
			Process proc = new ProcessBuilder("bash", bashFile.getCanonicalPath()).directory(tempDir)
					.redirectErrorStream(true).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				System.out.println("KIDL: " + l);
			}
			br.close();
			proc.waitFor();
			if (!xmlFile.exists())
				throw new IllegalStateException("Parsing file wasn't created, see error lines above for detailes");
			Map<?,?> map = SpecXmlHelper.parseXml(xmlFile);
			JSyncProcessor subst = new JSyncProcessor(map);
			return KbService.loadFromMap(map, subst);
		} finally {
			deleteRecursively(workDir);			
		}
	}
	
	public static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory())
			for (File f : fileOrDir.listFiles()) 
				deleteRecursively(f);
		fileOrDir.delete();
	}
}

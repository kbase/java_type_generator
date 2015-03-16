package us.kbase.scripts;

import java.io.File;
import java.io.PrintStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;

public class ModuleBuilder {
    private static final String defaultParentPackage = "us.kbase";

    public static void main(String[] args) throws Exception {
        Args a = new Args();
        CmdLineParser parser = new CmdLineParser(a);
        parser.setUsageWidth(100);
        if (args.length == 0 || (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help")))) {
            parser.parseArgument("no.spec");
            showUsage(parser, null, System.out);
            return;
        }
        try {
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            showError(parser, e.getMessage());
            return;
        }
        File outDir = a.outDir == null ? new File(".") : new File(a.outDir);
        outDir = outDir.getCanonicalFile();
        if (!outDir.exists())
            outDir.mkdirs();
        URL url = null;
        if (a.url != null) {
            try {
                url = new URL(a.url);
            } catch (MalformedURLException mue) {
                showError(parser, "The provided url " + a.url + " is invalid.");
                return;
            }
        }
        List<KbService> services = KidlParser.parseSpec(a.specFile, null, null, null, true);
        if (a.javaServerSide)
            a.javaClientSide = true;
        if (a.javaGwtPackage != null)
            a.javaClientSide = true;
        if (a.javaClientSide) {
            if (a.javaBuildXml != null) {
                showError(parser, "Unfortunately parameter -javabuildxml is not yet supported.");
                return;
            }
            File srcOutDir = new File(a.javaSrcDir);
            if (!srcOutDir.isAbsolute())
                srcOutDir = new File(outDir, a.javaSrcDir);
            JavaTypeGenerator.processSpec(services, srcOutDir, a.javaPackageParent, 
                    a.javaServerSide, null, a.javaGwtPackage, url);
        }
        boolean enableRetries = a.perlEnableRetries;
        FileIncludeProvider ip = new FileIncludeProvider(a.specFile.getCanonicalFile().getParentFile());
        FileSaver output = new DiskFileSaver(outDir);
        TemplateBasedGenerator.generate(services, a.url, a.jsClientSide, a.jsClientName, 
                a.perlClientSide, a.perlClientName, a.perlServerSide, a.perlServerName, 
                a.perlImplName, a.perlPsgiName, a.pyClientSide, a.pyClientName, 
                a.pyServerSide, a.pyServerName, a.pyImplName, enableRetries, true, 
                ip, output);
    }

    public static void generate(File specFile, String defaultUrl, 
            boolean genJs, String jsClientName,
            boolean genPerl, String perlClientName, boolean genPerlServer, 
            String perlServerName, String perlImplName, String perlPsgiName, 
            boolean genPython, String pythonClientName, boolean genPythonServer,
            String pythonServerName, String pythonImplName, boolean enableRetries, 
            boolean newStyle, File outDir) throws Exception {
        FileSaver output = new DiskFileSaver(outDir);
        FileIncludeProvider ip = new FileIncludeProvider(specFile.getCanonicalFile().getParentFile());
    }

    public static void generate(Reader specFile, String url, boolean jsClientSide, 
            String jsClientName, boolean perlClientSide, String perlClientName, 
            boolean perlServerSide, String perlServerName, String perlImplName, 
            String perlPsgiName, boolean perlEnableRetries, boolean pyClientSide, 
            String pyClientName, boolean pyServerSide, String pyServerName, String pyImplName,
            boolean javaClientSide, boolean javaServerSide, String javaPackageParent,
            String  javaSrcDir, String javaLibDir, String  javaBuildXml, String javaGwtPackage,
            boolean newStyle, IncludeProvider ip, FileSaver output) throws Exception {
        List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(specFile, null, ip));
        if (javaServerSide)
            javaClientSide = true;
        if (javaGwtPackage != null)
            javaClientSide = true;
        if (javaClientSide) {
            if (javaBuildXml != null) {
                throw new IllegalStateException("Unfortunately parameter -javabuildxml is not yet supported.");
            }
            File srcOutDir = new File(javaSrcDir);
            if (!srcOutDir.isAbsolute())
                srcOutDir = new File(".", javaSrcDir);
            JavaTypeGenerator.processSpec(services, srcOutDir, javaPackageParent, 
                    javaServerSide, null, javaGwtPackage, new URL(url));
        }
        TemplateBasedGenerator.generate(services, url, jsClientSide, jsClientName, 
                perlClientSide, perlClientName, perlServerSide, perlServerName, 
                perlImplName, perlPsgiName, pyClientSide, pyClientName, 
                pyServerSide, pyServerName, pyImplName, perlEnableRetries, true, 
                ip, output);
    }

    private static void showError(CmdLineParser parser, String message) {
        showUsage(parser, message, System.err);
    }

    private static void showUsage(CmdLineParser parser, String message, PrintStream out) {
        if (message != null)
            out.println(message);
        out.println("Program generates java client and server classes for JSON RPC calls.");
        out.println("Usage: <program> [options...] <spec-file>");
        out.println("Usage: <program> {-h|--help}     - to see this help");
        parser.printUsage(out);
    }

    public static class Args {
        @Option(name="-out",usage="Common output folder (instead of default . folder) which will be used for all relative paths", 
                metaVar="<out-dir>")
        String outDir = null;
        
        @Option(name="-js", usage="Defines whether or not java-script code for client side should be created, " +
                "default value is false, use -js for true")
        boolean jsClientSide = false;

        @Option(name="-jsclname", usage="JavaScript client name (if defined then -js will be treated as true automatically)", 
                metaVar = "<js-client-name>")
        String jsClientName = null;

        @Option(name="-perl", usage="Defines whether or not perl code for client side should be created, " +
                "default value is false, use -perl for true")
        boolean perlClientSide = false;

        @Option(name="-perlclname", usage="Perl client name including prefix with module subfolders separated " +
        		"by :: if necessary (if defined then -perl will be treated as true automatically)", 
                metaVar = "<perl-client-name>")
        String perlClientName = null;

        @Option(name="-perlsrv", usage="Defines whether or not perl code for server side should be created, " +
                "default value is false, use -perlsrv for true (if defined then -perl will be treated as true " +
                "automatically)")
        boolean perlServerSide = false;

        @Option(name="-perlsrvname", usage="Perl server name including prefix with module subfolders separated " +
                "by :: if necessary (if defined then -perlsrv will be treated as true automatically)", 
                metaVar = "<perl-server-name>")
        String perlServerName = null;

        @Option(name="-perlimplname", usage="Perl impl name including prefix with module subfolders separated " +
                "by :: if necessary (if defined then -perlsrv will be treated as true automatically)", 
                metaVar = "<perl-impl-name>")
        String perlImplName = null;

        @Option(name="-perlpsginame", usage="Perl PSGI name (if defined then -perlsrv will be treated as true " +
        		"automatically)", 
                metaVar = "<perl-psgi-name>")
        String perlPsgiName = null;

        @Option(name="-perlenableretries", usage="Defines whether or not perl code for client side should include " +
                "reconnection retries, default value is false")
        boolean perlEnableRetries = false;

        @Option(name="-py", usage="Defines whether or not python code for client side should be created, " +
                "default value is false, use -py for true")
        boolean pyClientSide = false;

        @Option(name="-pyclname", usage="Python client name including prefix with module subfolders separated " +
                "by . if necessary (if defined then -py will be treated as true automatically)", 
                metaVar = "<py-client-name>")
        String pyClientName = null;

        @Option(name="-pysrv", usage="Defines whether or not python code for server side should be created, " +
                "default value is false, use -pysrv for true (if defined then -py will be treated as true " +
                "automatically)")
        boolean pyServerSide = false;

        @Option(name="-pysrvname", usage="Python server name including prefix with module subfolders separated " +
                "by . if necessary (if defined then -perlsrv will be treated as true automatically)", 
                metaVar = "<py-server-name>")
        String pyServerName = null;

        @Option(name="-pyimplname", usage="Python impl name including prefix with module subfolders separated " +
                "by . if necessary (if defined then -perlsrv will be treated as true automatically)", 
                metaVar = "<py-impl-name>")
        String pyImplName = null;

        @Option(name="-java", usage="Defines whether or not java code for client side should be created, " +
                "default value is false, use -java for true (if defined then 'src' default value is used for " +
                "-javasrc if it's not overwritten explicitly)")
        boolean javaClientSide = false;

        @Option(name="-javasrc",usage="Source output folder (if defined then -java will be treated as true " +
                "automatically)", metaVar="<java-src-dir>")
        String javaSrcDir = "src";

        @Option(name="-javabuildxml",usage="Template for ant build file", metaVar="<java-build-xml>")
        String javaBuildXml;
        
        @Option(name="-url", usage="Default url for service", metaVar="<url>")
        String url = null;

        @Option(name="-javapackage",usage="Java package parent (module subpackages are created in this package), " +
        		"default value is " + defaultParentPackage, metaVar="<java-package>")      
        String javaPackageParent = defaultParentPackage;

        @Option(name="-javasrv", usage="Defines whether or not java code for server side should be created, " +
        		"default value is false, use -javasrv for true (if defined then -java will be treated as true " +
                "automatically)")
        boolean javaServerSide = false;

        @Option(name="-javagwt",usage="Gwt client java package (define it in case you need copies of generated " +
        		"classes for GWT client)", metaVar="<java-gwt-pckg>")     
        String javaGwtPackage = null;

        @Argument(metaVar="<spec-file>",required=true,usage="File *.spec for compilation into java classes")
        File specFile;
    }

}

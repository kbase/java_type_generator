package us.kbase.templates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class TemplateFormatter {

    public static boolean formatTemplate(String templateName, Map<?,?> context, 
            File output) throws IOException {
        FileWriter fw = new FileWriter(output);
        try {
            return formatTemplate(templateName, context, fw);
        } finally {
            fw.close();
        }
    }
    
    public static boolean formatTemplate(String templateName, Map<?,?> context, Writer output) {
        try {
            Reader input = new InputStreamReader(TemplateFormatter.class.getResourceAsStream(
                    templateName + ".vm.properties"), Charset.forName("utf-8"));
            VelocityContext cntx = new VelocityContext(context);
            boolean ret = Velocity.evaluate(cntx, output, "Template " + templateName, input);
            input.close();
            output.flush();
            return ret;
        } catch (Exception ex) {
            throw new IllegalStateException("Problems with template evaluation (" + templateName + ")", ex);
        }
    }
}

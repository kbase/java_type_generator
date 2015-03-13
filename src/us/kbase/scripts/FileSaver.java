package us.kbase.scripts;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public interface FileSaver {
    public Writer openWriter(String path) throws IOException;
    public File getAsFileOrNull(String path) throws IOException;
}
